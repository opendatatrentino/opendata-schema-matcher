package eu.trentorise.schemamatcher.implementation.model;

import java.util.HashMap;
import java.util.Map;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.schemamatcher.model.ISchemaElement;
import eu.trentorise.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.schemamatcher.odr.impl.AtrCorrespondence;

public class SchemaElementCorrespondence implements ISchemaElementCorrespondence {

	SchemaElement sourceElement;
	HashMap<ISchemaElement, Float> elementMapping;
	Float score; 
	SchemaElement highestTargetElement; 
	IAttributeDef attrDef;


	public float getElementCorrespondenceScore() {
		return this.score;
	}


	public SchemaElement getTargetElement() {
		return highestTargetElement;
	}


	public void setTargetElement(ISchemaElement targetElement) {
		this.highestTargetElement = (SchemaElement) targetElement;
	}


	public SchemaElement getSourceElement() {
		return sourceElement;
	}


	public void setSourceElement(ISchemaElement sourceElement) {
		this.sourceElement = (SchemaElement) sourceElement;
	}


	public Float getScore() {
		return score;
	}


	public void setElementCorrespondenceScore(Float score) {
		this.score = score;
	}


	public HashMap<ISchemaElement, Float> getElementMapping() {
		return elementMapping;
	}


	public void setElementMapping(HashMap<ISchemaElement, Float> elementMapping) {
		this.elementMapping = elementMapping;
	}

	public void computeHighestCorrespondencePair(){
		HashMap<ISchemaElement, Float> correspondences = this.elementMapping;
		if (correspondences==null){
			this.highestTargetElement=null;
			this.score=(float) 0;
		}
		else{
			Map.Entry<ISchemaElement, Float> maxEntry = null;
			for (Map.Entry<ISchemaElement,Float> entry:  correspondences.entrySet() ){
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
				{
					maxEntry = entry;
				}
			}
			this.highestTargetElement=(SchemaElement) maxEntry.getKey();
			this.score = maxEntry.getValue();
		}

	}
	
	public AtrCorrespondence convertToACorrespondence(){
		AtrCorrespondence ac = new AtrCorrespondence();
		ac.setScore(this.score);
		if(this.attrDef==null){
			ac.setAttrDef((AttributeDef) this.highestTargetElement.attrDef);
		} else
		ac.setAttrDef((AttributeDef) this.attrDef);
		ac.setColumnIndex(this.sourceElement.getColumnIndex());
		ac.setHeaderConceptID(this.sourceElement.getElementContext().getElementConcept());
		
		HashMap<ISchemaElement, Float> map = this.elementMapping;
		HashMap <IAttributeDef, Float> atrCorMap= new HashMap <IAttributeDef, Float> ();
		for (Map.Entry<ISchemaElement, Float> entry : map.entrySet())
		{
			SchemaElement se = (SchemaElement) entry.getKey();
			atrCorMap.put(se.getAttrDef(), entry.getValue());
		}
		ac.setAttrMap(atrCorMap);
		return ac;
		
	}
}
