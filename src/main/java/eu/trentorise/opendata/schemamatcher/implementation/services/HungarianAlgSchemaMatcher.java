package eu.trentorise.opendata.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;

/**
 * Schema matcher employs Hungarian algorithm for attribute (element) matching.
 * The algorithm is described in detail at @link
 * http://en.wikipedia.org/wiki/Hungarian_algorithm
 *
 * @author Ivan Tankoyeu
 *
 */
public class HungarianAlgSchemaMatcher implements ISchemaMatcher {

    public static final String ALGORITHM_NAME = "HungarianAllocationAndEditDistance";

    @Override
    public ISchemaCorrespondence matchSchemas(ISchema sourceSchema,
            ISchema targetSchema, String elementMatchingAlgorithm) {

        SchemaCorrespondence schemaCorrespondence = new SchemaCorrespondence();
        SElementMatchingService elMatching = new SElementMatchingService();
        SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
        float score = 0;

        List<ISchemaElement> sourceSchemaElements = sourceSchema.getElements();
        List<ISchemaElement> targetSchemaElements = targetSchema.getElements();

        sourceSchemaElements = sefe.runColumnRecognizer(sourceSchemaElements);

        ISchemaElementMatcher elementMatcher = elMatching.getElementMatcher(elementMatchingAlgorithm);

        schemaCorrespondence.setSourceSchema(sourceSchema);
        schemaCorrespondence.setTargetSchema(targetSchema);
        //create the matrix of schema element correspondence
        if ((!sourceSchemaElements.isEmpty()) && (!targetSchemaElements.isEmpty())) {
            List<ISchemaElementCorrespondence> elementCorrespondences = elementMatcher.matchSchemaElements(sourceSchemaElements, targetSchemaElements);
            score = optimezedCorrespondenceScore(elementCorrespondences);
            schemaCorrespondence.setElementCorrespondences(elementCorrespondences);

        } else {
            List<ISchemaElementCorrespondence> elementCorrespondences = new ArrayList();
            schemaCorrespondence.setElementCorrespondences(elementCorrespondences);
        }
        schemaCorrespondence.setScore(score);
        return schemaCorrespondence;
    }

    /**
     * @param elementCorrespondences
     * @return score of correspondences between two schemas
     */
    private float optimezedCorrespondenceScore(
            List<ISchemaElementCorrespondence> elementCorrespondences) {

        double[][] matrix;
        double[][] matrixFin;
        int j = 0;
        float score = 0;
        int assigmentMatrix[][];

        Map<ISchemaElement, Float> corMaps = elementCorrespondences.iterator().next().getElementMapping();

        ArrayList<ISchemaElement> targetElements = new ArrayList<ISchemaElement>();
        ArrayList<ISchemaElementCorrespondence> sourceElements = new ArrayList();

        int els = elementCorrespondences.size();
        int corMapSize = corMaps.size();

        if (corMapSize > els) {
            matrix = new double[corMaps.size()][corMaps.size()];
            matrixFin = new double[corMaps.size()][corMaps.size()];
        } else {
            matrix = new double[els][els];
            matrixFin = new double[els][els];
        }
        // initialize matrix with max score 1.0
        for (int z = 0; z < matrix.length; z++) {
            Arrays.fill(matrix[z], (float) 1.0);
        }
        // initialize matrixFin with max score 1.0
        for (int z = 0; z < matrixFin.length; z++) {
            Arrays.fill(matrixFin[z], (float) 1.0);
        }

        Map<ISchemaElement, Float> maping = elementCorrespondences.get(0).getElementMapping();

        TreeMap<ISchemaElement, Float> map = new TreeMap<ISchemaElement, Float>();

        KeyComparator bvc = new KeyComparator(map);
        TreeMap<ISchemaElement, Float> corMaping = new TreeMap<ISchemaElement, Float>(bvc);
        corMaping.putAll(maping);

        for (ISchemaElement key : corMaping.keySet()) {
            if (key.getElementContext().getElementName() != null) {
                targetElements.add(key);
            }

        }

        Collections.sort(targetElements, new SElementComparator());

        for (ISchemaElementCorrespondence elCor : elementCorrespondences) {
            int i = 0;
            Map<ISchemaElement, Float> mapingCor = elCor.getElementMapping();

            TreeMap<ISchemaElement, Float> corMap = new TreeMap(bvc);
            corMap.putAll(mapingCor);

            for (ISchemaElement key : corMap.keySet()) {
                if (key.getElementContext().getElementName() != null) {
                    matrix[j][i] = 1 - corMap.get(key);
                    matrixFin[j][i] = 1 - corMap.get(key);
                    i++;
                }
            }
            j++;
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int k = 0; k < matrix.length; k++) {
            }
        }

