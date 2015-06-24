/**
 *
 */
package eu.trentorise.opendata.schemamatcher.model;

/**
 * An element context is a contextual representaion of the element. e.g. name,
 * description, number of values, etc.
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 14 Jul 2014
 *
 */
public interface IElementContext {

    /**
     * Method return the name of a schema element
     *
     * @return name of the element
     */
    public String getElementName();

    /**
     * Method return the description of a schema element
     *
     * @return description of a schema element
     */
    public String getElementDescription();

    /**
     * Method return the data type of instances of schema element
     *
     * @return data type of schema element content
     */
    public String getElementDataType(); //should not be string

    /**
     * Method return id of the concept
     *
     * @return name of the element
     */
    public long getElementConcept();

    /**
     * Method allows assigns concept id
     *
     * @param conceptId
     * @return
     */
    public void setElementConcept(Long conceptId);

}
