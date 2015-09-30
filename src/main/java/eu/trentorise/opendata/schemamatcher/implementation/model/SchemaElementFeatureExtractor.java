package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.parboiled.common.ImmutableList;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.schemamatcher.model.DataType;
import eu.trentorise.opendata.schemamatcher.model.DataType.Datatype;
import eu.trentorise.opendata.schemamatcher.model.IElementContent;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.schemaanalysis.service.ISchemaElementFeatureExtractor;
import eu.trentorise.opendata.semantics.exceptions.OpenEntityNotFoundException;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

public class SchemaElementFeatureExtractor implements ISchemaElementFeatureExtractor {

    private static final Logger LOG = Logger.getLogger(SchemaElementFeatureExtractor.class.getName());
    
    public static final float CONCEPT_DISTANCE_WEIGHT = 0.5f;
    public static final float EDIT_DISTANCE_WEIGHT = 0.4f;
    public static final float DATATYPE_DISTANCE_WEIGHT = 0.1f;
    public static final double LOGOF2 = Math.log(2);

    @Override
    public void getSchemaElementConcept(ISchemaElement schemaElement) {
        //	schemaElement.getElementContext().getElementConcept()
        //TODO
    }

    @Override
    public void getSchemaElementConcept(List<ISchemaElement> schemaElement) {
        // TODO Auto-generated method stub
    }

    /**
     * Extracts concepts for each schema element in the data set with
     * column-concept recognizer
     * 
     * TODO THIS MESSY FUNCTION RETURNS A NEW LIST MADE WITH *MODIFIED* ELEMENTS OF THE INPUT :-@ 
     *
     */
    public List<ISchemaElement> runColumnRecognizer(List<ISchemaElement> schemaElements) {
        
	if (schemaElements.isEmpty()){
	    return new ArrayList();
	}
	
        List<String> elementNames = new ArrayList();
        List<List<String>> elementContent = new ArrayList();
        HashMap<Integer, ISchemaElement> map = new HashMap();
        int x = 0;
        for (ISchemaElement element : schemaElements) {
            x++;
            map.put(x, element);
            elementNames.add(element.getElementContext().getElementName());
            List<Object> content = element.getElementContent().getContent();
            List<String> contStr = new ArrayList();
            for (Object o : content) {
                contStr.add(o.toString());
            }
            elementContent.add(contStr);
        }

        List<Long> extractedConceptsGuids = ColumnRecognizer.computeColumnConceptIDs(elementNames, elementContent);
        
        List<ISchemaElement> ret = new ArrayList();

        List<Long> extractedConceptIds = new ArrayList();
        for (Long guid : extractedConceptsGuids){
            ConceptClient client = new ConceptClient(SwebConfiguration.getClientProtocol());
            List<it.unitn.disi.sweb.webapi.model.kb.concepts.Concept> concepts = client.readConcepts(1L, guid, null, null, null, null);
            if (concepts.isEmpty()) {
                throw new OpenEntityNotFoundException("Couldn't find concept with sweb global id " + guid);
            } else {
                if (concepts.size() > 1) {
                    LOG.warning("todo - only the first concept is returned. The number of returned concepts were: " + concepts.size());
                }                  
                it.unitn.disi.sweb.webapi.model.kb.concepts.Concept conc = concepts.get(0);
                
                extractedConceptIds.add(conc.getId());
            }

        }
        
        for (int i = 0; i < extractedConceptIds.size(); i++) {
            Long conceptId = extractedConceptIds.get(i);            
            SchemaElement se = (SchemaElement) map.get(i + 1);
            se.setColumnIndex(i);
            se.getElementContext().setElementConcept(
        	    SwebConfiguration.getUrlMapper().conceptIdToUrl(conceptId));
            ret.add(se);
        }
        return ret;
    }

    /**
     * Returns the distance between two concept. The method uses LCA approach.
     *
     * @param source source concept
     * @param target target concept
     * @return
     */
    public float getConceptsDistance(long source, long target) {
        ConceptClient cClient = new ConceptClient(SwebConfiguration.getClientProtocol());
        if ((source == -1) || (target == -1)) {
            return 0;
        }
        float score = (float) cClient.getDistanceUsingLca(source, target);
        if (score == 0) {
            return 1;
        }
        if (score == -1.0) {
            return 0;
        }
        float s = (float) (score - 1.0);
        if (s != 0.0) {
            return 1 / (score - 1);
        } else {
            return 0;
        }
    }

    /**
     * Returns the edit distance between source and target strings.
     *
     * @param sourceName
     * @param targetName
     * @return
     */
    public double getLevinsteinDistance(String sourceName, String targetName) {
        if (sourceName == null || targetName == null) {
            return 0.0;
        }

        if (sourceName.equalsIgnoreCase(targetName)) {
            return 1.0;
        }

        int editDistance = StringUtils.getLevenshteinDistance(
                sourceName.toLowerCase(), targetName.toLowerCase());

        // Normalize for length:
        double score
                = (double) (targetName.length() - editDistance) / (double) targetName.length();
        return Math.max(0.0, Math.min(score, 1.0));
    }

    /**
     * Compares data types from source and target elements and
     * returns score that represents similarity between them.
     *
     * @param sourceDataType
     * @param targetDataType
     * @return score from '0' to '1'.
     */
    public float getDataTypeSimilarity(String sourceDataType, String targetDataType) {

        Datatype sourceDT = DataType.getDataType(sourceDataType);
        Datatype targetDT = DataType.getDataType(targetDataType);
        if (sourceDT.equals(targetDT)) {
            return 1;
        } else {
            return 0;
        }
    }

    public float getComplexDistance(ElementContext sourceElementContext, ElementContext targetElementContext) {
        float complexDistance = (float) (getConceptsDistance(SwebConfiguration.getUrlMapper().conceptUrlToId(sourceElementContext.getElementConcept()),
                
               SwebConfiguration.getUrlMapper().conceptUrlToId(targetElementContext.getElementConcept()))
                * CONCEPT_DISTANCE_WEIGHT + getLevinsteinDistance(sourceElementContext.getElementName(), targetElementContext.getElementName())
                * EDIT_DISTANCE_WEIGHT + getDataTypeSimilarity(sourceElementContext.getElementName(), targetElementContext.getElementName())
                * DATATYPE_DISTANCE_WEIGHT);
        return complexDistance;
    }

    public float getStatisticalDistance(IElementContent sourceElementContent, IElementContent targetElementContent) {

        int size = sourceElementContent.getContentSize();
        double[] sourceSampleDistr = new double[size];
        double[] targetSampleDistr = new double[size];

        for (int i = 0; i < size - 1; i++) {
            sourceSampleDistr[i] = Double.parseDouble((String) sourceElementContent.getContent().get(i));
            targetSampleDistr[i] = (Float) targetElementContent.getContent().get(i);
        }
        double distance = computeKLDivergence(sourceSampleDistr, targetSampleDistr);
        return (float) distance;
    }

    private static double computeKLDivergence(double[] sourceSampleDistr, double[] targetSampleDistr) {

        double divergence = 0;
        for (int i = 0; i < sourceSampleDistr.length; ++i) {
            if (sourceSampleDistr[i] == 0 || targetSampleDistr[i] == 0) {
                continue;
            }
            divergence += sourceSampleDistr[i] * Math.log(sourceSampleDistr[i] / targetSampleDistr[i]);
        }
        return divergence / LOGOF2;
    }

}
