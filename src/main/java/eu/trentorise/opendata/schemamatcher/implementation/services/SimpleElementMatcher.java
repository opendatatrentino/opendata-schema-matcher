package eu.trentorise.opendata.schemamatcher.implementation.services;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;

public class SimpleElementMatcher implements ISchemaElementMatcher {

    private static final String ELEMENT_MATCHING_ALGORITHM = "ConceptDistanceBased";

    @Override
    public ISchemaElementCorrespondence matchSchemaElements(ISchemaElement sourceSchemaElement,
            ISchemaElement targetSchemaElement) {
        ISchemaElementCorrespondence elementCorrespondence = new SchemaElementCorrespondence();
        elementCorrespondence.setSourceElement(sourceSchemaElement);
        elementCorrespondence.setTargetElement(targetSchemaElement);
        float score = getConceptsDistance(
                SwebConfiguration.getUrlMapper().conceptUrlToId(sourceSchemaElement.getElementContext().getElementConcept()), 
                SwebConfiguration.getUrlMapper().conceptUrlToId(targetSchemaElement.getElementContext().getElementConcept()));
        elementCorrespondence.setElementCorrespondenceScore(score);
        return elementCorrespondence;
    }

    @Override
    public List<ISchemaElementCorrespondence> matchSchemaElements(
            List<ISchemaElement> sourceElements,
            List<ISchemaElement> targetElements) {
        List<ISchemaElementCorrespondence> elementCorespondences = new ArrayList();

        for (ISchemaElement sElement : sourceElements) {
            List<Entry<Long, Long>> batch = new ArrayList();
            List<Integer> distances;
            if (sElement.getElementContext().getElementConcept().isEmpty()) {
                distances = null;
            } else {
                for (ISchemaElement tElement : targetElements) {
                    Map.Entry<Long, Long> entry
                            = new AbstractMap.SimpleEntry(
                                   SwebConfiguration.getUrlMapper().conceptUrlToId(sElement.getElementContext().getElementConcept()),
                                   SwebConfiguration.getUrlMapper().conceptUrlToId(tElement.getElementContext().getElementConcept()));
                    batch.add(entry);
                }
                distances = getBatchDistance(batch);
            }

            SchemaElementCorrespondence sec = new SchemaElementCorrespondence();
            HashMap<ISchemaElement, Float> correspondences = new HashMap();

            for (int i = 0; i < targetElements.size(); i++) {
                float score = 0.01f;
                if (distances != null) {
                    score = getScore(distances.get(i));

                }
                correspondences.put(targetElements.get(i), score);
            }
            sec.setSourceElement(sElement);
            sec.setElementMapping(correspondences);
            sec.computeHighestCorrespondencePair();
            elementCorespondences.add(sec);

        }
        return elementCorespondences;
    }

    @Override
    public String getElementMatchingAlgorithm() {
        return SimpleElementMatcher.ELEMENT_MATCHING_ALGORITHM;
    }

    /**
     * Returns the distance between two concept. The method uses LCA approach.
     *
     * @param source source concept
     * @param target target concept
     * @return
     */
    private float getConceptsDistance(long source, long target) {
        ConceptClient cClient = new ConceptClient(SwebConfiguration.getClientProtocol());
        float score = (float) cClient.getDistanceUsingLca(source, target);
        if (score == -1) {
            return 0;
        }
        if ((score - 1) != 0) {
            return 1 / (score - 1);
        } else {
            return 0;
        }
    }

   
    private List<Integer> getBatchDistance(List<Entry<Long, Long>> batch) {
        ConceptClient cClient = new ConceptClient(SwebConfiguration.getClientProtocol());
        return cClient.getDistances(batch, 0);
    }

    /**
     * Converts distance between two concepts into score
     *
     * @param distance between two concepts
     * @return score of the closeness between attribute concept and header
     * concept
     */
    private float getScore(int distance) {
        float score = (float) distance;
        if (score == -1.0) {
            return 0.001f;
        }
        if (score == 0) {
            return 1;
        } else {
            return 1 / (score + 1);
        }
    }
}
