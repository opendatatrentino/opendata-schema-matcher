package eu.trentorise.opendata.schemamatcher.schemaanalysis.service;

import java.util.List;

import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;

/**
 * Class contains methods for feature extraction from schema element.
 *
 * @author Ivan Tankoyeu
 *
 */
public interface ISchemaElementFeatureExtractor {

    /**
     * Method extracts a concept(s) from schema element and assigns it to the
     * given element
     *
     * @param schemaElement schema element
     */
    void getSchemaElementConcept(ISchemaElement schemaElement);

    /**
     * Method extracts a concept(s) from schema elements and assigns it to the
     * given elements correspondingly
     *
     * @param schemaElement list of schema elements
     */
    void getSchemaElementConcept(List<ISchemaElement> schemaElement);
}
