package eu.trentorise.opendata.schemamatcher.implementation.services;

import it.unitn.disi.sweb.webapi.model.eb.Attribute;
import it.unitn.disi.sweb.webapi.model.eb.Instance;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import static com.google.common.base.Preconditions.checkNotNull;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.schemamatcher.util.SwebClientCrap;
import eu.trentorise.opendata.nlprise.DataTypeGuess.Datatype;
import eu.trentorise.opendata.schemamatcher.implementation.model.ElementContent;
import eu.trentorise.opendata.schemamatcher.implementation.model.ElementContext;
import eu.trentorise.opendata.schemamatcher.implementation.model.ElementRelation;
import eu.trentorise.opendata.schemamatcher.implementation.model.Schema;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElement;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.model.IElementRelation;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.services.importing.ISchemaImport;
import eu.trentorise.opendata.semantics.DataTypes;
import eu.trentorise.opendata.semantics.exceptions.UnsupportedSchemaException;
import eu.trentorise.opendata.semantics.model.entity.AttrDef;
import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.IEtypeService;
import eu.trentorise.opendata.traceprov.data.DcatMetadata;
import eu.trentorise.opendata.traceprov.types.ClassType;
import eu.trentorise.opendata.traceprov.types.ListType;
import eu.trentorise.opendata.traceprov.types.StringType;
import eu.trentorise.opendata.traceprov.types.TraceType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains logic for import from *.csv files, EntityTypes and ODR's IResource
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public class SchemaImport implements ISchemaImport {

    private static final int RECURSION_DEPTH = 3;

    private final static Logger LOG = LoggerFactory.getLogger(SchemaImport.class.getName());

    private IEkb ekb;

    public SchemaImport(IEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
    }

    @Override
    public ISchema extractSchema(File file) throws SchemaMatcherException {
        // TODO check supported format
        checkNotNull(file);

        ISchema schemaOut = null;
        try {
            schemaOut = parseCSV(file);
        }
        catch (IOException ex) {
            throw new RuntimeException("Error while parsing schema from CSV!", ex);
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
        List<ISchemaElement> sElements = new ArrayList();
        for (int i = 0; i < str.length; i++) {
            SchemaElement schemaElement = new SchemaElement();
            ElementContext sContext = new ElementContext();
            sContext.setElementName(str[i]);

            ElementContent elContent = new ElementContent();
            List<Object> elInstances = new ArrayList();

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
        schema.setConceptUrl(SwebConfiguration.getUrlMapper().conceptIdToUrl(conceptId));
        schema.setName(fileName);
        reader.close();
        return schema;
    }

    @Override
    public ISchema extractSchema(Object schema, Locale locale) throws SchemaMatcherException {

        if (schema == null) {
            throw new SchemaMatcherException("Null schema input is provided!");
        }
        if ((schema instanceof Etype)) {
            Etype etype = (Etype) schema;

            Schema schemaOut = new Schema();
            String name = etype.getName().string(locale);
            schemaOut.setName(name);
            schemaOut.setEtype(etype);
            List<ISchemaElement> schemaElements = null;
            if (!etype.getAttrDefs().isEmpty()) {
                schemaElements = extractSchemaElements(etype, locale);
            } else {
                schemaElements = new ArrayList();
            }

            schemaOut.setConceptUrl(etype.getConceptId());
            schemaOut.setSchemaElements(schemaElements);
            return schemaOut;
        } else {
            throw new SchemaMatcherException("Unsupported schema format!");
        }
    }
    
    

    /**
     * Extracts schema from Open Entity data format (which is a simple tree)
     *
     * Currently only supports traceprov type schema
     * {@code ListType<ClassType{field:string}>}
     *
     * @return object of Schema
     */
    public ISchema extractSchema(DcatMetadata dcatMetadata, TraceType traceType, Object data) {

        Schema schema = new Schema();

        String distribTitle = dcatMetadata.getDistribution().getTitle().some().str();

        schema.setName(distribTitle);
        List<ISchemaElement> selements = new ArrayList();

        // ----------------------------------------
        // checks supported schemas
        // ListType<ClassType>
        
        if (traceType instanceof ListType) {
            ListType listType = (ListType) traceType;
            TraceType subtype = listType.getSubtype();
            if (subtype instanceof ClassType) {
                ClassType classType = (ClassType) subtype;

                List<String> elNames = classType.getPropertyDefs().keySet().asList();

                for (String s : elNames) {
                    TraceType t = classType.getPropertyDef(s).getType();
                    if (!t.equals(StringType.of())) {
                        throw new UnsupportedSchemaException("Found unsupported sub type " + t.getId() + ", currently we only support " + StringType.of().getId());
                    }
                }

                for (String elName : elNames) {
                    SchemaElement sElement = new SchemaElement();
                    ElementContext elContext = new ElementContext();
                    ElementContent elContent = new ElementContent();
                    List<Object> instances = new ArrayList();
                    elContent.setContent(instances);
                    elContext.setElementName(elName);
                    sElement.setElementContext(elContext);
                    sElement.setElementContent(elContent);
                    selements.add(sElement);
                }
                if (data == null) {
                    LOG.info("Found null data, will only use type to find schemas");
                } else {
                    Iterable dataArray = (Iterable) data;
                    for (Object dn : dataArray) {
                        Map dataMap = (Map) dn;
                        for (int i = 0; i < elNames.size(); i++) {

                            String key = elNames.get(i);

                            Object value = dataMap.get(key); // hope any json terminal value is ok..

                            String sanitizedValue;
                            if (value == null) {
                                sanitizedValue = "";
                            } else if (value instanceof String) {
                                sanitizedValue = (String) value;
                            } else {
                                throw new UnsupportedSchemaException("Found unsupported data Java type " + value.getClass().getCanonicalName()
                                        + ", currently we only support " + StringType.of().getJavaClass());
                            }

                            selements.get(i).getElementContent().getContent().add(sanitizedValue);
                        }
                    }
                }

                SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
                // TODO THIS CALL ALSO *MODIFIES* STUFF INSIDE SELEMENTS :-@ 
                List<ISchemaElement> newSchemaElements;
                if (selements.isEmpty()){
                    newSchemaElements = new ArrayList();
                } else {
                    newSchemaElements = sefe.runColumnRecognizer(selements);    
                }
                                
                schema.setSchemaElements(newSchemaElements);
                long schemaConceptID = ColumnRecognizer.conceptFromText(distribTitle);
                schema.setConceptUrl(SwebConfiguration.getUrlMapper().conceptIdToUrl(schemaConceptID));

                return schema;
            }
        }

        throw new UnsupportedOperationException(
                "Could not interpret given data schema: " + traceType.toString());

    }

    /**
     * Method extracts schema elements from Etype
     *
     * @param etype input Etype
     * @param locale is used to return schema element names in the required
     * language
     * @return list of schema elements
     */
    private List<ISchemaElement> extractSchemaElements(Etype etype, Locale locale) {
        List<AttrDef> attrDefs = new ArrayList(etype.getAttrDefs().values());
        List<ISchemaElement> schemaElements = new ArrayList();
        getSchemaElements(etype, schemaElements, attrDefs, locale, 0, null);
        return schemaElements;
    }

    /**
     * @param etype
     * @param schemaElements
     * @param attrDefs
     * @param locale
     */
    private void getSchemaElements(Etype etype, List<ISchemaElement> schemaElements, List<AttrDef> attrDefs, Locale locale, int depth, ISchemaElement parentElement) {
        List<Instance> instances = SwebClientCrap.getEntities(etype);
        for (AttrDef atrDef : attrDefs) {
            SchemaElement schemaElement = new SchemaElement();

            ElementContext elContext = new ElementContext();
            ElementContent elContent = new ElementContent();
            List<IElementRelation> schemaElementRelations = new ArrayList();
            ElementRelation elRelation = new ElementRelation();
            if (parentElement != null) {
                elRelation.setRelation("parent");
                elRelation.setRelatedElement(parentElement);
            }
            schemaElementRelations.add(elRelation);
            //context extraction            
            elContext.setElementName(atrDef.getName().str(locale));
            String dataType = atrDef.getType().getDatatype();
            elContext.setElemetnDataType(dataType);
            //convert local concept id to a global one
            elContext.setElementConcept(atrDef.getConceptId());
            schemaElement.setElementContext(elContext);
            //schema element relation extraction
            schemaElement.setSchemaElementRelations(schemaElementRelations);

            //schema element's content extraction
            //TODO find the way to download best representing content
            List<Object> values = new ArrayList();
            if (instances != null) {
                if ((atrDef.getType().getDatatype().equalsIgnoreCase(DataTypes.FLOAT)) || (atrDef.getType().getDatatype().equalsIgnoreCase(DataTypes.INTEGER))
                        || (atrDef.getType().getDatatype().equalsIgnoreCase(DataTypes.LONG))) {
                    values = getValues(instances, atrDef);
                }
            }
            elContent.setContent(values);
            schemaElement.setElementContent(elContent);
            schemaElements.add(schemaElement);
            if (atrDef.getType().getDatatype().equalsIgnoreCase(DataTypes.STRUCTURE)) {
                IEtypeService ets = ekb.getEtypeService();
                Etype structureEtype = ets.readEtype(atrDef.getType().getEtypeId());
                List<AttrDef> strAttrDefs = new ArrayList(structureEtype.getAttrDefs().values());
                if (depth < RECURSION_DEPTH) {
                    getSchemaElements(structureEtype, schemaElements, strAttrDefs, locale, ++depth, schemaElement);
                }
            }
        }
    }

    private List<Object> getValues(List<Instance> instances,
            AttrDef atrDef) {
        List<Object> objects = new ArrayList();
        for (Instance in : instances) {
            List<Attribute> attrs = in.getAttributes();
            for (Attribute a : attrs) {
                if (a.getConceptId()== SwebConfiguration.getUrlMapper().conceptUrlToId(atrDef.getConceptId())) {
                    objects.add(a.getValues().iterator().next().getValue());
                }
            }
        }

        return objects;
    }

    public String extractDataType(ElementContent elContent) {
        //TODO think about best content for representation
        String contentToGuess = elContent.getContent().iterator().next().toString();
        //TODO think about enum of possible datatypes for the matcher
        Datatype datatype = TypeDetector.guessType(contentToGuess);
        return datatype.toString();
    }
}
