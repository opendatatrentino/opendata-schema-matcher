package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

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
	private long schemaConcept;
	private IEntityType etype;

	
	
	public Schema(String schemaName, String schemaDescription,
			List<ISchemaElement> schemaElements, int elementsNumber,
			SchemaStructureType structureType, Long schemaConcept,
			IEntityType etype) {
		super();
		this.schemaName = schemaName;
		this.schemaDescription = schemaDescription;
		this.schemaElements = schemaElements;
		this.elementsNumber = elementsNumber;
		this.structureType = structureType;
		this.schemaConcept = schemaConcept;
		this.etype = etype;
	}

	public Schema() {
	}

	@Override
	public String toString() {
		return "Schema [schemaName=" + schemaName + ", schemaDescription="
				+ schemaDescription + ", schemaElements=" + schemaElements.toString()
				+ ", elementsNumber=" + elementsNumber + ", structureType="
				+ structureType + "]";
	}
	@Override
	public List<ISchemaElement> getSchemaElements() {
		return this.schemaElements;
	}
	@Override
	public int getElementsNumber() {
		if ((Integer)this.elementsNumber==null){
			this.elementsNumber=this.schemaElements.size();
		} 
		return this.elementsNumber;
	}
	@Override
	public String getSchemaName() {
		return this.schemaName;
	}
	@Override
	public String getSchemaDescriptiopn() {
		return this.schemaDescription;
	}
	@Override
	public SchemaStructureType getStructureType() {
		return this.structureType;
	}

	public void setSchemaElements(List<ISchemaElement> schemaElements){
		Preconditions.checkNotNull(schemaElements);
		this.schemaElements=schemaElements;
	}

	@Nullable
	public void setSchemaName(String schemaName){
		this.schemaName=schemaName;
	}

	public void setSchemaDescription(String schemaDescription){
		Preconditions.checkNotNull(schemaDescription);
		this.schemaDescription=schemaDescription;
	}
	@Override
	public Long getSchemaConcept() {
		return this.schemaConcept;
	}

	public void setSchemaConcept(Long conceptId) {
		Preconditions.checkNotNull(conceptId);
		this.schemaConcept=conceptId;
	}
	
	public IEntityType getEtype(){
		return this.etype;
	}

	public void setEtype(IEntityType etype){
		Preconditions.checkNotNull(etype);
		this.etype=etype;
	}

	
}
