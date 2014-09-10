package eu.trentorise.schemamatcher.implementation.model;


import eu.trentorise.schemamatcher.model.IElementRelation;
import eu.trentorise.schemamatcher.model.ISchemaElement;

public class ElementRelation implements IElementRelation {

	String relation;
	ISchemaElement relatedElement;

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



