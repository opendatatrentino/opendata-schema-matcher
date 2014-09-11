package eu.trentorise.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.trentorise.schemamatcher.implementation.model.SchemaCorrespondence;
import eu.trentorise.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.schemamatcher.model.ISchema;
import eu.trentorise.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.schemamatcher.model.ISchemaElement;
import eu.trentorise.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.schemamatcher.model.ISchemaElementMatcher;
import eu.trentorise.schemamatcher.model.ISchemaMatcher;

/** Implementation of the schema matcher. 
 * The schema matcher uses Column-ConceptRecognizer that extracts concepts from the headers of schema elements 
 * (from names and instances of schema element),  
 * @author Ivan Tankoyeu
 *
 */
public class SimpleSchemaMatcher implements ISchemaMatcher {

	public static final String ALGORITHM_NAME="Simple";

	public SimpleSchemaMatcher() {

	}

	@Override
	public ISchemaCorrespondence matchSchemas(ISchema sourceSchema,
			ISchema targetSchema, String elementMatchingAlgorithm) {
		SchemaCorrespondence schemaCorrespondence = new SchemaCorrespondence();
		SElementMatchingService elMatching = new SElementMatchingService(); 

		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();

		
		List<ISchemaElement> sourceSchemaElements= sourceSchema.getSchemaElements();
		List<ISchemaElement> targetSchemaElements= targetSchema.getSchemaElements();
		//TODO check presence of concepts for schema elements
		sourceSchemaElements = sefe.runColumnRecognizer(sourceSchemaElements);

		ISchemaElementMatcher elementMatcher = elMatching.getElementMatcher(elementMatchingAlgorithm);
		schemaCorrespondence.setSourceSchema(sourceSchema);
		schemaCorrespondence.setTargetSchema(targetSchema);
		float score;
		//create the matrix of schema element correspondence
		if((sourceSchemaElements.size()!=0)&&(targetSchemaElements.size()!=0)){
			List<ISchemaElementCorrespondence> elementCorrespondences = elementMatcher.matchSchemaElements(sourceSchemaElements, targetSchemaElements);
			schemaCorrespondence.setElementCorrespondences(elementCorrespondences);
			score = computeOverallSchemaScore(sourceSchema,targetSchema, schemaCorrespondence);

		}else 

		{	
			List<ISchemaElementCorrespondence> elementCorrespondences = new ArrayList<ISchemaElementCorrespondence>();
			schemaCorrespondence.setElementCorrespondences(elementCorrespondences);
			score = (float) (computeOverallSchemaScore(sourceSchema,targetSchema)*0.1);
		}
		schemaCorrespondence.setScore(score);
		return schemaCorrespondence;
	}

	@Override
	public List<ISchemaCorrespondence> matchSchemas(
			List<ISchema> sourceSchemas, List<ISchema> targetSchemas,  String elementMatchingAlgorithm) {
		List<ISchemaCorrespondence> correspondences = new ArrayList<ISchemaCorrespondence>();
		
		for(ISchema sourceSchema: sourceSchemas){
			for (ISchema targetSchema: targetSchemas){
				ISchemaCorrespondence correspondence = matchSchemas(sourceSchema, targetSchema, elementMatchingAlgorithm);
				correspondences.add(correspondence);
			}
		}
		sort(correspondences);
		return correspondences;
	}

	@Override
	public String getSchemaMatchingAlgorithm() {
		return ALGORITHM_NAME;
	}

	private float computeSchemaElementCorrespondence(ISchemaCorrespondence sCorrespondence){
		float scoreSum=0;
		List<ISchemaElementCorrespondence> correspondences = sCorrespondence.getSchemaElementCorrespondence();

		for(ISchemaElementCorrespondence elCorr: correspondences ){
			scoreSum=scoreSum+elCorr.getElementCorrespondenceScore();
		}
		return scoreSum;
	}

	/**Methods computes scores from schema names concepts and element correspondences scores
	 * @param sourceSchema
	 * @param targetSchema
	 * @param sCorrespondence
	 * @return
	 */
	private float computeOverallSchemaScore(ISchema sourceSchema,
			ISchema targetSchema,ISchemaCorrespondence sCorrespondence){

		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
		float elementMatchScore =computeSchemaElementCorrespondence( sCorrespondence);
		float nameMatchScore= sefe.getConceptsDistance(sourceSchema.getSchemaConcept(), targetSchema.getSchemaConcept());
		float overallScore= (elementMatchScore+nameMatchScore)/(sCorrespondence.getSchemaElementCorrespondence().size()+1);

		return overallScore;
	}
	
	/** Methods returns score based on the distance between two source and target concepts of schemas names
	 * @param sourceSchema
	 * @param targetSchema
	 * @return score between target and source schema's names concepts
	 */
	private float computeOverallSchemaScore(ISchema sourceSchema,
			ISchema targetSchema){
		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
		return sefe.getConceptsDistance(sourceSchema.getSchemaConcept(), targetSchema.getSchemaConcept());
	}
	
	/** Sorts the given list of correspondence according to the score of each shema correspondence.
	 * @param schemaCorespondences
	 */
	private void sort(List<ISchemaCorrespondence> schemaCorespondences) {
		Collections.sort(schemaCorespondences, new CustomComparator());
	}
	

}
