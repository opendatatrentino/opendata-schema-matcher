package eu.trentorise.opendata.schemamatcher.implementation.model;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.schemamatcher.model.IElementRelation;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;

/**
 * Implementation of the element relation interface
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 *
 */
public class ElementRelation implements IElementRelation {

    private String relation;
    private ISchemaElement relatedElement;

    public ElementRelation(String relation, ISchemaElement relatedElement) {
        super();
        this.relation = relation;
        this.relatedElement = relatedElement;
    }

    public ElementRelation() {
    }

    @Override
    public String getRelation() {
        return relation;
    }

    @Override
    public void setRelation(String relation) {
        Preconditions.checkNotNull(relation);
        this.relation = relation;
    }

    @Override
    public ISchemaElement getRelatedElement() {
        return relatedElement;
    }

    @Override
    public void setRelatedElement(ISchemaElement relatedElement) {
        Preconditions.checkNotNull(relatedElement);
        this.relatedElement = relatedElement;
    }
}
