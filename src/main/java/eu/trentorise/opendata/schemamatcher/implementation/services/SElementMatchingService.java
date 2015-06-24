package eu.trentorise.opendata.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;
import eu.trentorise.opendata.schemamatcher.services.matching.ISElementMatchingService;

public class SElementMatchingService implements ISElementMatchingService {

    public List<String> getElementMatchingAlgorithms() {
        List<String> matcherTypes = new ArrayList<String>();
        matcherTypes.add("ConceptDistanceBased");
        matcherTypes.add("EditDistanceBased");
        return matcherTypes;
    }

    public ISchemaElementMatcher getElementMatcher(String elementMatcherType) {
        return ElementMatcherFactory.create(elementMatcherType);
    }
}
