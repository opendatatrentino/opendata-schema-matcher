package eu.trentorise.opendata.schemamatcher.odr.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import eu.trentorise.opendata.schemamatcher.implementation.model.Schema;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.IElementRelation;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;

import eu.trentorise.opendata.semantics.model.entity.Etype;
import eu.trentorise.opendata.semantics.services.IEkb;
import eu.trentorise.opendata.semantics.services.AttrMapping;
import eu.trentorise.opendata.semantics.services.IEtypeService;
import eu.trentorise.opendata.semantics.services.Mappings;
import eu.trentorise.opendata.semantics.services.SchemaMapping;
import eu.trentorise.opendata.traceprov.data.DcatMetadata;
import eu.trentorise.opendata.traceprov.types.TraceType;
import java.util.Collections;
import java.util.LinkedList;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchingService implements eu.trentorise.opendata.semantics.services.ISchemaMatchingService {

    private static final String SCHEMA_MATCHER_ALGORITHM = "Simple";
    private final static Logger LOG = LoggerFactory.getLogger(MatchingService.class.getName());
    private Locale locale;
    private IEkb ekb;

    public MatchingService(IEkb ekb) {
        checkNotNull(ekb);
        this.ekb = ekb;
    }
           
    public List<SchemaMapping> matchSchemasFile(File file) {
        SchemaImport si = new SchemaImport(ekb);
        ISchema schemaCSV = null;
        try {
            schemaCSV = si.parseCSV(file);
        }
        catch (IOException e) {
            LOG.error(e.getMessage());
        }
        List<ISchema> sourceSchemas = new ArrayList();
        sourceSchemas.add(schemaCSV);
        return match(sourceSchemas);
    }

    
    private List<SchemaMapping> match(List<ISchema> sourceSchemas) {
        List<SchemaMapping> ret = new ArrayList();
        
        SchemaImport si = new SchemaImport(ekb);
        
        List<Etype> etypeList = ekb.getEtypeService().readAllEtypes();
        List<ISchema> targetSchemas = new ArrayList();

        for (Etype etype : etypeList) {

            ISchema schemaEtype;
            try {
                schemaEtype = si.extractSchema(etype, locale);
                targetSchemas.add(schemaEtype);
                ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create(SCHEMA_MATCHER_ALGORITHM);
                List<ISchemaCorrespondence> schemaCor = schemaMatcher.matchSchemas(sourceSchemas, targetSchemas, "ConceptDistanceBased");
                
                for (ISchemaCorrespondence sc : schemaCor) {                    
                    ret.add(schemaCorrespondanceToSchemaMapping(sc));
                }
            }
            catch (SchemaMatcherException e) {
                LOG.error("Could not extract schema from etype " + etype.getId() + ", skipping it", e);
            }

        }

        Collections.sort(ret, Collections.reverseOrder());
        return ret;
    }
    

    @Override
    public List<SchemaMapping> matchSchemas(DcatMetadata dcatMetadata, TraceType traceType, @Nullable Object data) {
        checkNotNull(dcatMetadata);
        checkNotNull(traceType);

        List<SchemaMapping> ret = new ArrayList();
        // so... let's start data rock'n'roll

        SchemaImport si = new SchemaImport(ekb);
        IEtypeService etypeService = ekb.getEtypeService();
        List<Etype> etypeList = etypeService.readAllEtypes();
        List<ISchema> targetSchemas = new ArrayList();

        ISchema sourceSchema = si.extractSchema(dcatMetadata, traceType, data);
        
        for (Etype etype : etypeList) {

            ISchema schemaEtype = null;
            try {
                schemaEtype = si.extractSchema(etype, locale);
            }
            catch (SchemaMatcherException e) {
                LOG.error(e.getMessage());
            }
            targetSchemas.add(schemaEtype);

        }
        ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create(SCHEMA_MATCHER_ALGORITHM);
        List<ISchemaCorrespondence> schemaCor
                = schemaMatcher.matchSchemas(Lists.newArrayList(sourceSchema), targetSchemas, "ConceptDistanceBased");

        for (ISchemaCorrespondence sc : schemaCor) {            
            ret.add(schemaCorrespondanceToSchemaMapping(sc));
        }
        Collections.sort(ret, Collections.reverseOrder());
        return ret;
    }


    /**
     * @throws IllegalArgumentException if no 'parent' relation is found.
    */
    private IElementRelation findParentRelation(ISchemaElement se){
                
        for (IElementRelation rel : se.getSchemaElementsRelations()){
            if ("parent".equals(rel.getRelation())){
                return rel;
            }
        }
        throw new IllegalArgumentException("Could not find any 'parent' relation");
    }
    
    /**
     * Todo decide where to put this
     */
    private List<String> extractPath(ISchemaElement se){
   
        LinkedList<String> ret = new LinkedList();
        
        ISchemaElement curSe = se;
        while (curSe != null){
            try {
                IElementRelation rel = findParentRelation(curSe);
                curSe = rel.getRelatedElement();
                ret.addFirst(curSe.getElementContext().getElementName());
            } catch (Exception ex){
                curSe = null;            
            }
        }
        return ret;
    }
    
    /**
     * Todo decide where to put this
     */
    private SchemaMapping schemaCorrespondanceToSchemaMapping(ISchemaCorrespondence sc) {
        
        SchemaMapping.Builder smBuilder = SchemaMapping.builder();
        
        List<AttrMapping> amaps = new ArrayList();
        for (ISchemaElementCorrespondence sec : sc.getSchemaElementCorrespondence()){
            AttrMapping.Builder amBuilder = AttrMapping.builder();
            amBuilder.addSourcePath(Mappings.SCHEMA_SOURCE);
            amBuilder.addAllSourcePath(extractPath(sec.getSourceElement()));
            amBuilder.addAllTargetPath(extractPath(sec.getTargetElement()));
            amaps.add(amBuilder.build());
            Collections.sort(amaps, Collections.reverseOrder());
        }
        
        return smBuilder
                .addAllMappings(amaps)
                .setScore(sc.getSchemaCorrespondenceScore())
                .setTargetEtype(( (Schema)sc.getTargetSchema()).getEtype()).build();
        
    }

}
