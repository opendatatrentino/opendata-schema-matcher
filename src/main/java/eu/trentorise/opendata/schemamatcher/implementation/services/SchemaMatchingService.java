package eu.trentorise.opendata.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.schemamatcher.services.matching.ISchemaMatchingService;

public class SchemaMatchingService implements ISchemaMatchingService {

    public List<String> getSchemaMatchingAlgorithms() {

        List<String> matcherTypes = new ArrayList<String>();
        matcherTypes.add("Simple");
        matcherTypes.add("HungarianAllocationAndEditDistance");
        return matcherTypes;
    }

    public ISchemaMatcher getSchemaMatcher(String scheamMatcherType) {
        return SchemaMatcherFactory.create(scheamMatcherType);
    }
}