        assigmentMatrix = hungarianAssigment(matrix);

        for (int i = 0; i < assigmentMatrix.length; i++) {
            if ((assigmentMatrix[i][0] > sourceElements.size() - 1) || assigmentMatrix[i][1] > targetElements.size() - 1) {
                continue;
            }

            ISchemaElementCorrespondence sourceElement = sourceElements.get(assigmentMatrix[i][0]);
            ISchemaElement targetElement = targetElements.get(assigmentMatrix[i][1]);

            int indexT = targetElements.indexOf(targetElement);
            int indexS = sourceElements.indexOf(sourceElement);

            sourceElement.setTargetElement(targetElement);

            float scoreEl = (float) (1 - matrixFin[indexS][indexT]);
            sourceElement.setElementCorrespondenceScore(scoreEl);
            score += scoreEl;
        }

        return score / elementCorrespondences.size();
    }

    /**
     * Makes hungarian assigments
     *
     * @param matrix NxN of scores for elements of two schemas.
     * @return matrix with the best assigments
     */
    private int[][] hungarianAssigment(double[][] matrix) {
        KuhnMunkres ha = new KuhnMunkres();
        return ha.computeAssignments(matrix);
    }

    @Override
    public String getSchemaMatchingAlgorithm() {
        return HungarianAlgSchemaMatcher.ALGORITHM_NAME;
    }

    @Override
    public List<ISchemaCorrespondence> matchSchemas(
            List<ISchema> sourceSchemas, List<ISchema> targetSchemas,
            String elementMatchingAlgorithm) {
        List<ISchemaCorrespondence> correspondences = new ArrayList<ISchemaCorrespondence>();

        for (ISchema sourceSchema : sourceSchemas) {
            for (ISchema targetSchema : targetSchemas) {
                ISchemaCorrespondence correspondence = matchSchemas(sourceSchema, targetSchema, elementMatchingAlgorithm);
                correspondences.add(correspondence);
            }
        }
        sort(correspondences);
        return correspondences;
    }

    /**
     * Sorts the given list of correspondence according to the score of each
     * schema correspondence.
     *
     * @param schemaCorespondences
     */
    private void sort(List<ISchemaCorrespondence> schemaCorespondences) {
        Collections.sort(schemaCorespondences, new CustomComparator());
    }

    public class SElementComparator implements Comparator<ISchemaElement> {

        public int compare(ISchemaElement o1, ISchemaElement o2) {
            return o1.getElementContext().getElementName().compareTo(o2.getElementContext().getElementName());
        }
    }

}

/**
 * Compares names of a schema elements
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
class KeyComparator implements Comparator<ISchemaElement> {

    private Map<ISchemaElement, Float> base;

    public KeyComparator(Map<ISchemaElement, Float> base) {
        this.setBase(base);
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(ISchemaElement a, ISchemaElement b) {
        if (a.getElementContext().getElementName() == null) {
            return 1;
        } else if (b.getElementContext().getElementName() == null) {
            return -1;
        } else {
            return a.getElementContext().getElementName().compareTo(b.getElementContext().getElementName());
        }

    }

    public Map<ISchemaElement, Float> getBase() {
        return base;
    }

    public void setBase(Map<ISchemaElement, Float> base) {
        this.base = base;
    }
}
