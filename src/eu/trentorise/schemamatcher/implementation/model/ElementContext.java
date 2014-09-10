package eu.trentorise.schemamatcher.implementation.model;

import eu.trentorise.schemamatcher.model.IElementContext;

public class ElementContext implements IElementContext {

	String elementName;
	String elementDescription;
	String elementDataType;
	Long elementConcept;
	
	
	
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

	

	public void setElemetnDataType(String elemetnDataType) {
		this.elementDataType = elementDataType;
	}

	public Long getElementConcept() {
		return elementConcept;
	}

	public void setElementConcept(Long elementConcept) {
		this.elementConcept = elementConcept;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public void setElementDescription(String elementDescription) {
		this.elementDescription = elementDescription;
	}

	
}
