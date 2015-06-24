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

    private String elementName;
    private String elementDescription;
    private String elementDataType;
    @Nullable
    private long elementConcept;

    public ElementContext(String elementName, String elementDescription,
            String elementDataType, long elementConcept) {
        super();
        this.elementName = elementName;
        this.elementDescription = elementDescription;
        this.elementDataType = elementDataType;
        this.elementConcept = elementConcept;
    }

    public ElementContext() {

    }

    @Override
    public String toString() {
        return "ElementContext [elementName=" + elementName
                + ", elementDescription=" + elementDescription
                + ", elementDataType=" + elementDataType + ", elementConcept="
                + elementConcept + "]";
    }

    @Override
    public String getElementName() {
        return this.elementName;
    }

    @Override
    public String getElementDescription() {
        return this.elementName;
    }

    @Override
    public String getElementDataType() {
        return this.elementDataType;
    }

    public void setElemetnDataType(String elementDataType) {
        Preconditions.checkNotNull(elementDataType);
        this.elementDataType = elementDataType;
    }

    @Override
    @Nullable
    public long getElementConcept() {
        return elementConcept;
    }

    @Override
    @Nullable
    public void setElementConcept(Long elementConcept) {
        Preconditions.checkNotNull(elementConcept);
        this.elementConcept = elementConcept;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public void setElementDescription(String elementDescription) {
        Preconditions.checkNotNull(elementDescription);
        this.elementDescription = elementDescription;
    }
}
