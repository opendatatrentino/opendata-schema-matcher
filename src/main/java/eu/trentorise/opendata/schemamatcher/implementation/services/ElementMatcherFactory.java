package eu.trentorise.opendata.schemamatcher.implementation.services;

import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;

/**
 * Factory class aims to return an object of schema element matcher
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public class ElementMatcherFactory {

    /**
     * Method create an object of ISchemaElementMatcher
     *
     * @param elementMatcherType type of element matcher. The full list can be
     * returned by calling @see
     * ElementMatchingService.getSchemaMatchingAlgorithms().
     * @return object of ISchemaElementMatcher
     */
    public static ISchemaElementMatcher create(String elementMatcherType) {
        ISchemaElementMatcher elementMatcher = null;
        if (elementMatcherType.equalsIgnoreCase("ConceptDistanceBased")) {
            return (ISchemaElementMatcher) new SimpleElementMatcher();
        } else if (elementMatcherType.equalsIgnoreCase("EditDistanceBased")) {
            return (ISchemaElementMatcher) new EditDistanceElementMatcher();
        }
        return elementMatcher;
    }

}
