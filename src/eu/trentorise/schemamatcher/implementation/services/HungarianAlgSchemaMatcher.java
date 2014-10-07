package eu.trentorise.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import eu.trentorise.schemamatcher.implementation.model.SchemaCorrespondence;
import eu.trentorise.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.schemamatcher.model.ISchema;
import eu.trentorise.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.schemamatcher.model.ISchemaElement;
import eu.trentorise.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.schemamatcher.model.ISchemaElementMatcher;
import eu.trentorise.schemamatcher.model.ISchemaMatcher;

/** Schema matcher employs Hungarian algorithm for attribute matching
 * @author Ivan Tankoyeu
 *
 */
public class HungarianAlgSchemaMatcher implements ISchemaMatcher {

	public static final String ALGORITHM_NAME="HungarianAllocationAndEditDistance";

	public ISchemaCorrespondence matchSchemas(ISchema sourceSchema,
			ISchema targetSchema, String elementMatchingAlgorithm) {

		SchemaCorrespondence schemaCorrespondence = new SchemaCorrespondence();
		SElementMatchingService elMatching = new SElementMatchingService(); 
		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
		float score=0;


		List<ISchemaElement> sourceSchemaElements= sourceSchema.getSchemaElements();
		List<ISchemaElement> targetSchemaElements= targetSchema.getSchemaElements();
		
		sourceSchemaElements = sefe.runColumnRecognizer(sourceSchemaElements);

		ISchemaElementMatcher elementMatcher = elMatching.getElementMatcher(elementMatchingAlgorithm);

		schemaCorrespondence.setSourceSchema(sourceSchema);
		schemaCorrespondence.setTargetSchema(targetSchema);
		//create the matrix of schema element correspondence
		if((sourceSchemaElements.size()!=0)&&(targetSchemaElements.size()!=0)){
			List<ISchemaElementCorrespondence> elementCorrespondences = elementMatcher.matchSchemaElements(sourceSchemaElements, targetSchemaElements);
			score=optimezedCorrespondenceScore(elementCorrespondences);
			schemaCorrespondence.setElementCorrespondences(elementCorrespondences);

		}else 

		{	
			List<ISchemaElementCorrespondence> elementCorrespondences = new ArrayList<ISchemaElementCorrespondence>();
			schemaCorrespondence.setElementCorrespondences(elementCorrespondences);
		}
		schemaCorrespondence.setScore(score);
		return schemaCorrespondence;
	}

	private float  optimezedCorrespondenceScore(
			List<ISchemaElementCorrespondence> elementCorrespondences) {

		float[][] matrix;
		float[][] matrixFin;
		int j = 0;
		float score = 0;
		int assigmentMatrix[][];

		HashMap<ISchemaElement, Float> corMaps = elementCorrespondences.iterator().next().getElementMapping();

		ArrayList<ISchemaElement> targetElements = new ArrayList<ISchemaElement>();
		ArrayList<ISchemaElementCorrespondence> sourceElements = new ArrayList<ISchemaElementCorrespondence>();

		for (ISchemaElement key : corMaps.keySet()) {
			targetElements.add(key);
		}
		int els = elementCorrespondences.size();
		int corMapSize =corMaps.size();

		if(corMapSize>els){
			matrix = new float[corMaps.size()][corMaps.size()];
			matrixFin = new float[corMaps.size()][corMaps.size()];
		} else 	{	
			matrix = new float[elementCorrespondences.size()][elementCorrespondences.size()];
			matrixFin = new float[corMaps.size()][corMaps.size()];
		}
		// initialize array with max score 1.0
		for(int z=0; z<matrix.length; z++)
		{
			Arrays.fill(matrix[z], (float)1.0);
			Arrays.fill(matrixFin[z], (float)1.0);
		}

		for (ISchemaElementCorrespondence elCor: elementCorrespondences){
			int i = 0;
			HashMap<ISchemaElement, Float> corMap = elCor.getElementMapping();
			sourceElements.add(elCor);
			for (ISchemaElement key : corMap.keySet()) {
				matrix[i][j]=1-corMap.get(key);
				matrixFin[i][j]=1-corMap.get(key);
				i++;
			}
			j++;
		}
		
		assigmentMatrix=hungarianAssigment(matrix);

		for(int i =0; i<elementCorrespondences.size();i++ ){

			ISchemaElement targetElement =targetElements.get(assigmentMatrix[i][0]);
			ISchemaElementCorrespondence sourceElement = sourceElements.get(assigmentMatrix[i][1]);
			int indexT = targetElements.indexOf(targetElement);
			int indexS = sourceElements.indexOf(sourceElement);

			sourceElement.setTargetElement(targetElement);

			float scoreEl = 1-matrixFin[indexT][indexS];
			sourceElement.setElementCorrespondenceScore(scoreEl);
			score+=scoreEl;
		}

		return score;
	}

	private int[][] hungarianAssigment(float[][] matrix) {
		HungarianAlgorithm ha = new HungarianAlgorithm();
		int[][] assigmentMatrix = ha.computeAssignments(matrix);
		return assigmentMatrix;
	}

	public String getSchemaMatchingAlgorithm() {
		return HungarianAlgSchemaMatcher.ALGORITHM_NAME;
	}

	public List<ISchemaCorrespondence> matchSchemas(
			List<ISchema> sourceSchemas, List<ISchema> targetSchemas,
			String elementMatchingAlgorithm) {
		return null;
	}

}
