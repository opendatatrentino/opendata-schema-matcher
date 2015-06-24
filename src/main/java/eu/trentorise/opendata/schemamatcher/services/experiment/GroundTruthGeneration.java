package eu.trentorise.opendata.schemamatcher.services.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElement;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;

/**
 * Generates ground truth for schema matching experiments. It is important to
 * provide appropriate GROUND_TRUTH_DATA_FOLDER that contains all the data sets
 * that were used in experiments.
 *
 * @author Ivan Tankoyeu
 *
 */
public class GroundTruthGeneration {

    private final String GROUND_TRUTH_DATA_FOLDER = "/home/ivan/work/development/Schema_Matching_dataset/";

    private final String LODGING_FACILITY_DATASET = "1_agritur16_10_2014.csv";
    private final Long LODGING_FACILITY_ETYPE_ID = 8L;
    private final static HashMap<String, String> LODGING_FACILITY_ELEMENTS_MAP;

    static {
        LODGING_FACILITY_ELEMENTS_MAP = new HashMap<String, String>();
        LODGING_FACILITY_ELEMENTS_MAP.put("Nome_Impresa_agricola", "Nome");
        LODGING_FACILITY_ELEMENTS_MAP.put("Latitudine", "Latitudine");
        LODGING_FACILITY_ELEMENTS_MAP.put("Longitudine", "Longitudine");
        LODGING_FACILITY_ELEMENTS_MAP.put("Altitudine", "Altitudine");
        LODGING_FACILITY_ELEMENTS_MAP.put("data_rilascio_prima_autorizzazione", "Inizio");
        LODGING_FACILITY_ELEMENTS_MAP.put("indirizzo_impresa_agricola", "Indirizzo");
        LODGING_FACILITY_ELEMENTS_MAP.put("Indirizzo_agriturismo", "Indirizzo");
        LODGING_FACILITY_ELEMENTS_MAP.put("pref", "Telefono");
        LODGING_FACILITY_ELEMENTS_MAP.put("tel", "Telefono");
        LODGING_FACILITY_ELEMENTS_MAP.put("indirizzo_e_mail", "Email");
        LODGING_FACILITY_ELEMENTS_MAP.put("PEC", "Email");
        LODGING_FACILITY_ELEMENTS_MAP.put("Comune_Sede_Agriturismo", "Comune");
        LODGING_FACILITY_ELEMENTS_MAP.put("Servizio_Ristorante_per_passanti", "Servizio");
        LODGING_FACILITY_ELEMENTS_MAP.put("N_tot_stanze_e_stanze_in_appartamento", "Camera");
        LODGING_FACILITY_ELEMENTS_MAP.put("Tot_posti_letto", "Letto");
        LODGING_FACILITY_ELEMENTS_MAP.put("Servizio_Ristorante_per_passanti", "Servizio");

    }

    private final String REFRESHMENT_FACILITY_DATASET = "6_Elenco_osterie_tipiche_civici.1386925759.csv";
    private final static HashMap<String, String> REFRESHMENT_FACILITY_ELEMENTS_MAP;

    static {
        REFRESHMENT_FACILITY_ELEMENTS_MAP = new HashMap<String, String>();
        REFRESHMENT_FACILITY_ELEMENTS_MAP.put("Insegna", "Nome");
        REFRESHMENT_FACILITY_ELEMENTS_MAP.put("Tipo", "Classe");
        REFRESHMENT_FACILITY_ELEMENTS_MAP.put("Indirizzo ", "Indirizzo");
        REFRESHMENT_FACILITY_ELEMENTS_MAP.put("Frazione ", "Frazione");
        REFRESHMENT_FACILITY_ELEMENTS_MAP.put("Civico ", "Numero civico");
        REFRESHMENT_FACILITY_ELEMENTS_MAP.put("Comune", "Comune");

    }
    private final Long REFRESHMENT_FACILITY_ETYPE_ID = 15L;
    private final String FACILITY_DATASET1 = "impianti-risalita-vivifiemme .csv";
    private final static HashMap<String, String> FACILITY_ELEMENTS_MAP1;

    static {
        FACILITY_ELEMENTS_MAP1 = new HashMap<String, String>();
        FACILITY_ELEMENTS_MAP1.put("nome", "Nome");
        FACILITY_ELEMENTS_MAP1.put("orari", "Orario di apertura");
        FACILITY_ELEMENTS_MAP1.put("typeIt", "Classe");
        FACILITY_ELEMENTS_MAP1.put("typeEn", "Classe");
        FACILITY_ELEMENTS_MAP1.put("latitudine", "Latitudine");
        FACILITY_ELEMENTS_MAP1.put("longitudine", "Longitudine");

    }

