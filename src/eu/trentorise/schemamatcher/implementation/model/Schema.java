package eu.trentorise.schemamatcher.implementation.model;

import java.util.List;

import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.schemamatcher.model.ISchema;
import eu.trentorise.schemamatcher.model.ISchemaElement;
import eu.trentorise.schemamatcher.model.SchemaStructureType;

public class Schema implements ISchema{

	String schemaName;
	String schemaDescription;
	List<ISchemaElement> schemaElements;
	int elementsNumber;
	SchemaStructureType structureType;
	Long schemaConcept;
	IEntityType etype;


	@Override
	public String toString() {
		return "Schema [schemaName=" + schemaName + ", schemaDescription="
				+ schemaDescription + ", schemaElements=" + schemaElements.toString()
				+ ", elementsNumber=" + elementsNumber + ", structureType="
				+ structureType + "]";
	}

	public List<ISchemaElement> getSchemaElements() {
		return this.schemaElements;
	}

	public int getElementsNumber() {
		if ((Integer)this.elementsNumber==null){
			this.elementsNumber=this.schemaElements.size();
		} 
		return this.elementsNumber;
	}

	public String getSchemaName() {
		return this.schemaName;
	}

	public String getSchemaDescriptiopn() {
		return this.schemaDescription;
	}

	public SchemaStructureType getStructureType() {
		return this.structureType;
	}

	public void setSchemaElements(List<ISchemaElement> schemaElements){
		this.schemaElements=schemaElements;
	}


	public void setSchemaName(String schemaName){
		this.schemaName=schemaName;
	}

	public void setSchemaDescription(String schemaDescription){
		this.schemaDescription=schemaDescription;
	}

	public Long getSchemaConcept() {
		return this.schemaConcept;
	}

	public void setSchemaConcept(Long conceptId) {
		this.schemaConcept=conceptId;
	}
	
	public IEntityType getEtype(){
		return this.etype;
	}

	public void setEtype(IEntityType etype){
		this.etype=etype;
	}
}
