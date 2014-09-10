package eu.trentorise.schemamatcher.implementation.model;

import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.disiclient.model.knowledge.ConceptODR;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.schemamatcher.model.ISchemaElement;
import eu.trentorise.schemamatcher.schemaanalysis.service.ISchemaElementFeatureExtractor;

public class SchemaElementFeatureExtractor implements ISchemaElementFeatureExtractor{

	@Override
	public void getSchemaElementConcept(ISchemaElement schemaElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getSchemaElementConcept(List<ISchemaElement> schemaElement) {
		// TODO Auto-generated method stub

	}

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
	
}
