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

import eu.trentorise.opendata.disiclient.model.entity.AttributeDef;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.odr.impl.AtrCorrespondence;
import eu.trentorise.opendata.semantics.model.entity.IAttributeDef;

public class SchemaElementCorrespondence implements ISchemaElementCorrespondence {

	private SchemaElement sourceElement;
	private HashMap<ISchemaElement, Float> elementMapping;
	private Float score; 
	private SchemaElement highestTargetElement; 
	private IAttributeDef attrDef;

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
		Map<ISchemaElement, Float> correspondences = this.elementMapping;
		if (correspondences==null){
			this.highestTargetElement=null;
			this.score=(float) 0;
		}
		else{
			Map.Entry<ISchemaElement,Float> maxEntry = null;
			TreeMap<ISchemaElement,Float> map = new TreeMap<ISchemaElement,Float>();
			KeyComparator bvc =  new KeyComparator(map);
			TreeMap<ISchemaElement,Float> sortedKeyMap = new TreeMap<ISchemaElement,Float>(bvc);
			sortedKeyMap.putAll(correspondences);
			for (Map.Entry<ISchemaElement,Float> entry : sortedKeyMap.entrySet())
			{
			    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			    {
			        maxEntry = entry;
			    }
			}
			this.highestTargetElement=(SchemaElement) maxEntry.getKey() ;
			this.score = maxEntry.getValue();
		}

	}

	public AtrCorrespondence convertToACorrespondence(){
		AtrCorrespondence ac = new AtrCorrespondence();
		ac.setScore(this.score);
		if(this.attrDef==null){
			ac.setAttrDef((AttributeDef) this.highestTargetElement.attrDef);
		} else {
			ac.setAttrDef((AttributeDef) this.attrDef);
		}
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

	class KeyComparator implements Comparator<ISchemaElement> {

		private Map<ISchemaElement, Float> base;
		public KeyComparator(Map<ISchemaElement, Float> base) {
			this.base = base;
		}
		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(ISchemaElement a, ISchemaElement b) {
			if(a.getElementContext().getElementName()==null){
				return 1;
			}else 
				if(b.getElementContext().getElementName()==null){
					return -1;
				} else {
					return a.getElementContext().getElementName().compareTo(b.getElementContext().getElementName());
				}
		}
	}

	public static class MapUtil
	{
		public static <K, V extends Comparable<? super V>> Map<K, V> 
		sortByValue( Map<K, V> map )
		{
			List<Map.Entry<K, V>> list =
					new LinkedList<Map.Entry<K, V>>( map.entrySet() );
			Collections.sort( list, new Comparator<Map.Entry<K, V>>()
					{
				public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
				{
					return (o1.getValue()).compareTo( o2.getValue() );
				}
					} );
			Map<K, V> result = new LinkedHashMap<K, V>();
			for (Map.Entry<K, V> entry : list)
			{
				result.put( entry.getKey(), entry.getValue() );
			}
			return result;
		}
	}
}
