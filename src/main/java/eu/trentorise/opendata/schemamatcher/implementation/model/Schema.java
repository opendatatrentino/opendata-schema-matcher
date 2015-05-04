package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.List;

import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.SchemaStructureType;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

public class Schema implements ISchema{

    private	String schemaName;
	private String schemaDescription;
	private List<ISchemaElement> schemaElements;
	private int elementsNumber;
	private SchemaStructureType structureType;
	private Long schemaConcept;
	private IEntityType etype;

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
