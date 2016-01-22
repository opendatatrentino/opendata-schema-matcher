package eu.trentorise.opendata.schemamatcher.implementation.services;

import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Entity;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;
import it.unitn.disi.sweb.webapi.model.kb.types.DataType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.nlprise.DataTypeGuess;
import eu.trentorise.opendata.nlprise.DataTypeGuess.Datatype;
import eu.trentorise.opendata.schemamatcher.implementation.model.ElementContent;
import eu.trentorise.opendata.schemamatcher.implementation.model.ElementContext;
import eu.trentorise.opendata.schemamatcher.implementation.model.Schema;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElement;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.model.IResource;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.services.importing.ISchemaImport;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;

/** 
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public class SchemaImport implements ISchemaImport{

	private final static Logger logger = Logger.getLogger(SchemaImport.class.getName());

	private final static int PAGE_SIZE=100;

	public ISchema extractSchema(File file) throws SchemaMatcherException {
		// TODO check supported format
		if(file==null){
			throw new SchemaMatcherException("Null input is provided!");
		}

		ISchema schemaOut = null;
		try {
			schemaOut = parseCSV(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return schemaOut;
	}

	/** The method parse schema from a *.csv file. 
	 * 
	 * @param input *.csv file
	 * @return Schema object 
	 * @throws IOException
	 */
	public ISchema parseCSV(File file) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(file));
		//String [] nextLine;
		List<String[]> strings = reader.readAll();
		String[] str = strings.get(0);
		List<ISchemaElement> sElements = new ArrayList<ISchemaElement>();
		for (int i=0; i<str.length;i++){
			SchemaElement schemaElement = new SchemaElement();
			ElementContext sContext=new ElementContext();
			sContext.setElementName(str[i]);

			ElementContent elContent = new ElementContent();
			List<Object> elInstances = new ArrayList<Object>();

			int lineNumber=1;

			//for(String[] sArr: strings )
			for(int k=1; k<strings.size(); k++){
				elInstances.add(strings.get(k)[i]);				
				lineNumber++;
			}
			if(lineNumber==1){
				//	System.out.println(lineNumber);
			}
			elContent.setContentSize(lineNumber);
			elContent.setContent(elInstances);

			String datatype = extractDataType(elContent);
			//logger.info("Extracted datatype is: " + datatype);
			sContext.setElemetnDataType(datatype);

			schemaElement.setElementContext(sContext);
			schemaElement.setElementContent(elContent);

			sElements.add(schemaElement);

		}
		Schema schema = new Schema();
		schema.setSchemaElements(sElements);

		String fileName = file.getName().replaceFirst("[.][^.]+$", "");

		long conceptId=ColumnRecognizer.conceptFromText(fileName);
		schema.setSchemaConcept(conceptId);
		schema.setSchemaName(fileName);
		return schema;
	}	
	/* (non-Javadoc)
	 * @see eu.trentorise.schemamatcher.services.importing.ISchemaImport#extractSchema(java.lang.Object)
	 */
	public ISchema extractSchema(Object schema, Locale locale) throws SchemaMatcherException {

		if(schema==null){
			throw new SchemaMatcherException("Null input is provided!");
		}
		if(schema instanceof IResource){

			IResource resource = (IResource) schema;
			logger.log(Level.INFO,  resource.getTableResource().getHeaders());
			ISchema schemaOut= extractFromODRResource(resource.getResourceContext(), resource.getTableResource());
			return schemaOut;
		}
		if ((schema instanceof IEntityType)){
			IEntityType etype = (IEntityType) schema;

			Schema schemaOut = new Schema();
			String name = etype.getName().getString(locale);
			schemaOut.setSchemaName(name);
			schemaOut.setEtype(etype);
			List<ISchemaElement>	schemaElements=null;
			if(etype.getAttributeDefs().size()!=0){
				schemaElements=extractSchemaElements(etype, locale);
			}
			else {
				schemaElements=new ArrayList<ISchemaElement>();	
			}

			ConceptODR codr = new ConceptODR();
			codr = new KnowledgeService().readConceptGlobalID(etype.getConcept().getGUID());
			long globalConceptID =codr.getId();
			schemaOut.setSchemaConcept(globalConceptID);
			schemaOut.setSchemaElements(schemaElements);
			return schemaOut;
		}
		else
			throw new SchemaMatcherException("Unsupported schema format!");

	}

	/** Method extracts schema from ODR IResource 
	 * @param resourceContext
	 * @param tableResource
	 * @return object of Schema
	 */
	private ISchema extractFromODRResource( IResourceContext resourceContext,ITableResource tableResource){
		Schema schema= new Schema();	

		schema.setSchemaName(resourceContext.getResourceName());
		List<ISchemaElement> selements = new ArrayList<ISchemaElement>();

		List<String> elNames= tableResource.getHeaders();
		int tableSize = tableResource.getHeaders().size();
		for(int i=0; i<tableSize; i++){
			SchemaElement sElement = new SchemaElement();
			ElementContext elContext = new ElementContext();
			ElementContent elContent = new ElementContent();
			
			List<String> tabInstances = tableResource.getColumns().get(i);
			List<Object> instances = new ArrayList<Object>();
			for(String st : tabInstances)
			{
				instances.add(st);
			}
			elContent.setContent(instances);
			elContext.setElementName(elNames.get(i));
			sElement.setElementContext(elContext);
			sElement.setElementContent(elContent);
			selements.add(sElement);
		}
		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
		// Assignment concepts for schema
		for(ISchemaElement el : selements)
		{
			logger.log(Level.INFO, "El name: "+el.getElementContext().getElementName());
		}
		selements = sefe.runColumnRecognizer(selements);
		//		for(ISchemaElement sel:  selements){
		//			System.out.println("Name: "+sel.getElementContext().getElementName()+" Concept: "
		//					+sel.getElementContext().getElementConcept());
		//		}
		schema.setSchemaElements(selements);
		long schemaConceptID=ColumnRecognizer.conceptFromText(resourceContext.getResourceName());
		schema.setSchemaConcept(schemaConceptID);

		return schema;
	}



	/** Method extracts schema elements from Etype
	 * @param etype input Etype
	 * @param locale is used to return schema element names in the required language 
	 * @return list of schema elements
	 */
	private List<ISchemaElement> extractSchemaElements(IEntityType etype,Locale locale){
		List<IAttributeDef> attrDefs = etype.getAttributeDefs();
		List<ISchemaElement> schemaElements = new ArrayList<ISchemaElement>();
		getSchemaElements(etype, schemaElements, attrDefs,  locale, 0 );
		return schemaElements;
	}


	/** 
	 * @param etype
	 * @param schemaElements
	 * @param attrDefs
	 * @param locale
	 */
	private void getSchemaElements(IEntityType etype, List<ISchemaElement> schemaElements, List<IAttributeDef> attrDefs, Locale locale, int depth ){
		List<Instance> instances = getEntities(etype); 
		for (IAttributeDef atrDef: attrDefs){
			SchemaElement schemaElement = new SchemaElement();

			ElementContext elContext = new ElementContext();
			ElementContent elContent = new ElementContent();
			//context extraction
			schemaElement.setAttrDef(atrDef);
			elContext.setElementName(atrDef.getName().getString(locale));
			String dataType = atrDef.getDataType();
			elContext.setElemetnDataType(dataType);
			//convert local concept id to a global one
			elContext.setElementConcept(WebServiceURLs.urlToConceptID(atrDef.getConceptURL()));
			schemaElement.setElementContext(elContext);
			//schema element relation extraction
			//TODO extract the schema structure

			//schema element's content extraction
			//TODO find the way to download best representing content
			List<Object> values = new ArrayList<Object>();
			if (instances!=null){
				if((atrDef.getDataType().equalsIgnoreCase("xsd:float"))||(atrDef.getDataType().equalsIgnoreCase("xsd:integer"))
						||(atrDef.getDataType().equalsIgnoreCase("xsd:long"))){
					values = getValues(instances, atrDef);
				}
			}
			elContent.setContent(values);
			schemaElement.setElementContent(elContent);
			schemaElements.add(schemaElement);
			if(atrDef.getDataType().equalsIgnoreCase("oe:structure"))
			{
				EntityTypeService ets = new EntityTypeService();
				IEntityType structureEtype= (EntityType) ets.readEntityType(atrDef.getRangeEtypeURL());
				List<IAttributeDef> strAttrDefs = structureEtype.getAttributeDefs();
				  if (depth<2) {
				getSchemaElements(structureEtype, schemaElements, strAttrDefs, locale, ++depth);
				  }
			}
		}
	}
	
	private List<Object> getValues(List<Instance> instances,
			IAttributeDef atrDef) {
		List<Object> objects = new ArrayList<Object>();
		for(Instance in: instances){
			List<Attribute> attrs = in.getAttributes();

			for(Attribute a : attrs){
				if(a.getConceptId()==WebServiceURLs.urlToConceptID(atrDef.getConceptURL()))
					objects.add(a.getValues().iterator().next().getValue());
				//System.out.println(a.getDataType());
				//	System.out.println(a.getValues().iterator().next().getValue().toString());

			}
		}

		return objects;
	}

	/** methods takes random entities from Entitypedia
	 * @param etype
	 * @return
	 */
	public List<Instance> getEntities(IEntityType etype) {

		InstanceClient insClient = new InstanceClient(WebServiceURLs.getClientProtocol());
		Long etypeId = WebServiceURLs.urlToEtypeID(etype.getURL());
		Pagination page = new Pagination();

		//		Random rand = new Random();
		//		int pageIndex = rand.nextInt((1000 - 1) + 1) + 1; //random number from 1 to 1000
		//	page.setPageIndex(10);
		page.setPageSize(PAGE_SIZE);

		List<Instance> instances = 	insClient.readInstances(1L, etypeId, null, null, page); // TODO Make sure that they are taken randomly
		List<Long> instancesIds =  new ArrayList<Long>();

		for (Instance in : instances){
			instancesIds.add(in.getId());
		}

		InstanceFilter filter = new InstanceFilter();
		filter.setIncludeAttributes(true);
		if(instancesIds.size()!=0){
			List<Instance> instancesFull = insClient.readInstancesById(instancesIds, filter);
			//			for(Instance in: instancesFull){
			//				List<Attribute> attrs = in.getAttributes();
			//
			//				for(Attribute a : attrs){
			//					if(a.getDataType().equals(DataType.FLOAT))
			//						//System.out.println(a.getDataType());
			//						System.out.println(a.getValues().iterator().next().getValue().toString());
			//				}
			//			}
			return instancesFull;
		} else 
			return null;

	}

	public String extractDataType(ElementContent elContent){
		//TODO think about best content for representation
		//		if(elContent.getContentSize()==1){
		//			System.out.println("Size: "+elContent.getContentSize());
		//
		//			System.out.println("1st element: "+elContent.getContent().get(0));
		//		}
		String contentToGuess = elContent.getContent().iterator().next().toString();
		//TODO think about enum of possible datatypes for the matcher

		Datatype datatype = TypeDetector.guessType(contentToGuess);
		return	datatype.toString();
	}

}
