package eu.trentorise.opendata.schemamatcher.implementation.services;

import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;

/**
 * Factory class aims to return an object of schema matcher.
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public class SchemaMatcherFactory {

    /**
     * Method create an object of @see ISchemaMatcher
     *
     * @param schemaMatcherType type of schema matcher. The full list of schema
     * matching algorithms can be returned by calling @see
     * SchemaMatchingService.getSchemaMatchingAlgorithms()
     * @return object of ISchemaMatcher
     */
    public static ISchemaMatcher create(String schemaMatcherType) {
        ISchemaMatcher schemaMatcher = null;
        if (schemaMatcherType.equalsIgnoreCase("Simple")) {
            return (ISchemaMatcher) new SimpleSchemaMatcher();
        } else if (schemaMatcherType.equalsIgnoreCase("HungarianAllocationAndEditDistance")) {
            return (ISchemaMatcher) new HungarianAlgSchemaMatcher();
        }
        return schemaMatcher;
    }
}
