package eu.trentorise.schemamatcher.implementation.services;

import eu.trentorise.schemamatcher.model.ISchemaElementMatcher;

public class ElementMatcherFactory {


	public static ISchemaElementMatcher create(String elementMatcherType) {
		ISchemaElementMatcher elementMatcher = null;
		if (elementMatcherType.equalsIgnoreCase ("ConceptDistanceBased")){
			return (ISchemaElementMatcher) new SimpleElementMatcher();
		}
		return elementMatcher;
	}
	
}
