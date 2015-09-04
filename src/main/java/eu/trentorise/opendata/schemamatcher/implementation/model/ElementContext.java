package eu.trentorise.opendata.schemamatcher.implementation.model;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.schemamatcher.model.IElementContext;

/**
 * Implementation of IElementCOntext interface
 *
 * @author Ivan Tankoyeu
 *
 */
public class ElementContext implements IElementContext {

    private String name;
    private String description;
    private String dataType;
    @Nullable
    private String concept;

    public ElementContext(String name, String description,
            String dataType, String concept) {
        super();
        this.name = name;
        this.description = description;
        this.dataType = dataType;
        this.concept = concept;
    }

    public ElementContext() {

    }

    @Override
    public String toString() {
        return "ElementContext [elementName=" + name
                + ", elementDescription=" + description
                + ", elementDataType=" + dataType + ", elementConcept="
                + concept + "]";
    }

    @Override
    public String getElementName() {
        return this.name;
    }

    @Override
    public String getElementDescription() {
        return this.name;
    }

    @Override
    public String getElementDataType() {
        return this.dataType;
    }

    public void setElemetnDataType(String elementDataType) {
        Preconditions.checkNotNull(elementDataType);
        this.dataType = elementDataType;
    }

    @Override
    @Nullable
    public String getElementConcept() {
        return concept;
    }

    @Override
    @Nullable
    public void setElementConcept(String elementConcept) {
        Preconditions.checkNotNull(elementConcept);
        this.concept = elementConcept;
    }

    public void setElementName(String elementName) {
        this.name = elementName;
    }

    public void setElementDescription(String elementDescription) {
        Preconditions.checkNotNull(elementDescription);
        this.description = elementDescription;
    }
}
