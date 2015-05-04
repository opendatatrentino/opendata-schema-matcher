package eu.trentorise.opendata.schemamatcher.implementation.model;


import eu.trentorise.opendata.schemamatcher.model.IElementRelation;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;

/** Implementation of the element relation interface
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public class ElementRelation implements IElementRelation {

	private String relation;
	private ISchemaElement relatedElement;

	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public ISchemaElement getRelatedElement() {
		return relatedElement;
	}
	public void setRelatedElement(ISchemaElement relatedElement) {
		this.relatedElement = relatedElement;
	}
}



