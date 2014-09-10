package eu.trentorise.schemamatcher.odr.impl;

import java.util.HashMap;
import java.util.Map;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;

public class AtrCorrespondence implements IAttributeCorrespondence {
	
	float score;
	HashMap<IAttributeDef,Float> attrMap;
	AttributeDef attrDef;
	long headerConceptID;
	int columnIndex;
	
	
	@Override
	public IAttributeDef getAttrDef() {
		return this.attrDef;
	}

	@Override
	public Map<IAttributeDef, Float> getAttrMap() {
		return this.attrMap;
	}

	@Override
	public int getColumnIndex() {
		return this.columnIndex;
	}

	@Override
	public String getHeaderConceptURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getHeaderConceptID() {
		return this.headerConceptID;
	}

	@Override
	public float getScore() {
		return this.score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public void setAttrMap(HashMap<IAttributeDef, Float> attrMap) {
		this.attrMap = attrMap;
	}

	public void setAttrDef(AttributeDef attrDef) {
		this.attrDef = attrDef;
	}

	public void setHeaderConceptID(long headerConceptID) {
		this.headerConceptID = headerConceptID;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	
	

}
