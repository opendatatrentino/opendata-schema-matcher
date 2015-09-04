package eu.trentorise.opendata.schemamatcher.implementation.model;

import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.schemamatcher.model.DataType;
import eu.trentorise.opendata.schemamatcher.model.IElementContent;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.DataType.Datatype;
import eu.trentorise.opendata.schemamatcher.schemaanalysis.service.ISchemaElementFeatureExtractor;

public class SchemaElementFeatureExtractor implements ISchemaElementFeatureExtractor {
 
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
     */
    public List<ISchemaElement> runColumnRecognizer(List<ISchemaElement> schemaEls) {
        List<ISchemaElement> schemaElements = schemaEls;
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

        List<Long> extractedConceptsIDs = ColumnRecognizer.computeColumnConceptIDs(elementNames, elementContent);
        List<ISchemaElement> schemaElementsOut = new ArrayList();

        for (int i = 0; i < extractedConceptsIDs.size(); i++) {
            Long conceptId = extractedConceptsIDs.get(i);            
            SchemaElement se = (SchemaElement) map.get(i + 1);
            se.setColumnIndex(i);
            se.getElementContext().setElementConcept(SwebConfiguration.getUrlMapper().conceptIdToUrl(conceptId));
            schemaElementsOut.add(se);
        }
        return schemaElementsOut;
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
        float complexDistance = (float) (getConceptsDistance(SwebConfiguration.getUrlMapper().urlToConceptId(sourceElementContext.getElementConcept()),
                
               SwebConfiguration.getUrlMapper().urlToConceptId(targetElementContext.getElementConcept()))
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
