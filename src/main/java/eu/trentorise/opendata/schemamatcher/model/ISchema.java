package eu.trentorise.opendata.schemamatcher.model;

import java.util.List;

/**
 * Class aims to represents schema
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 14 Jul 2014
 *
 */
public interface ISchema {

    /**
     * Elements of the schema. For example, if we consider schema as a graph
     * schema elements are graph nodes
     *
     * @return all schema elements
     */
    List<ISchemaElement> getSchemaElements();

    /**
     * Total number of elements in the schema
     *
     * @return number of schema elements
     */
    int getElementsNumber();

    /**
     * The name of the schema
     *
     * @return schema name
     */
    String getSchemaName();

    /**
     * The schema description
     *
     * @return schema description
     */
    String getSchemaDescription();

    /**
     * Type of the schema structure e.g tree, graph
     *
     * @return type structure of the schema
     */
    SchemaStructureType getStructureType();

    /**
     * Concept id that corresponds to the schema
     *
     * @return id of the concept
     */
    Long getSchemaConcept();

}
