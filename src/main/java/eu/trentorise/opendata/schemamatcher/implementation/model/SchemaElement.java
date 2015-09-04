package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.List;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.schemamatcher.model.IElementContent;
import eu.trentorise.opendata.schemamatcher.model.IElementContext;
import eu.trentorise.opendata.schemamatcher.model.IElementRelation;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;

public class SchemaElement implements ISchemaElement {

    private IElementContent elementContent;
    private IElementContext elementContext;
    private List<IElementRelation> schemaElementRelations;
    protected IAttributeDef attrDef;
    private int columnIndex;

    public SchemaElement(IElementContent elementContent,
            IElementContext elementContext,
            List<IElementRelation> schemaElementRelations,
            IAttributeDef attrDef, int columnIndex) {
        super();
        this.elementContent = elementContent;
        this.elementContext = elementContext;
        this.schemaElementRelations = schemaElementRelations;
        this.attrDef = attrDef;
        this.columnIndex = columnIndex;
    }

    public SchemaElement() {
        // TODO Auto-generated constructor stub
    }

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
        Preconditions.checkNotNull(schemaElementRelations);
        this.schemaElementRelations = schemaElementRelations;
    }

    public void setElementContent(ElementContent elContent) {
        Preconditions.checkNotNull(elContent);
        this.elementContent = elContent;
    }

    public void setElementContext(IElementContext elementContext) {
        Preconditions.checkNotNull(elementContext);
        this.elementContext = elementContext;
    }

    @Override
    public IElementContent getElementContent() {
        return this.elementContent;
    }

    @Override
    public IElementContext getElementContext() {
        return this.elementContext;
    }

    @Override
    public Boolean hasElementContent() {
        return elementContent.getContent().size() > 0;
    }

    @Override
    public List<IElementRelation> getSchemaElementsRelations() {
        return (List<IElementRelation>) this.schemaElementRelations;
    }


    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        Preconditions.checkNotNull(columnIndex);
        this.columnIndex = columnIndex;
    }

}
