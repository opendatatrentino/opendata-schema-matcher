package eu.trentorise.opendata.schemamatcher.implementation.services;

import it.unitn.disi.sweb.webapi.client.IProtocolClient;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;

public class SimpleElementMatcher implements ISchemaElementMatcher {

	private final String ELEMENT_MATCHING_ALGORITHM="ConceptDistanceBased";

	public ISchemaElementCorrespondence matchSchemaElements(ISchemaElement sourceSchemaElement,
			ISchemaElement targetSchemaElement) {
		ISchemaElementCorrespondence elementCorrespondence = new SchemaElementCorrespondence();
		elementCorrespondence.setSourceElement(sourceSchemaElement);
		elementCorrespondence.setTargetElement(targetSchemaElement);
		float score = getConceptsDistance(sourceSchemaElement.getElementContext().getElementConcept(),targetSchemaElement.getElementContext().getElementConcept());
		elementCorrespondence.setElementCorrespondenceScore(score);
		return elementCorrespondence;
	}

	public List<ISchemaElementCorrespondence> matchSchemaElements(
			List<ISchemaElement> sourceElements,
			List<ISchemaElement> targetElements) {
		List<ISchemaElementCorrespondence> elementCorespondences = new ArrayList<ISchemaElementCorrespondence>();

		for(ISchemaElement sElement: sourceElements){
			List<Entry<Long,Long>> batch =new ArrayList<Entry<Long,Long>>(); 

			for (ISchemaElement tElement: targetElements){
				Map.Entry<Long,Long> entry =
						new AbstractMap.SimpleEntry<Long,Long>(sElement.getElementContext().getElementConcept(),tElement.getElementContext().getElementConcept());
				batch.add(entry);
			}

			List<Integer> distances = getBatchDistance(batch);
			SchemaElementCorrespondence sec = new SchemaElementCorrespondence();
			HashMap<ISchemaElement, Float> correspondences = new HashMap <ISchemaElement, Float>();

			for(int i=0; i<targetElements.size(); i++  ){
				float score = getScore(distances.get(i));
				correspondences.put(targetElements.get(i), score);
			}
			sec.setSourceElement(sElement);
			sec.setElementMapping(correspondences);
			sec.computeHighestCorrespondencePair();
			elementCorespondences.add(sec);
		}
		return elementCorespondences;
	}

	
	public String getElementMatchingAlgorithm() {
		return this.ELEMENT_MATCHING_ALGORITHM;
	}


	/** Returns the distance between two concept. The method uses LCA approach. 
	 * @param source source concept
	 * @param target target concept
	 * @return
	 */
	private float getConceptsDistance( long source, long target){
		ConceptClient cClient = new ConceptClient(getClientProtocol());
		float score  = (float)cClient.getDistanceUsingLca(source,target);
		if (score==-1) return 0;
		if ((score-1)!=0){
			return score = 1/(score-1);
		}
		else return 0;
	}


	/** Returns protocol for connection to the server.
	 * @return protocol that provides connection to the server.
	 */
	private IProtocolClient getClientProtocol(){
		return  WebServiceURLs.getClientProtocol();
	}

	/** Method sends to a server map of concept ids to compute distances among them. 
	 * @param batch list of entries 
	 * @return list of LCA distances
	 */
	private List<Integer> getBatchDistance(List<Entry<Long, Long>> batch) {
		ConceptClient cClient = new ConceptClient(getClientProtocol());
		return cClient.getDistancesUsingLca(batch);
	}
	/** Converts distance between two concepts into score 
	 * @param distance between two concepts
	 * @return score of the closeness between attribute concept and header concept 
	 */
	private float getScore( int distance){
		float score  = (float)distance;
		if (score==-1.0) return 0;
		if (score==0) return 1;
		else {
			return score = 1/(score+1);
		}}
}
