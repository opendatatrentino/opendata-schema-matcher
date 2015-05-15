package eu.trentorise.opendata.schemamatcher.model;

import java.util.List;

/** A schema element ( e.g. column header, attribute,  etc) contains structural, contextual and content information.
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 14 Jul 2014
 * 
 */
public interface ISchemaElement {

	
	/** Method returns the content of the element, it can be only a part of the data 
	 * @return the content of the schema element 
	 */
	public IElementContent getElementContent();
	
	/** Method provides contextual information of the schema element e.g. data type, name, description, etc.
	 * @return the context of the schema element
	 */
	public IElementContext getElementContext();

	/** Method aims to provide information about the relationship between schema elements.
	 * @return Map,  where KEY is  relationship and value is a Map of target and source of schema elements correspondingly. 
	 */
	public List<IElementRelation> getSchemaElementsRelations();
	
	/** Method provides information about the existance or absence of the content of schema element .
	 * @return  TRUE in case of existance of a content in schema element and FALSE otherwise.
	 */
	public Boolean hasElementContent(); 
	
}
