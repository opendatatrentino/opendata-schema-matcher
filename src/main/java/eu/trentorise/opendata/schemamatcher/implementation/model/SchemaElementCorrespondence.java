package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;

public class SchemaElementCorrespondence implements ISchemaElementCorrespondence {

    private SchemaElement sourceElement;
    private Map<ISchemaElement, Float> elementMapping;
    private float score;
    private SchemaElement highestTargetElement;

    public SchemaElementCorrespondence(SchemaElement sourceElement,
            HashMap<ISchemaElement, Float> elementMapping, float score,
            SchemaElement highestTargetElement, IAttributeDef attrDef) {
        super();
        this.sourceElement = sourceElement;
        this.elementMapping = elementMapping;
        this.score = score;
        this.highestTargetElement = highestTargetElement;
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
            KeyComparator bvc = new KeyComparator();
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

    class KeyComparator implements Comparator<ISchemaElement> {
      

        // Note: this comparator imposes orderings that are inconsistent with equals.    
        @Override
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

}
