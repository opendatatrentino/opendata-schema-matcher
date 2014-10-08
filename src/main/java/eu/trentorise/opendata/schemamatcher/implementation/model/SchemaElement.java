package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.List;

import eu.trentorise.opendata.schemamatcher.model.IElementContent;
import eu.trentorise.opendata.schemamatcher.model.IElementContext;
import eu.trentorise.opendata.schemamatcher.model.IElementRelation;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;

public class SchemaElement implements ISchemaElement{

	IElementContent elementContent;
	IElementContext elementContext;
	List<IElementRelation>  schemaElementRelations;
	IAttributeDef attrDef;
	int columnIndex; 

	

	@Override
	public String toString() {
		return "SchemaElement [elementContent=" + elementContent
				+ ", elementContext=" + elementContext
				+ ", schemaElementRelations=" + schemaElementRelations + "]";
	}

	public List<IElementRelation> getSchemaElementRelations() {
		return schemaElementRelations;
	}

	public void setSchemaElementRelations(List<IElementRelation> schemaElementRelations) {
		this.schemaElementRelations = schemaElementRelations;
	}

	public void setElementContent(ElementContent elContent) {
		this.elementContent = elContent;
	}

	public void setElementContext(IElementContext elementContext) {
		this.elementContext =  elementContext;
	}


	public IElementContent getElementContent() {
		return  this.elementContent;
	}

	public IElementContext getElementContext() {
		return  this.elementContext;
	}

	public Boolean hasElementContent() {
		return this.hasElementContent();
	}

	public List<IElementRelation> getSchemaElementsRelations() {
		return (List<IElementRelation>) this.schemaElementRelations;
	}

	public IAttributeDef getAttrDef() {
		return attrDef;
	}

	public void setAttrDef(IAttributeDef attrDef) {
		this.attrDef = attrDef;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	
	

}
