package eu.trentorise.opendata.schemamatcher.implementation.services;

import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.nlprise.DataTypeGuess.Datatype;
import eu.trentorise.opendata.schemamatcher.implementation.model.ElementContent;
import eu.trentorise.opendata.schemamatcher.implementation.model.ElementContext;
import eu.trentorise.opendata.schemamatcher.implementation.model.ElementRelation;
import eu.trentorise.opendata.schemamatcher.implementation.model.Schema;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElement;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.model.IElementRelation;
import eu.trentorise.opendata.schemamatcher.model.IResource;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.services.importing.ISchemaImport;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;

/**
 * Contains logic for import from *.csv files, EntityTypes and ODR's IResource
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public class SchemaImport implements ISchemaImport {

    private static final int RECURSION_DEPTH = 3;

    private final static Logger logger = Logger.getLogger(SchemaImport.class.getName());

    private final static int PAGE_SIZE = 100;

    public ISchema extractSchema(File file) throws SchemaMatcherException {
        // TODO check supported format
        if (file == null) {
            throw new SchemaMatcherException("Null input is provided!");
        }
        ISchema schemaOut = null;
        try {
            schemaOut = parseCSV(file);
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        return schemaOut;
    }

    /**
     * The method parse schema from a *.csv file.
     *
     * @param input *.csv file
     * @return Schema object
     * @throws IOException
     */
    public ISchema parseCSV(File file) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file));
        List<String[]> strings = reader.readAll();
        String[] str = strings.get(0);
        List<ISchemaElement> sElements = new ArrayList<ISchemaElement>();
        for (int i = 0; i < str.length; i++) {
            SchemaElement schemaElement = new SchemaElement();
            ElementContext sContext = new ElementContext();
            sContext.setElementName(str[i]);

            ElementContent elContent = new ElementContent();
            List<Object> elInstances = new ArrayList<Object>();

            int lineNumber = 1;

            for (int k = 1; k < strings.size(); k++) {
                elInstances.add(strings.get(k)[i]);
                lineNumber++;
            }
            elContent.setContentSize(lineNumber);
            elContent.setContent(elInstances);

            String datatype = extractDataType(elContent);
            sContext.setElemetnDataType(datatype);

            schemaElement.setElementContext(sContext);
            schemaElement.setElementContent(elContent);

            sElements.add(schemaElement);

        }
        Schema schema = new Schema();
        schema.setSchemaElements(sElements);

        String fileName = file.getName().replaceFirst("[.][^.]+$", "");

        long conceptId = ColumnRecognizer.conceptFromText(fileName);
        schema.setSchemaConcept(conceptId);
        schema.setSchemaName(fileName);
        reader.close();
        return schema;
    }

    @SuppressWarnings("deprecation")
    public ISchema extractSchema(Object schema, Locale locale) throws SchemaMatcherException {

        if (schema == null) {
            throw new SchemaMatcherException("Null input is provided!");
        }
        if (schema instanceof IResource) {

            IResource resource = (IResource) schema;
            logger.log(Level.INFO, resource.getTableResource().getHeaders());
            return extractFromODRResource(resource.getResourceContext(), resource.getTableResource());
        }
        if ((schema instanceof IEntityType)) {
            IEntityType etype = (IEntityType) schema;

            Schema schemaOut = new Schema();
            String name = etype.getName().string(locale);
            schemaOut.setSchemaName(name);
            schemaOut.setEtype(etype);
            List<ISchemaElement> schemaElements = null;
            if (etype.getAttributeDefs().size() != 0) {
                schemaElements = extractSchemaElements(etype, locale);
            } else {
                schemaElements = new ArrayList<ISchemaElement>();
            }
            ConceptODR codr = new ConceptODR();
            codr = codr.readConceptGlobalID(etype.getConcept().getGUID());
            long globalConceptID = codr.getId();
            schemaOut.setSchemaConcept(globalConceptID);
            schemaOut.setSchemaElements(schemaElements);
            return schemaOut;
        } else {
            throw new SchemaMatcherException("Unsupported schema format!");
        }
    }

    /**
     * Method extracts schema from ODR IResource
     *
     * @param resourceContext
     * @param tableResource
     * @return object of Schema
     */
    private ISchema extractFromODRResource(IResourceContext resourceContext, ITableResource tableResource) {
        Schema schema = new Schema();

        schema.setSchemaName(resourceContext.getResourceName());
        List<ISchemaElement> selements = new ArrayList<ISchemaElement>();

        List<String> elNames = tableResource.getHeaders();
        int tableSize = tableResource.getHeaders().size();
        for (int i = 0; i < tableSize; i++) {
            SchemaElement sElement = new SchemaElement();
            ElementContext elContext = new ElementContext();
            ElementContent elContent = new ElementContent();

            List<String> tabInstances = tableResource.getColumns().get(i);
            List<Object> instances = new ArrayList<Object>();
            for (String st : tabInstances) {
                instances.add(st);
            }
            elContent.setContent(instances);
            elContext.setElementName(elNames.get(i));
            sElement.setElementContext(elContext);
            sElement.setElementContent(elContent);
            selements.add(sElement);
        }
        SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
        selements = sefe.runColumnRecognizer(selements);
        schema.setSchemaElements(selements);
        long schemaConceptID = ColumnRecognizer.conceptFromText(resourceContext.getResourceName());
        schema.setSchemaConcept(schemaConceptID);

        return schema;
    }

    /**
     * Method extracts schema elements from Etype
     *
     * @param etype input Etype
     * @param locale is used to return schema element names in the required
     * language
     * @return list of schema elements
     */
    private List<ISchemaElement> extractSchemaElements(IEntityType etype, Locale locale) {
        List<IAttributeDef> attrDefs = etype.getAttributeDefs();
        List<ISchemaElement> schemaElements = new ArrayList<ISchemaElement>();
        getSchemaElements(etype, schemaElements, attrDefs, locale, 0, null);
        return schemaElements;
    }

    /**
     * @param etype
     * @param schemaElements
     * @param attrDefs
     * @param locale
     */
    private void getSchemaElements(IEntityType etype, List<ISchemaElement> schemaElements, List<IAttributeDef> attrDefs, Locale locale, int depth, ISchemaElement parentElement) {
        List<Instance> instances = getEntities(etype);
        for (IAttributeDef atrDef : attrDefs) {
            SchemaElement schemaElement = new SchemaElement();

            ElementContext elContext = new ElementContext();
            ElementContent elContent = new ElementContent();
            List<IElementRelation> schemaElementRelations = new ArrayList<IElementRelation>();
            ElementRelation elRelation = new ElementRelation();
            if (parentElement != null) {
                elRelation.setRelation("parent");
                elRelation.setRelatedElement(parentElement);
            }
            schemaElementRelations.add(elRelation);
            //context extraction
            schemaElement.setAttrDef(atrDef);
            elContext.setElementName(atrDef.getName().string(locale));
            String dataType = atrDef.getDataType();
            elContext.setElemetnDataType(dataType);
            //convert local concept id to a global one
            elContext.setElementConcept(WebServiceURLs.urlToConceptID(atrDef.getConceptURL()));
            schemaElement.setElementContext(elContext);
            //schema element relation extraction
            schemaElement.setSchemaElementRelations(schemaElementRelations);

            //schema element's content extraction
            //TODO find the way to download best representing content
            List<Object> values = new ArrayList<Object>();
            if (instances != null) {
                if ((atrDef.getDataType().equalsIgnoreCase("xsd:float")) || (atrDef.getDataType().equalsIgnoreCase("xsd:integer"))
                        || (atrDef.getDataType().equalsIgnoreCase("xsd:long"))) {
                    values = getValues(instances, atrDef);
                }
            }
            elContent.setContent(values);
            schemaElement.setElementContent(elContent);
            schemaElements.add(schemaElement);
            if (atrDef.getDataType().equalsIgnoreCase("oe:structure")) {
                EntityTypeService ets = new EntityTypeService();
                IEntityType structureEtype = (EntityType) ets.readEntityType(atrDef.getRangeEtypeURL());
                List<IAttributeDef> strAttrDefs = structureEtype.getAttributeDefs();
                if (depth < RECURSION_DEPTH) {
                    getSchemaElements(structureEtype, schemaElements, strAttrDefs, locale, ++depth, schemaElement);
                }
            }
        }
    }

    private List<Object> getValues(List<Instance> instances,
            IAttributeDef atrDef) {
        List<Object> objects = new ArrayList<Object>();
        for (Instance in : instances) {
            List<Attribute> attrs = in.getAttributes();
            for (Attribute a : attrs) {
                if (a.getConceptId() == WebServiceURLs.urlToConceptID(atrDef.getConceptURL())) {
                    objects.add(a.getValues().iterator().next().getValue());
                }
            }
        }

        return objects;
    }

    /**
     * methods takes random entities from Entitypedia
     *
     * @param etype
     * @return
     */
    public List<Instance> getEntities(IEntityType etype) {

        InstanceClient insClient = new InstanceClient(WebServiceURLs.getClientProtocol());
        Long etypeId = WebServiceURLs.urlToEtypeID(etype.getURL());
        Pagination page = new Pagination();
        page.setPageSize(PAGE_SIZE);
        List<Instance> instances = insClient.readInstances(1L, etypeId, null, null, page); // TODO Make sure that they are taken randomly
        List<Long> instancesIds = new ArrayList<Long>();

        for (Instance in : instances) {
            instancesIds.add(in.getId());
        }
        InstanceFilter filter = new InstanceFilter();
        filter.setIncludeAttributes(true);
        if (instancesIds.size() != 0) {
            List<Instance> instancesFull = insClient.readInstancesById(instancesIds, filter);
            return instancesFull;
        } else {
            return null;
        }
    }

    public String extractDataType(ElementContent elContent) {
        //TODO think about best content for representation
        String contentToGuess = elContent.getContent().iterator().next().toString();
        //TODO think about enum of possible datatypes for the matcher
        Datatype datatype = TypeDetector.guessType(contentToGuess);
        return datatype.toString();
    }
}
