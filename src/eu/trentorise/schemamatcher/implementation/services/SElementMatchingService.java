package eu.trentorise.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.schemamatcher.model.ISchemaElementMatcher;
import eu.trentorise.schemamatcher.services.matching.ISElementMatchingService;

public class SElementMatchingService implements ISElementMatchingService {

	public List<String> getElementMatchingAlgorithms() {
		List<String> matcherTypes = new ArrayList<String>();
		matcherTypes.add("ConceptDistanceBased");
		return matcherTypes;
	}

	public ISchemaElementMatcher getElementMatcher(String elementMatcherType) {
		ISchemaElementMatcher em=	ElementMatcherFactory.create (elementMatcherType);
		return em;
	}
	

}