    private final String FACILITY_DATASET2 = "OSPEDALI001.csv";
    private final static HashMap<String, String> FACILITY_ELEMENTS_MAP2;

    static {
        FACILITY_ELEMENTS_MAP2 = new HashMap<String, String>();
        FACILITY_ELEMENTS_MAP2.put("OSPEDALE", "Nome");
        FACILITY_ELEMENTS_MAP2.put("INDIRIZZO", "Indirizzo");
        FACILITY_ELEMENTS_MAP2.put("TIPO_OSP", "Classe");
        FACILITY_ELEMENTS_MAP2.put("LATITUDINE_P", "Latitudine");
        FACILITY_ELEMENTS_MAP2.put("LONGITUDINE_P", "Longitudine");
        FACILITY_ELEMENTS_MAP2.put("LATITUDINE_V", "Latitudine");
        FACILITY_ELEMENTS_MAP2.put("LONGITUDINE_V", "Longitudine");
        FACILITY_ELEMENTS_MAP2.put("COMUNE", "Comune");
        FACILITY_ELEMENTS_MAP2.put("TELEFONO", "Telefono");
        FACILITY_ELEMENTS_MAP2.put("FAX", "Fax");
        FACILITY_ELEMENTS_MAP2.put("SITO_WEB", "Sito web");
        FACILITY_ELEMENTS_MAP2.put("E_MAIL", "Email");

    }

    private final String FACILITY_DATASET3 = "16_PUNTIPREL001.csv";
    private final static HashMap<String, String> FACILITY_ELEMENTS_MAP3;

    static {
        FACILITY_ELEMENTS_MAP3 = new HashMap<String, String>();
        FACILITY_ELEMENTS_MAP3.put("INDIRIZZO", "Indirizzo");
        FACILITY_ELEMENTS_MAP3.put("NOTE", "Descrizione");
        FACILITY_ELEMENTS_MAP3.put("LATITUDINE_P", "Latitudine");
        FACILITY_ELEMENTS_MAP3.put("LONGITUDINE_P", "Longitudine");
        FACILITY_ELEMENTS_MAP3.put("LATITUDINE_V", "Latitudine");
        FACILITY_ELEMENTS_MAP3.put("LONGITUDINE_V", "Longitudine");
        FACILITY_ELEMENTS_MAP3.put("COMUNE", "Comune");
        FACILITY_ELEMENTS_MAP3.put("TELEFONO", "Telefono");
    }
    private final Long FACILITY_ETYPE_ID = 12L;

    private final String SHOPPING_FACILITY_DATASET1 = "3_Botteghe_Storiche_del_Commercio.1392719556.csv";
    private final static HashMap<String, String> SHOPPING_FACILITY_ELEMENTS_MAP1;

    static {
        SHOPPING_FACILITY_ELEMENTS_MAP1 = new HashMap<String, String>();
        SHOPPING_FACILITY_ELEMENTS_MAP1.put("Insegna", "Nome");
        SHOPPING_FACILITY_ELEMENTS_MAP1.put("Indirizzo", "Indirizzo");
        SHOPPING_FACILITY_ELEMENTS_MAP1.put("Note", "Descrizione");
        SHOPPING_FACILITY_ELEMENTS_MAP1.put("Frazione ", "Frazione");
        SHOPPING_FACILITY_ELEMENTS_MAP1.put("Civico ", "Numero civico");
        SHOPPING_FACILITY_ELEMENTS_MAP1.put("Comune", "Comune");
    }

    private final String SHOPPING_FACILITY_DATASET2 = "FARM001.csv";
    private final static HashMap<String, String> SHOPPING_FACILITY_ELEMENTS_MAP2;

    static {
        SHOPPING_FACILITY_ELEMENTS_MAP2 = new HashMap<String, String>();
        SHOPPING_FACILITY_ELEMENTS_MAP2.put("FARMACIA", "Nome");
        SHOPPING_FACILITY_ELEMENTS_MAP2.put("COMUNE", "Comune");
        SHOPPING_FACILITY_ELEMENTS_MAP1.put("FRAZIONE ", "Frazione");

        SHOPPING_FACILITY_ELEMENTS_MAP2.put("INDIRIZZO", "Indirizzo");
        SHOPPING_FACILITY_ELEMENTS_MAP2.put("DATA_INIZIO", "Inizio");
        SHOPPING_FACILITY_ELEMENTS_MAP2.put("TIPOLOGIA", "Classe");
        SHOPPING_FACILITY_ELEMENTS_MAP2.put("LATITUDINE_P", "Latitudine");
        SHOPPING_FACILITY_ELEMENTS_MAP2.put("LONGITUDINE_P", "Longitudine");
        SHOPPING_FACILITY_ELEMENTS_MAP2.put("LATITUDINE_V", "Latitudine");
        SHOPPING_FACILITY_ELEMENTS_MAP2.put("LONGITUDINE_V", "Longitudine");

    }

