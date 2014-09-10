package eu.trentorise.schemamatcher.implementation.model;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;
import eu.trentorise.schemamatcher.model.ISchema;
import eu.trentorise.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.schemamatcher.odr.impl.AtrCorrespondence;
import eu.trentorise.schemamatcher.odr.impl.ScheCorrespondence;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 24 Feb 2014
 * 
 */
public class SchemaCorrespondence implements ISchemaCorrespondence {
	/** Scores are considered equals up to this margin */
	static public final double SCORE_TOLERANCE = 0.01;

	ISchema sourceSchema;
	IEntityType etype;
	ISchema targetSchema;
	float score;
	List<ISchemaElementCorrespondence> elementCorrespondences;

	@Override
	public String toString() {
		return "SchemaCorrespondence [sourceSchema=" + sourceSchema
				+ ", targetSchema=" + targetSchema + ", score=" + score
				+ ", attributeCorrespondences=" + elementCorrespondences
				+ "]";
	}

	public void setScore(Float score) {
		this.score = score;
	}
	public void setSchemaElementCorrespondence(
			List<ISchemaElementCorrespondence> elementCorrespondences) {
		this.elementCorrespondences = elementCorrespondences;
	}

	public void setSourceSchema(ISchema sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	public void setTargetSchema(ISchema targetSchema) {
		this.targetSchema = targetSchema;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public void setElementCorrespondences(
			List<ISchemaElementCorrespondence> elementCorrespondences) {
		this.elementCorrespondences = elementCorrespondences;
	}

	@Override
	public List<ISchemaElementCorrespondence> getSchemaElementCorrespondence() {
		return elementCorrespondences;
	}
	@Override
	public ISchema getSourceSchema() {
		return this.sourceSchema;
	}
	@Override
	public ISchema getTargetSchema() {
		return this.targetSchema;
	}
	@Override
	public float getSchemaCorrespondenceScore() {
		return score;
	}

	public ScheCorrespondence convertToScheCorrespondence(){
		ScheCorrespondence sc = new ScheCorrespondence();
		sc.setScore(this.score);

		if (this.etype==null){
			Schema s= (Schema)this.targetSchema;
			sc.setEtype((EntityType)s.getEtype());
		} 
		else sc.setEtype((EntityType) this.etype);



		List<IAttributeCorrespondence> atrCors = new ArrayList<IAttributeCorrespondence>();
		List<ISchemaElementCorrespondence> elCors =  this.elementCorrespondences;
		for (ISchemaElementCorrespondence elCor: elCors){
			SchemaElementCorrespondence el=(SchemaElementCorrespondence)elCor;
			AtrCorrespondence a = el.convertToACorrespondence();
			atrCors.add(a);
		}
		sc.setAttributeCorrespondence(atrCors);
		return sc;
	}

}

