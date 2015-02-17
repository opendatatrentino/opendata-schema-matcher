package eu.trentorise.opendata.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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


		//		for (ISchemaElement key : corMaps.keySet()) {
		//			targetElements.add(key);
		//		}

		//		System.out.println("Target elements:");







		int els = elementCorrespondences.size();
		int corMapSize =corMaps.size();

		if(corMapSize>els){
			matrix = new float[corMaps.size()][corMaps.size()];
			matrixFin = new float[corMaps.size()][corMaps.size()];
		} else 	{	
			matrix = new float[els][els];
			matrixFin = new float[els][els];
		}
		// initialize matrix with max score 1.0
		for(int z=0; z<matrix.length; z++)
		{
			Arrays.fill(matrix[z], (float)1.0);
		}
		// initialize matrixFin with max score 1.0
		for(int z=0; z<matrixFin.length; z++)
		{
			Arrays.fill(matrixFin[z], (float)1.0);
		}

		HashMap<ISchemaElement, Float> maping = elementCorrespondences.get(0).getElementMapping();

		TreeMap<ISchemaElement,Float> map = new TreeMap<ISchemaElement,Float>();

		KeyComparator bvc =  new KeyComparator(map);
		TreeMap<ISchemaElement,Float> corMaping = new TreeMap<ISchemaElement,Float>(bvc);
		corMaping.putAll(maping);


		for (ISchemaElement key : corMaping.keySet()) {
				//	System.out.print("  		 "+key.getElementContext().getElementName());
			if(key.getElementContext().getElementName()!=null){
			targetElements.add(key);
			}

		}

		Collections.sort(targetElements, new SElementComparator());
			//System.out.println();


		for (ISchemaElementCorrespondence elCor: elementCorrespondences){
			//	System.out.print(elCor.getSourceElement().getElementContext().getElementName());
			int i = 0;
			HashMap<ISchemaElement, Float> mapingCor = elCor.getElementMapping();


			//HashMap<ISchemaElement, Float> maping = elementCorrespondences.get(0).getElementMapping();


			TreeMap<ISchemaElement,Float> corMap = new TreeMap<ISchemaElement,Float>(bvc);
			corMap.putAll(mapingCor);



			//		System.out.println();

			sourceElements.add(elCor);

			//	Collections.sort(sourceElements, new SElementComparator());

			for (ISchemaElement key : corMap.keySet()) {
				if(key.getElementContext().getElementName()!=null){
				//	System.out.print("  		    "+(1-corMap.get(key)));
				matrix[j][i]=1-corMap.get(key);
				matrixFin[j][i]=1-corMap.get(key);
				i++;}
			}
				//	System.out.println();
			j++;
		}

		for (int i=0; i<matrix.length;i++)
		{
			for (int k=0; k<matrix.length;k++){
					//		System.out.print( (double)Math.round(matrix[i][k] * 100) / 100+"  ");
			}
				//	System.out.println();
		}



		assigmentMatrix=hungarianAssigment(matrix);
			//System.out.println(Arrays.deepToString(assigmentMatrix));

		int size;
		if(corMapSize<els){
			size = corMapSize;
		}
		else {
			size = els;}
		for(int i =0; i<assigmentMatrix.length;i++ ){
			if((assigmentMatrix[i][0]>sourceElements.size()-1)||assigmentMatrix[i][1]>targetElements.size()-1)
			{ 
				continue;
			}

			ISchemaElementCorrespondence sourceElement = sourceElements.get(assigmentMatrix[i][0]);
				//	System.out.println("Source: "+assigmentMatrix[i][0]+" target: "+assigmentMatrix[i][1]);
			ISchemaElement targetElement =targetElements.get(assigmentMatrix[i][1]);



			int indexT = targetElements.indexOf(targetElement);
			int indexS = sourceElements.indexOf(sourceElement);

			sourceElement.setTargetElement(targetElement);

			float scoreEl = 1-matrixFin[indexS][indexT];
			//			System.out.println("Target: "+targetElement.getElementContext().getElementName()+" Source: "+sourceElement.getSourceElement().getElementContext().getElementName()+
			//					" Score: "+scoreEl);
			sourceElement.setElementCorrespondenceScore(scoreEl);
			score+=scoreEl;
		}

		return score/elementCorrespondences.size();
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
		List<ISchemaCorrespondence> correspondences = new ArrayList<ISchemaCorrespondence>();

		for(ISchema sourceSchema: sourceSchemas){
			for (ISchema targetSchema: targetSchemas){
				ISchemaCorrespondence correspondence = matchSchemas(sourceSchema, targetSchema, elementMatchingAlgorithm);
				correspondences.add(correspondence);
			}
		}
		sort(correspondences);
		return correspondences;	}

	/** Sorts the given list of correspondence according to the score of each schema correspondence.
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

class KeyComparator implements Comparator<ISchemaElement> {

	Map<ISchemaElement, Float> base;
	public KeyComparator(Map<ISchemaElement, Float> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(ISchemaElement a, ISchemaElement b) {
		if(a.getElementContext().getElementName()==null){
			return 1;
			//			System.out.println("1: "+a.getElementContext().getElementName()+ " 2: "+b.getElementContext().getElementName());
		}else 
			if(b.getElementContext().getElementName()==null){
				return -1;
				//			System.out.println("1: "+b.getElementContext().getElementName()+ " 2: "+b.getElementContext().getElementName());
			} else
				return a.getElementContext().getElementName().compareTo(b.getElementContext().getElementName());
	}
}
