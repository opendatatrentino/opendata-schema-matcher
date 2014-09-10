package eu.trentorise.schemamatcher.odr.impl;

import java.util.List;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;
import eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence;

public class ScheCorrespondence implements ISchemaCorrespondence{

	 
		EntityType etype;
		float score;
		List<IAttributeCorrespondence> attributeCorrespondences;
		
		
		@Override
		public String toString() {
			return "SchemaCorrespondence [etype=" + etype + ", score=" + score
					+ ", attributeCorrespondences=" + attributeCorrespondences
					+ "]";
		}
		public EntityType getEtype() {
			return etype;
		}
		public void setEtype(EntityType etype) {
			this.etype = etype;
		}
		public float getScore() {
			return score;
		}
		public void setScore(Float score) {
			this.score = score;
		}
		public void setAttributeCorrespondence(
				List<IAttributeCorrespondence> atrCors) {
			this.attributeCorrespondences = atrCors;
		}
		public List<IAttributeCorrespondence> getAttributeCorrespondences() {
			return attributeCorrespondences;
		}
		public List<IAttributeCorrespondence> getAttributeCorrespondence() {
			return attributeCorrespondences;
		}
		
}