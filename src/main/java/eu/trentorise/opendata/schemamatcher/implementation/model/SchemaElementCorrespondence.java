package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.odr.impl.AtrCorrespondence;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;

public class SchemaElementCorrespondence implements ISchemaElementCorrespondence {

    private SchemaElement sourceElement;
    private Map<ISchemaElement, Float> elementMapping;
    private float score;
    private SchemaElement highestTargetElement;
    private IAttributeDef attrDef;

    public SchemaElementCorrespondence(SchemaElement sourceElement,
            HashMap<ISchemaElement, Float> elementMapping, float score,
            SchemaElement highestTargetElement, IAttributeDef attrDef) {
        super();
        this.sourceElement = sourceElement;
        this.elementMapping = elementMapping;
        this.score = score;
        this.highestTargetElement = highestTargetElement;
        this.attrDef = attrDef;
    }

    public SchemaElementCorrespondence() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public float getElementCorrespondenceScore() {
        return this.score;
    }

    @Override
    public SchemaElement getTargetElement() {
        return highestTargetElement;
    }

    @Override
    public void setTargetElement(ISchemaElement targetElement) {
        Preconditions.checkNotNull(targetElement);
        this.highestTargetElement = (SchemaElement) targetElement;
    }

    @Override
    public SchemaElement getSourceElement() {
        return sourceElement;
    }

    @Override
    public void setSourceElement(ISchemaElement sourceElement) {
        Preconditions.checkNotNull(sourceElement);
        this.sourceElement = (SchemaElement) sourceElement;
    }

    public float getScore() {
        return score;
    }

    @Override
    public void setElementCorrespondenceScore(float score) {
        Preconditions.checkNotNull(score);
        this.score = score;
    }

    @Override
    public Map<ISchemaElement, Float> getElementMapping() {
        return elementMapping;
    }

    @Override
    public void setElementMapping(Map<ISchemaElement, Float> elementMapping) {
        Preconditions.checkNotNull(elementMapping);
        this.elementMapping = elementMapping;
    }

    public void computeHighestCorrespondencePair() {
        Map<ISchemaElement, Float> correspondences = this.elementMapping;
        if (correspondences == null) {
            this.highestTargetElement = null;
            this.score = (float) 0;
        } else {
            Map.Entry<ISchemaElement, Float> maxEntry = null;
            TreeMap<ISchemaElement, Float> map = new TreeMap();
            KeyComparator bvc = new KeyComparator(map);
            TreeMap<ISchemaElement, Float> sortedKeyMap = new TreeMap(bvc);
            sortedKeyMap.putAll(correspondences);
            for (Map.Entry<ISchemaElement, Float> entry : sortedKeyMap.entrySet()) {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = entry;
                }
            }
            this.highestTargetElement = (SchemaElement) maxEntry.getKey();
            this.score = maxEntry.getValue();
        }

    }

    public AtrCorrespondence convertToACorrespondence() {
        AtrCorrespondence ac = new AtrCorrespondence();
        ac.setScore(this.score);
        if (this.attrDef == null) {
            ac.setAttrDef((AttributeDef) this.highestTargetElement.attrDef);
        } else {
            ac.setAttrDef((AttributeDef) this.attrDef);
        }
        ac.setColumnIndex(this.sourceElement.getColumnIndex());
        ac.setHeaderConceptID(this.sourceElement.getElementContext().getElementConcept());

        Map<ISchemaElement, Float> map = this.elementMapping;
        HashMap<IAttributeDef, Float> atrCorMap = new HashMap();
        for (Map.Entry<ISchemaElement, Float> entry : map.entrySet()) {
            SchemaElement se = (SchemaElement) entry.getKey();
            atrCorMap.put(se.getAttrDef(), entry.getValue());
        }
        ac.setAttrMap(atrCorMap);
        return ac;

    }

    class KeyComparator implements Comparator<ISchemaElement> {

        private Map<ISchemaElement, Float> base;

        public KeyComparator(Map<ISchemaElement, Float> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.    
        public int compare(ISchemaElement a, ISchemaElement b) {
            if (a.getElementContext().getElementName() == null) {
                return 1;
            } else if (b.getElementContext().getElementName() == null) {
                return -1;
            } else {
                return a.getElementContext().getElementName().compareTo(b.getElementContext().getElementName());
            }
        }
    }

    public static class MapUtil {

        public static <K, V extends Comparable<? super V>> Map<K, V>
                sortByValue(Map<K, V> map) {
            List<Map.Entry<K, V>> list
                    = new LinkedList<Map.Entry<K, V>>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
                public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            });
            Map<K, V> result = new LinkedHashMap<K, V>();
            for (Map.Entry<K, V> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
    }

    public void setElementCorrespondenceScore(Float score) {
        // TODO Auto-generated method stub

    }
}
