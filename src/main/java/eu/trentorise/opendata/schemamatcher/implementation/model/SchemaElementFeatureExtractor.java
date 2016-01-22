package eu.trentorise.opendata.schemamatcher.implementation.model;

import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.KnowledgeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.schemamatcher.model.DataType;
import eu.trentorise.opendata.schemamatcher.model.IElementContent;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.DataType.Datatype;
import eu.trentorise.opendata.schemamatcher.schemaanalysis.service.ISchemaElementFeatureExtractor;

public class SchemaElementFeatureExtractor implements ISchemaElementFeatureExtractor{

	//public static final double MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH=  0.3;

	public static final float CONCEPT_DISTANCE_WEIGHT=  0.5f;
	public static final float EDIT_DISTANCE_WEIGHT=  0.4f;
	public static final float DATATYPE_DISTANCE_WEIGHT=  0.1f;



	public void getSchemaElementConcept(ISchemaElement schemaElement) {
		//	schemaElement.getElementContext().getElementConcept()
		//TODO
	}

	public void getSchemaElementConcept(List<ISchemaElement> schemaElement) {
		// TODO Auto-generated method stub

	}

	/** run column-concept recognizer, that extracts concepts for each schema element in the data set 
	 * @param schemaEls
	 * @return
	 */
	public List<ISchemaElement> runColumnRecognizer(List<ISchemaElement> schemaEls){
		List<ISchemaElement> schemaElements = schemaEls;
		List<String> elementNames = new ArrayList<String>();
		List<List<String>> elementContent = new ArrayList<List<String>>();
		HashMap<Integer,ISchemaElement> map = new HashMap<Integer, ISchemaElement>();
		int x = 0;
		for (ISchemaElement element: schemaElements){
			x++;
			map.put(x, element);
			elementNames.add(element.getElementContext().getElementName());
			List<Object> content = element.getElementContent().getContent();
			List<String> contStr = new ArrayList<String>();
			for(Object o: content){
				contStr.add(o.toString());
			}
			elementContent.add(contStr);

		}

		//		List<ColumnConceptCandidate> extractedConcepts =
		//				ColumnRecognizer.computeScoredCandidates(elementNames, elementContent);
		List<Long> extractedConceptsIDs = ColumnRecognizer.computeColumnConceptIDs(elementNames, elementContent);

		List<ISchemaElement> schemaElementsOut = new ArrayList<ISchemaElement>();

		for (int i=0; i<extractedConceptsIDs.size();i++)
		{
			//	System.out.println(extractedConceptsIDs+" col num: " +i);
			Long conceptId = extractedConceptsIDs.get(i);
			long globalConceptID;
			if (conceptId==-1){
				globalConceptID=-1;
			}else{
				ConceptODR codr = new ConceptODR();
				codr = new KnowledgeService().readConceptGlobalID(conceptId);
				globalConceptID =codr.getId();
			}
			SchemaElement se =(SchemaElement) map.get(i+1);

			se.setColumnIndex(i);
			se.getElementContext().setElementConcept(globalConceptID);
			schemaElementsOut.add(se);
			//		System.out.println(se.getElementContext().getElementName()+"  "+globalConceptID);
		}
		return schemaElementsOut;
	}

	/** Returns the distance between two concept. The method uses LCA approach. 
	 * @param source source concept
	 * @param target target concept
	 * @return
	 */
	public float getConceptsDistance( long source, long target){
		ConceptClient cClient = new ConceptClient(WebServiceURLs.getClientProtocol());
		if((source==-1)||(target==-1)){
			return 0;
		}
		float score  = (float)cClient.getDistanceUsingLca(source,target);
		if (score==0){
			return 1;
		}
		if (score==-1.0) return 0;
		float s = (float) (score-1.0);
		if (s!=0.0){
			return score = 1/(score-1);
		}
		else return 0;
	}

	/** Returns the edit distance between source and target strings. 
	 * @param sourceName
	 * @param targetName
	 * @return
	 */
	public double getLevinsteinDistance(String sourceName, String targetName) {
		if(sourceName==null||targetName==null){
			return 0.0;}

		//System.out.println("Source: "+sourceName+" Target: "+targetName);
		if (sourceName.equalsIgnoreCase(targetName)) {
			return 1.0;
		}

		//	System.out.println("Source: "+sourceName+" Target: "+targetName);

		int editDistance = StringUtils.getLevenshteinDistance(
				sourceName.toLowerCase(), targetName.toLowerCase());

		// Normalize for length:
		double score
		= (double) (targetName.length() - editDistance) / (double) targetName.length();
		//System.out.println("Score: "+score);
		// Artificially reduce the score if the first letters don't match
		//		if (sourceName.charAt(0) != targetName.charAt(0)) {
		//			score = Math.min(score, MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH);
		//		}
		return Math.max(0.0, Math.min(score, 1.0));
	}


	/** The method compares data types from source and target elements and returns score
	 * that represents similarity between them.
	 * @param sourceDataType
	 * @param targetDataType
	 * @return score from '0' to '1'.
	 */
	public float  getDataTypeSimilarity(String sourceDataType, String targetDataType){

		Datatype sourceDT = DataType.getDataType(sourceDataType);
		Datatype targetDT = DataType.getDataType(targetDataType);
		if(sourceDT.equals(targetDT)){
			return 1;
		} 
		else 
			return 0;
	}

	public float getComplexDistance(ElementContext sourceElementContext, ElementContext targetElementContext){
		float complexDistance = (float) (getConceptsDistance(sourceElementContext.getElementConcept(),targetElementContext.getElementConcept())*
				CONCEPT_DISTANCE_WEIGHT+getLevinsteinDistance(sourceElementContext.getElementName(),targetElementContext.getElementName())*
				EDIT_DISTANCE_WEIGHT+getDataTypeSimilarity(sourceElementContext.getElementName(),targetElementContext.getElementName())*
				DATATYPE_DISTANCE_WEIGHT);



		return complexDistance;
	}

	public float getStatisticalDistance(IElementContent sourceElementContent, IElementContent targetElementContent){
		
		int sSize = sourceElementContent.getContentSize();
		int tSize = targetElementContent.getContentSize();
		int size = 0;
	
		if(sSize<=tSize){
			size = sSize;
		}
		else 
		{	
			size = tSize;
		}

		double[] p1 = new double[size];
		double[] p2 = new double[size];

		System.out.println("sourceVals"+Arrays.toString(p1));
		System.out.println("targeteVals"+Arrays.toString(p2));

		
		
		for (int i = 0; i < size-1; i++) {
			p1[i] =  Double.parseDouble((String) sourceElementContent.getContent().get(i));  
			p2[i] = (Float) targetElementContent.getContent().get(i);  
		}

		double distance = StatUtil.klDivergence(p1, p2);

		return (float) distance;
	}

}
