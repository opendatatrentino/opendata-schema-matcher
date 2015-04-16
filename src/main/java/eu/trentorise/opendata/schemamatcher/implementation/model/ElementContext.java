package eu.trentorise.opendata.schemamatcher.implementation.model;

import eu.trentorise.opendata.schemamatcher.model.IElementContext;

/** Implementation of IElementCOntext interface
 * @author Ivan Tankoyeu
 *
 */
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

	public String getElementName() {
		return this.elementName;
	}

	public String getElementDescription() {
		return this.elementName;
	}

	public String getElementDataType() {
		return this.elementDataType;
	}

	

	public void setElemetnDataType(String elementDataType) {
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
