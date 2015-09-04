package eu.trentorise.opendata.schemamatcher.model;

import java.util.List;

/**
 * Represents a generic schema
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public interface ISchema {

    /**
     * Elements of the schema. For example, if we consider schema as a graph
     * schema elements are graph nodes
     *
     */
    List<ISchemaElement> getElements();

    /**
     * Total number of elements in the schema
     *
     */
    int getElementsNumber();

    /**
     * The name of the schema
     *     
     */
    String getName();

    /**
     * The schema description
     *     
     */
    String getDescription();

    /**
     * Type of the schema structure e.g tree, graph
     *     
     */
    SchemaStructureType getStructureType();

    /**
     * Concept url that corresponds to the schema     
     */
    String getConceptUrl();

}
