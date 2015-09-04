package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.SchemaStructureType;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

public class Schema implements ISchema {

    private String name;
    private String description;
    private List<ISchemaElement> elements;
    private int elementsNumber;
    private SchemaStructureType structureType;
    private String conceptUrl;
    private IEntityType etype;

    public Schema(String name, String description,
            List<ISchemaElement> elements, int number,
            SchemaStructureType structureType, String conceptUrl,
            IEntityType etype) {
        super();
        this.name = name;
        this.description = description;
        this.elements = elements;
        this.elementsNumber = number;
        this.structureType = structureType;
        this.conceptUrl = conceptUrl;
        this.etype = etype;
    }

    public Schema() {
    }

    @Override
    public String toString() {
        return "Schema{" + "name=" + name + ", description=" + description + ", elements=" + elements + ", elementsNumber=" + elementsNumber + ", structureType=" + structureType + ", conceptUrl=" + conceptUrl + ", etype=" + etype + '}';
    }
   

    @Override
    public List<ISchemaElement> getElements() {
        return this.elements;
    }

    @Override
    public int getElementsNumber() {
        
        this.elementsNumber = this.elements.size();
        
        return this.elementsNumber;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public SchemaStructureType getStructureType() {
        return this.structureType;
    }

    public void setSchemaElements(List<ISchemaElement> schemaElements) {
        Preconditions.checkNotNull(schemaElements);
        this.elements = schemaElements;
    }

    @Nullable
    public void setName(String schemaName) {
        this.name = schemaName;
    }

    public void setDescription(String schemaDescription) {
        Preconditions.checkNotNull(schemaDescription);
        this.description = schemaDescription;
    }

    @Override
    public String getConceptUrl() {
        return this.conceptUrl;
    }

    public void setConceptUrl(String conceptUrl) {
        
        Preconditions.checkNotNull(conceptUrl);
        this.conceptUrl = conceptUrl;
    }

    public IEntityType getEtype() {
        return this.etype;
    }

    public void setEtype(IEntityType etype) {
        Preconditions.checkNotNull(etype);
        this.etype = etype;
    }

}
