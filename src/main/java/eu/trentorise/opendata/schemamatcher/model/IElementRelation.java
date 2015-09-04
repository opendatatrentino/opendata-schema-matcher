package eu.trentorise.opendata.schemamatcher.model;

/**
 * Describes the relationship between different elements of a schema
 * 
 * For {@link SchemaStructureType#TREE} a possible relation is "parent"
 * 
 * @author Ivan Tankoyeu
 *
 */
public interface IElementRelation {

    /**
     * The relation between schema elements
     *
     * @return relation between element
     */
    public String getRelation();

    /**
     * Sets relation between two schema elements
     *
     * @param relation between schema elements
     */
    public void setRelation(String relation);

    /**
     * The target schema element towards which relation is assigned
     *
     * @return schema element
     */
    public ISchemaElement getRelatedElement();

    /**
     * Sets target schema element
     *
     * @param relatedElement related schema element
     */
    public void setRelatedElement(ISchemaElement relatedElement);

}
