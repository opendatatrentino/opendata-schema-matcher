package eu.trentorise.schemamatcher.implementation.model;

import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.schemamatcher.model.DataType;
import eu.trentorise.schemamatcher.model.DataType.Datatype;
import eu.trentorise.schemamatcher.model.ISchemaElement;
import eu.trentorise.schemamatcher.schemaanalysis.service.ISchemaElementFeatureExtractor;

public class SchemaElementFeatureExtractor implements ISchemaElementFeatureExtractor{

	public static final double MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH=  0.3;

	@Override
	public void getSchemaElementConcept(ISchemaElement schemaElement) {
		//	schemaElement.getElementContext().getElementConcept()
		//TODO
	}

	@Override
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

		for (ISchemaElement element: schemaElements){
			elementNames.add(element.getElementContext().getElementName());
			List<Object> content = element.getElementContent().getContent();
			List<String> contStr = new ArrayList<String>();
			for(Object o: content){
				contStr.add(o.toString());
			}
			elementContent.add(contStr);
		}

		List<ColumnConceptCandidate> extractedConcepts =
				ColumnRecognizer.computeScoredCandidates(elementNames, elementContent);
		List<ISchemaElement> schemaElementsOut = new ArrayList<ISchemaElement>();

		for (int i=0; i<extractedConcepts.size();i++)
		{
			Long conceptId = extractedConcepts.get(i).getConceptID();

			ConceptODR codr = new ConceptODR();
			codr = codr.readConceptGlobalID(conceptId);
			long globalConceptID =codr.getId();
			SchemaElement se =(SchemaElement) schemaElements.get(i);
			se.setColumnIndex(extractedConcepts.get(i).getColumnNumber()-1);
			se.getElementContext().setElementConcept(globalConceptID);
			schemaElementsOut.add(se);

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
		if (sourceName.equals(targetName)) {
			return 1.0;
		}
		int editDistance = StringUtils.getLevenshteinDistance(
				sourceName, targetName);
		// Normalize for length:
		double score
		= (double) (targetName.length() - editDistance) / (double) targetName.length();

		// Artificially reduce the score if the first letters don't match
		if (sourceName.charAt(0) != targetName.charAt(0)) {
			score = Math.min(score, MAX_SCORE_FOR_NO_FIRST_LETTER_MATCH);
		}

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
		} else if (sourceDT.equals(DataType.STRING))
			return 0.1f;
		else 
			return 0;
	}

}
