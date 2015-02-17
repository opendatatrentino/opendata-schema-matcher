package eu.trentorise.opendata.schemamatcher.implementation.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

public class TestSimpleSchemaMatcher {


	private final static Logger LOGGER = Logger.getLogger(TestSchemaImport.class.getName());
	private EntityType etype;

	@Before
	public void readEtype(){
		EntityTypeService ets = new EntityTypeService();
		String etypeUrl = WebServiceURLs.etypeIDToURL(12L);
		etype= (EntityType) ets.readEntityType(etypeUrl);
	}


	@Test
	public void testSchemaElementMatcher() throws IOException, SchemaMatcherException{
		SchemaImport si = new SchemaImport();
		//File file = new File("impianti risalita.csv");
		File file = new File("impianti risalita.csv");

		ISchema schemaCSV= si.parseCSV(file);
		ISchema schemaEtype=si.extractSchema(etype, Locale.ITALIAN);

		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("Simple");
		ISchemaCorrespondence  schemaCor =schemaMatcher.matchSchemas(schemaCSV, schemaEtype, "ConceptDistanceBased");
		
		LOGGER.info("Schema Cor Score: "+schemaCor.getSchemaCorrespondenceScore());

	}

	@Test
	public void testConceptFromText(){

		String resourceName = "IMPIANTI RISALITA";
		Long conceptID = ColumnRecognizer.conceptFromText(resourceName);


	}


	@Test
	public void testSchemaElementMatcherAllEtypes() throws IOException, SchemaMatcherException{
		SchemaImport si = new SchemaImport();
		//		File file = new File("/home/ivan/Downloads/Elenco_osterie_tipiche_civici.1386925759.csv");
		File file = new File("impianti risalita.csv");
		//		File file = new File("impianti risalita.csv");

		ISchema schemaCSV= si.parseCSV(file);
		List<ISchema> sourceSchemas = new ArrayList<ISchema>();
		sourceSchemas.add(schemaCSV);

		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypeList = etypeService.getAllEntityTypes();
		List<ISchema> targetSchemas = new ArrayList<ISchema>();

		for (IEntityType etype:etypeList){

			ISchema schemaEtype=si.extractSchema(etype, Locale.ITALIAN);
			targetSchemas.add(schemaEtype);

		}

		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("HungarianAllocationAndEditDistance");
		List<ISchemaCorrespondence>  schemaCor =schemaMatcher.matchSchemas(sourceSchemas, targetSchemas, "EditDistanceBased");

		for (ISchemaCorrespondence c : schemaCor){
			LOGGER.info("Etype name: "+c.getTargetSchema().getSchemaName()+ " "+c.getSchemaCorrespondenceScore() );
			List<ISchemaElementCorrespondence> sElCors = c.getSchemaElementCorrespondence();

			//			for (ISchemaElementCorrespondence sel: sElCors){
			//				LOGGER.info("Source-Target Element: "+sel.getSourceElement().getElementContext().getElementName()+":"+sel.getSourceElement().getElementContext().getElementConcept()+"-");
			//				LOGGER.info(sel.getTargetElement().getElementContext().getElementName()+":"+sel.getTargetElement().getElementContext().getElementConcept() + " Score: "+sel.getElementCorrespondenceScore());
			//				for(ISchemaElement key : sel.getElementMapping().keySet())
			//				{
			//					LOGGER.info("NameList: "+key.getElementContext().getElementName()+"++"+key.getElementContext().getElementConcept());
			//					LOGGER.info(" ScoreList: "+ sel.getElementMapping().get(key));
			//
			//				}
			//				LOGGER.info("-------------------------------------------");
			//
			//			}

		}


	}

	@Test 
	public void testConsistanceOfColConcRecognizer() throws IOException{
		SchemaImport si = new SchemaImport();

		File file = new File("impianti risalita.csv");
		//		File file = new File("impianti risalita.csv");

		ISchema schemaCSV= si.parseCSV(file);
		List<ISchema> sourceSchemas = new ArrayList<ISchema>();
		sourceSchemas.add(schemaCSV);

		List<ISchemaElement> schemaElements = schemaCSV.getSchemaElements();
		List<String> elementNames = new ArrayList<String>();
		List<List<String>> elementContent = new ArrayList<List<String>>();
		
		HashMap<Integer,String> map = new HashMap<Integer,String>();
		int z=0;
		for (ISchemaElement element: schemaElements){
			z++;
			map.put( z,element.getElementContext().getElementName());
			elementNames.add(element.getElementContext().getElementName());
			List<Object> content = element.getElementContent().getContent();
			List<String> contStr = new ArrayList<String>();
			for(Object o: content){
				contStr.add(o.toString());
			}
			elementContent.add(contStr);
		}

			List<ColumnConceptCandidate> extractedConcepts = new ArrayList<ColumnConceptCandidate>();
		
			extractedConcepts =
					ColumnRecognizer.computeScoredCandidates(elementNames, elementContent);
			List<Long> cid = ColumnRecognizer.computeColumnConceptIDs(elementNames, elementContent);
			
//			List<ISchemaElement> schemaElementsOut = new ArrayList<ISchemaElement>();
//			int i=0;
		for (Long id: cid){
			System.out.println(id);
			
		}
			for(ColumnConceptCandidate ccc: extractedConcepts){

				System.out.println(" Name: "+map.get(ccc.getColumnNumber())+" concept id: "+ccc.getConceptID()+" col num: " +ccc.getColumnNumber());
				//Long conceptId = extractedConcepts.get(i).getConceptID();
		//	}
		}

	}			


}