    private final String SHOPPING_FACILITY_DATASET3 = "12_PARAFARM001.csv";
    private final static HashMap<String, String> SHOPPING_FACILITY_ELEMENTS_MAP3;

    static {
        SHOPPING_FACILITY_ELEMENTS_MAP3 = new HashMap<String, String>();
        SHOPPING_FACILITY_ELEMENTS_MAP3.put("PARAFARMACIA", "Nome");
        SHOPPING_FACILITY_ELEMENTS_MAP3.put("INDIRIZZO", "Indirizzo");
        SHOPPING_FACILITY_ELEMENTS_MAP3.put("COMUNE", "Comune");
        SHOPPING_FACILITY_ELEMENTS_MAP3.put("FRAZIONE ", "Frazione");
        SHOPPING_FACILITY_ELEMENTS_MAP3.put("LATITUDINE_P", "Latitudine");
        SHOPPING_FACILITY_ELEMENTS_MAP3.put("LONGITUDINE_P", "Longitudine");
        SHOPPING_FACILITY_ELEMENTS_MAP3.put("LATITUDINE_V", "Latitudine");
        SHOPPING_FACILITY_ELEMENTS_MAP3.put("LONGITUDINE_V", "Longitudine");
    }
    private final Long SHOPPING_FACILITY_ETYPE_ID = 1L;

    private final String PRODUCT_DATASET = "15_prodotti_tradizionali.csv";
    private final static HashMap<String, String> PRODUCT_ELEMENTS_MAP;

    static {
        PRODUCT_ELEMENTS_MAP = new HashMap<String, String>();
        PRODUCT_ELEMENTS_MAP.put("category", "Classe");
        PRODUCT_ELEMENTS_MAP.put("DESCRIZIONE SINTETICA DEL PRODOTTO", "Descrizione");
        PRODUCT_ELEMENTS_MAP.put("CURIOSITA", "Descrizione");
        PRODUCT_ELEMENTS_MAP.put("product_name", "Nome");
        PRODUCT_ELEMENTS_MAP.put("url", "Url");
        PRODUCT_ELEMENTS_MAP.put("production_areas", "Zona di produzione");
    }
    private final Long PRODUCT_ETYPE_ID = 19L;

    private final String CERT_PRODUCT_DATASET = "14_prodotti_protetti.csv";
    private final static HashMap<String, String> CERT_PRODUCT_ELEMENTS_MAP;

    static {
        CERT_PRODUCT_ELEMENTS_MAP = new HashMap<String, String>();
        CERT_PRODUCT_ELEMENTS_MAP.put("category", "Classe");
        CERT_PRODUCT_ELEMENTS_MAP.put("Caratteristiche", "Descrizione");
        CERT_PRODUCT_ELEMENTS_MAP.put("Nome", "Nome");
        CERT_PRODUCT_ELEMENTS_MAP.put("URL", "Url");
        CERT_PRODUCT_ELEMENTS_MAP.put("Zona di produzione", "Zona di produzione");
    }
    private final Long CERT_PRODUCT_ETYPE_ID = 17L;

    private final String LOCATION_DATASET1 = "10_LuoghiStoriciCommercio2010_2013.1387270045.csv";
    private final static HashMap<String, String> LOCATION_ELEMENTS_MAP;

    static {
        LOCATION_ELEMENTS_MAP = new HashMap<String, String>();
        LOCATION_ELEMENTS_MAP.put("Luogo storico del commercio", "Nome");
    }
    private final Long LOCATION_ETYPE_ID = 18L;
    SchemaImport si = new SchemaImport();

    List<ISchemaCorrespondence> schemaCorrespondenceGT = new ArrayList<ISchemaCorrespondence>();

    /**
     * Generate correspondence between source and target schemas
     *
     * @param inputDataSet an input data set (source schema)
     * @param etypeID an etype id (target schema)
     * @param elementMapping element mapping among two schemas
     * @return schema correspondence
     * @throws SchemaMatcherException
     */
    private SchemaCorrespondence generateGT(String inputDataSet, Long etypeID, HashMap<String, String> elementMapping) throws SchemaMatcherException {
        SchemaCorrespondence sc = new SchemaCorrespondence();
        File file1 = new File(GROUND_TRUTH_DATA_FOLDER + inputDataSet);
        ISchema sourceSchema = si.extractSchema(file1);
        sc.setSourceSchema(sourceSchema);
        EntityTypeService ets = new EntityTypeService();
        EntityType etype = (EntityType) ets.readEntityType(WebServiceURLs.etypeIDToURL(etypeID));
        ISchema targetSchema = si.extractSchema(etype, Locale.ITALIAN);
        sc.setTargetSchema(targetSchema);
        List<ISchemaElementCorrespondence> elementCorrespondences = generateGTElementCorrespondence(sourceSchema, targetSchema, elementMapping);
        sc.setSchemaElementCorrespondence(elementCorrespondences);
        return sc;
    }

