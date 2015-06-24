package eu.trentorise.opendata.schemamatcher.odr.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;
import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;

public class AtrCorrespondence implements IAttributeCorrespondence {

    private float score;
    private HashMap<IAttributeDef, Float> attrMap;
    private AttributeDef attrDef;
    private long headerConceptID;
    private int columnIndex;

    public IAttributeDef getAttrDef() {
        return this.attrDef;
    }

    public Map<IAttributeDef, Float> getAttrMap() {
        return this.attrMap;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }

    public String getHeaderConceptURL() {
        return WebServiceURLs.conceptIDToURL(this.headerConceptID);
    }

    public long getHeaderConceptID() {
        return this.headerConceptID;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(float score) {
        Preconditions.checkNotNull(score);

        this.score = score;
    }

    public void setAttrMap(HashMap<IAttributeDef, Float> attrMap) {
        Preconditions.checkNotNull(attrMap);
        this.attrMap = attrMap;
    }

    public void setAttrDef(AttributeDef attrDef) {
        Preconditions.checkNotNull(attrDef);
        this.attrDef = attrDef;
    }

    public void setHeaderConceptID(long headerConceptID) {
        Preconditions.checkNotNull(headerConceptID);

        this.headerConceptID = headerConceptID;
    }

    public void setColumnIndex(int columnIndex) {
        Preconditions.checkNotNull(columnIndex);

        this.columnIndex = columnIndex;
    }

}
