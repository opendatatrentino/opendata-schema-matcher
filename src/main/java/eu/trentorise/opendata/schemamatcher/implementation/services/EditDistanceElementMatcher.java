package eu.trentorise.opendata.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;

/** Implementation of schema matcher where  edit (string) distance of  two schema elements are compared by
 * 
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public class EditDistanceElementMatcher implements ISchemaElementMatcher {

	private final String ELEMENT_MATCHING_ALGORITHM="EditDistanceBased";

	public ISchemaElementCorrespondence matchSchemaElements(ISchemaElement sourceSchemaElement,
			ISchemaElement targetSchemaElement) {
		ISchemaElementCorrespondence elementCorrespondence = new SchemaElementCorrespondence();
		elementCorrespondence.setSourceElement(sourceSchemaElement);
		elementCorrespondence.setTargetElement(targetSchemaElement);
		float score = getDistance(sourceSchemaElement,targetSchemaElement);
		elementCorrespondence.setElementCorrespondenceScore(score);
		return elementCorrespondence;
	}

	/** Returns edit distance between source and target element names
	 * @param sourceSchemaElement source schema element	
	 * @param targetSchemaElement target schema element
	 * @return edit distance betweeen two schema element names
	 */
	private float getDistance(ISchemaElement sourceSchemaElement, ISchemaElement targetSchemaElement) {
		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
		double distance = sefe.getLevinsteinDistance(sourceSchemaElement.getElementContext().getElementName(), targetSchemaElement.getElementContext().getElementName());
	//	System.out.println("Source: "+sourceSchemaElement.getElementContext().getElementName()+" Target: "+ targetSchemaElement.getElementContext().getElementName() + " Score: "+distance);
		return (float) distance;
	}

	public List<ISchemaElementCorrespondence> matchSchemaElements(
			List<ISchemaElement> sourceElements,
			List<ISchemaElement> targetElements) {
		List<ISchemaElementCorrespondence> elementCorespondences = new ArrayList<ISchemaElementCorrespondence>();

		for(ISchemaElement sel:sourceElements){
			SchemaElementCorrespondence sec = new SchemaElementCorrespondence();
			HashMap<ISchemaElement, Float> correspondences = new HashMap <ISchemaElement, Float>();

			for (ISchemaElement stel: targetElements){
				ISchemaElementCorrespondence elementCorrespondence=	matchSchemaElements(sel,stel);

				correspondences.put(stel, elementCorrespondence.getElementCorrespondenceScore());
			}
			sec.setElementMapping(correspondences);
			sec.setSourceElement(sel);
			sec.computeHighestCorrespondencePair();
			elementCorespondences.add(sec);
		}

//		for (ISchemaElementCorrespondence elCor: elementCorespondences){
//			System.out.println(elCor.getSourceElement().getElementContext().getElementName());
//			for (ISchemaElement key : elCor.getElementMapping().keySet()) {
//				System.out.print(key.getElementContext().getElementName()+": "+elCor.getElementMapping().get(key)+" ");
//				
//			}
//			System.out.println();
//		}
		
		return elementCorespondences;
	}

	
	public String getElementMatchingAlgorithm() {
		return ELEMENT_MATCHING_ALGORITHM;
	}




}