    /**
     * Provides GT correspondence between elements from source and target
     * schemas
     *
     * @param sourceSchema source schema
     * @param targetSchema target schema
     * @param elementMapping hard-coded mapping among elements
     * @return list of correspondences among input and source schemas elements
     */
    private List<ISchemaElementCorrespondence> generateGTElementCorrespondence(ISchema sourceSchema, ISchema targetSchema, HashMap<String, String> elementMapping) {
        List<ISchemaElementCorrespondence> elementCorrs = new ArrayList<ISchemaElementCorrespondence>();
        List<ISchemaElement> elements = sourceSchema.getSchemaElements();

        for (ISchemaElement el : elements) {
            ISchemaElementCorrespondence elCorr = new SchemaElementCorrespondence();
            elCorr.setSourceElement(el);
            ISchemaElement targetElement = getTargetElement(elementMapping.get(el.getElementContext().getElementName()), targetSchema);
            elCorr.setTargetElement(targetElement);
            elementCorrs.add(elCorr);
        }
        return elementCorrs;
    }

    /**
     * Returns an element of a given schema by provided name
     *
     * @param targetElementName name of schema element
     * @param targetSchema schema
     * @return schema element
     */
    private ISchemaElement getTargetElement(String targetElementName, ISchema targetSchema) {
        for (ISchemaElement el : targetSchema.getSchemaElements()) {
            if ((el.getElementContext().getElementName() != null) && (el.getElementContext().getElementName().equalsIgnoreCase(targetElementName))) {
                return el;
            }
        }
        return null;
    }

    /**
     * Generates ground truth assigning hard-coded sources schemas to
     * correspondent target schemas.
     *
     * @throws SchemaMatcherException
     */
    public void generateGT() throws SchemaMatcherException {
        SchemaCorrespondence sc1 = generateGT(LODGING_FACILITY_DATASET, LODGING_FACILITY_ETYPE_ID, LODGING_FACILITY_ELEMENTS_MAP);
        schemaCorrespondenceGT.add(sc1);
        SchemaCorrespondence sc2 = generateGT(REFRESHMENT_FACILITY_DATASET, REFRESHMENT_FACILITY_ETYPE_ID, REFRESHMENT_FACILITY_ELEMENTS_MAP);
        schemaCorrespondenceGT.add(sc2);
        SchemaCorrespondence sc3 = generateGT(FACILITY_DATASET1, FACILITY_ETYPE_ID, FACILITY_ELEMENTS_MAP1);
        schemaCorrespondenceGT.add(sc3);
        SchemaCorrespondence sc4 = generateGT(FACILITY_DATASET2, FACILITY_ETYPE_ID, FACILITY_ELEMENTS_MAP2);
        schemaCorrespondenceGT.add(sc4);
        SchemaCorrespondence sc5 = generateGT(FACILITY_DATASET3, FACILITY_ETYPE_ID, FACILITY_ELEMENTS_MAP3);
        schemaCorrespondenceGT.add(sc5);
        SchemaCorrespondence sc9 = generateGT(SHOPPING_FACILITY_DATASET1, SHOPPING_FACILITY_ETYPE_ID, SHOPPING_FACILITY_ELEMENTS_MAP1);
        schemaCorrespondenceGT.add(sc9);
        SchemaCorrespondence sc10 = generateGT(SHOPPING_FACILITY_DATASET2, SHOPPING_FACILITY_ETYPE_ID, SHOPPING_FACILITY_ELEMENTS_MAP2);
        schemaCorrespondenceGT.add(sc10);
        SchemaCorrespondence sc11 = generateGT(SHOPPING_FACILITY_DATASET3, SHOPPING_FACILITY_ETYPE_ID, SHOPPING_FACILITY_ELEMENTS_MAP3);
        schemaCorrespondenceGT.add(sc11);
        SchemaCorrespondence sc12 = generateGT(PRODUCT_DATASET, PRODUCT_ETYPE_ID, PRODUCT_ELEMENTS_MAP);
        schemaCorrespondenceGT.add(sc12);
        SchemaCorrespondence sc13 = generateGT(CERT_PRODUCT_DATASET, CERT_PRODUCT_ETYPE_ID, CERT_PRODUCT_ELEMENTS_MAP);
        schemaCorrespondenceGT.add(sc13);
        SchemaCorrespondence sc14 = generateGT(LOCATION_DATASET1, LOCATION_ETYPE_ID, LOCATION_ELEMENTS_MAP);
        schemaCorrespondenceGT.add(sc14);
    }

}
