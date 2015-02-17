package eu.trentorise.opendata.schemamatcher.implementation.services;

import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;

public class ElementMatcherFactory {


	public static ISchemaElementMatcher create(String elementMatcherType) {
		ISchemaElementMatcher elementMatcher = null;
		if (elementMatcherType.equalsIgnoreCase ("ConceptDistanceBased")){
			return (ISchemaElementMatcher) new SimpleElementMatcher();
		} else if (elementMatcherType.equalsIgnoreCase ("EditDistanceBased"))
			return (ISchemaElementMatcher) new EditDistanceElementMatcher();
		return elementMatcher;
	}
	
}
