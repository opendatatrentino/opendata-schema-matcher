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

public class HungarianAlgSchemaMatcher implements ISchemaMatcher {

	public static final String ALGORITHM_NAME="HungarianAllocationAndEditDistance";

	@Override
	public ISchemaCorrespondence matchSchemas(ISchema sourceSchema,
			ISchema targetSchema, String elementMatchingAlgorithm) {

		SchemaCorrespondence schemaCorrespondence = new SchemaCorrespondence();
		SElementMatchingService elMatching = new SElementMatchingService(); 

		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();


		List<ISchemaElement> sourceSchemaElements= sourceSchema.getSchemaElements();
		List<ISchemaElement> targetSchemaElements= targetSchema.getSchemaElements();

		sourceSchemaElements = sefe.runColumnRecognizer(sourceSchemaElements);

		ISchemaElementMatcher elementMatcher = elMatching.getElementMatcher(elementMatchingAlgorithm);

		schemaCorrespondence.setSourceSchema(sourceSchema);
		schemaCorrespondence.setTargetSchema(targetSchema);
		float score=0;
		//create the matrix of schema element correspondence
		if((sourceSchemaElements.size()!=0)&&(targetSchemaElements.size()!=0)){
			List<ISchemaElementCorrespondence> elementCorrespondences = elementMatcher.matchSchemaElements(sourceSchemaElements, targetSchemaElements);
			optimezedCorrespondenceScore(elementCorrespondences);
			schemaCorrespondence.setElementCorrespondences(elementCorrespondences);
			//	score = computeOverallSchemaScore(sourceSchema,targetSchema, schemaCorrespondence);

		}else 

		{	
			List<ISchemaElementCorrespondence> elementCorrespondences = new ArrayList<ISchemaElementCorrespondence>();
			schemaCorrespondence.setElementCorrespondences(elementCorrespondences);
			//score = (float) (computeOverallSchemaScore(sourceSchema,targetSchema)*0.1);
		}
		schemaCorrespondence.setScore(score);
		return schemaCorrespondence;
	}

	private float  optimezedCorrespondenceScore(
			List<ISchemaElementCorrespondence> elementCorrespondences) {

		HashMap<ISchemaElement, Float> corMaps = elementCorrespondences.get(0).getElementMapping();

		ArrayList<String> targetElNames = new ArrayList<String>();
		ArrayList<String> sourceElNames = new ArrayList<String>();

		for (ISchemaElement key : corMaps.keySet()) {
			targetElNames.add(key.getElementContext().getElementName());
		}
		int els = elementCorrespondences.size();
		int corMapSize =corMaps.size();

		int j = 0;
		float[][] matrix;
		float[][] matrixFin;
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
			sourceElNames.add(elCor.getSourceElement().getElementContext().getElementName());
			for (ISchemaElement key : corMap.keySet()) {
				System.out.print(key.getElementContext().getElementName()+"  ");
				System.out.print((float)Math.round((1-corMap.get(key)) * 100) / 100+"	   ");
				matrix[i][j]=1-corMap.get(key);
				matrixFin[i][j]=1-corMap.get(key);
				i++;
			}
			System.out.println();
			j++;
		}
		int assigmentMatrix[][]=hungarianAssigment(matrix);
		int p;
		if(corMapSize>els)
			p = corMapSize;
		else   p = els;


		float score = 0;
		for(int i =0; i<elementCorrespondences.size();i++ ){

			String targetElName =targetElNames.get(assigmentMatrix[i][0]);
			String sourceElement = sourceElNames.get(assigmentMatrix[i][1]);
			int indexT = targetElNames.indexOf(targetElName);
			int indexS = sourceElNames.indexOf(sourceElement);

			System.out.print(targetElNames.get(assigmentMatrix[i][0])+" "+indexT+ " ");
			System.out.print(sourceElNames.get(assigmentMatrix[i][1])+" "+indexS+ " ");

			float scoreEL = 1-matrixFin[indexT][indexS];
			score+=scoreEL;

			System.out.print(targetElNames.get(assigmentMatrix[i][0])+ "   ");
			System.out.print(sourceElNames.get(assigmentMatrix[i][1])+ "   ");
			System.out.println(scoreEL+"  ");
		}
		System.out.println("Total score: "+score);
		return score;
	}



	private int[][] hungarianAssigment(float[][] matrix) {
		HungarianAlgorithm ha = new HungarianAlgorithm();
		int[][] assigmentMatrix = ha.computeAssignments(matrix);
		return assigmentMatrix;
	}

	@Override
	public String getSchemaMatchingAlgorithm() {
		return this.ALGORITHM_NAME;
	}

	@Override
	public List<ISchemaCorrespondence> matchSchemas(
			List<ISchema> sourceSchemas, List<ISchema> targetSchemas,
			String elementMatchingAlgorithm) {
		return null;
	}




}
