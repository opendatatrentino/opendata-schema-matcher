package eu.trentorise.opendata.schemamatcher.services.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

public class MatchingExperiment implements  IMatchingExperiment {
	private final static Logger LOGGER = Logger.getLogger(MatchingExperiment.class.getName());


	public IExperimentResult runExperiment(File file, String methodology, String approach,
			List<ISchemaCorrespondence> groundTruth) {
		//	LOGGER.setLevel(Level.INFO);
		for(ISchemaCorrespondence schemaCor :groundTruth){
			System.out.println("Ground Truth: Schema correspondence level:");
			System.out.println("Source schema:"+schemaCor.getSourceSchema().getSchemaName()+" Target schema:"+schemaCor.getTargetSchema().getSchemaName());
		}

		List<ISchema> sourceSchemas = null;
		try {
			sourceSchemas = getAllSourceSchemas(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<ISchema> targetSchemas = null;
		try {
			targetSchemas = getAllTargetSchemas();
		} catch (SchemaMatcherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ISchemaMatcher schemaMatcher =  SchemaMatcherFactory.create(methodology);
		//	List<ISchemaCorrespondence> schemaCorespondencesResult=null;
		ExperimentResult experimentResults=new ExperimentResult();
		int firstAppearance = 0;
		int withinFiveAppearance=0;
		int numberOfMappedAttributes=0;
		int correctlyMappedAttribute=0;
		for(ISchema sourceSchema: sourceSchemas){
			//	System.out.println("Schema Name"+sourceSchema.getSchemaName());

			List<ISchema> source = new ArrayList<ISchema>();
			source.add(sourceSchema);
			List<ISchemaCorrespondence> schemaCorespondencesResult = new ArrayList<ISchemaCorrespondence>();
			long time =System.currentTimeMillis();
			schemaCorespondencesResult= schemaMatcher.matchSchemas(source, targetSchemas, approach);
			long time2 = System.currentTimeMillis();
			System.out.println(time2-time);
			//	List<ISchemaElementCorrespondence> eles = schemaCorespondencesResult.get(0).getSchemaElementCorrespondence();
			//						LOGGER.info("_________________________________________________________________");
			//			
			//						LOGGER.info("Source: "+sourceSchema.getSchemaName());
			//						for(ISchemaCorrespondence sc: schemaCorespondencesResult){
			//							LOGGER.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//			
			//							LOGGER.info("Target: "+sc.getTargetSchema().getSchemaName());
			//							LOGGER.info("Score: "+sc.getSchemaCorrespondenceScore());
			//							LOGGER.info("+++++++++++++++++++++++++++++++++++++");
			//			
			//							LOGGER.info("Element Mapping: "+sc.getTargetSchema().getSchemaName());
			//							List<ISchemaElementCorrespondence> eles = sc.getSchemaElementCorrespondence();
			//			
			//							for(ISchemaElementCorrespondence elc: eles){
			//								LOGGER.info("Source El: "+elc.getSourceElement().getElementContext().getElementName());
			//								LOGGER.info("Target El: "+elc.getTargetElement().getElementContext().getElementName());
			//								LOGGER.info("Scores: "+elc.getElementCorrespondenceScore());
			//			
			//							}
			//			
			//						}




			for (ISchemaCorrespondence  gtSchema : groundTruth){
				List<ISchemaElementCorrespondence> elCor = gtSchema.getSchemaElementCorrespondence();


				if(gtSchema.getSourceSchema().getSchemaName().equals(schemaCorespondencesResult.get(0).getSourceSchema().getSchemaName())){
					if(gtSchema.getTargetSchema().getSchemaName().equals(schemaCorespondencesResult.get(0).getTargetSchema().getSchemaName())){
						firstAppearance++;
						withinFiveAppearance++;
						continue;
					}

				}

				for (int i=1; i<5;i++){
					if(gtSchema.getSourceSchema().getSchemaName().equals(schemaCorespondencesResult.get(i).getSourceSchema().getSchemaName())){
						if(gtSchema.getTargetSchema().getSchemaName().equals(schemaCorespondencesResult.get(i).getTargetSchema().getSchemaName())){
							withinFiveAppearance++;
						}
					}
				}



			}

			for (ISchemaCorrespondence  gtSchema : groundTruth){
				List<ISchemaElementCorrespondence> gtCorrs =  gtSchema.getSchemaElementCorrespondence();


				if(gtSchema.getSourceSchema().getSchemaName().equals(schemaCorespondencesResult.get(0).getSourceSchema().getSchemaName())){
					System.out.println("gtSchemaSource: "+gtSchema.getSourceSchema().getSchemaName());
					System.out.println("resSchemaSource: "+schemaCorespondencesResult.get(0).getSourceSchema().getSchemaName());

					for (ISchemaCorrespondence resultSchema :schemaCorespondencesResult){
						if(	gtSchema.getTargetSchema().getSchemaName().equals(resultSchema.getTargetSchema().getSchemaName())){
//							System.out.println("gtSchemaSource: "+gtSchema.getTargetSchema().getSchemaName());
//							System.out.println("resSchemaSource: "+resultSchema.getTargetSchema().getSchemaName());
							List<ISchemaElementCorrespondence> elCorGt =  gtSchema.getSchemaElementCorrespondence();
							List<ISchemaElementCorrespondence> elCorRes  = resultSchema.getSchemaElementCorrespondence();
							
							for(ISchemaElementCorrespondence elGt: elCorGt){
								
								if(elGt.getTargetElement()!=null){
//									System.out.println("elGtSource: "+elGt.getSourceElement().getElementContext().getElementName());
//									System.out.println("elGtTarget: "+elGt.getTargetElement().getElementContext().getElementName());
									for(ISchemaElementCorrespondence elRes: elCorRes){
										if(elGt.getSourceElement().getElementContext().getElementName().equals(elRes.getSourceElement().getElementContext().getElementName()))
										{	System.out.println("elGtSource: "+elGt.getSourceElement().getElementContext().getElementName());
										System.out.println("elGtTarget: "+elGt.getTargetElement().getElementContext().getElementName());
										System.out.println("elResSource: "+elRes.getSourceElement().getElementContext().getElementName());
										System.out.println("elResTarget: "+elRes.getTargetElement().getElementContext().getElementName());
										if(elGt.getTargetElement().getElementContext().getElementName().equals(elRes.getTargetElement().getElementContext().getElementName())){
											{	System.out.println("Mapped correctly!: ");
											correctlyMappedAttribute++;}
										}

										}
									}
								
								numberOfMappedAttributes++;
								}
							}
						}
					}
				}
				for (ISchemaCorrespondence resultSchema :schemaCorespondencesResult){



					if(gtSchema.getTargetSchema().getSchemaName().equals(resultSchema.getSourceSchema().getSchemaName())){
						//	List<ISchemaElementCorrespondence> gtCorrs =  gtSchema.getSchemaElementCorrespondence();
						List<ISchemaElementCorrespondence> resCorrs = resultSchema.getSchemaElementCorrespondence();
						for  (ISchemaElementCorrespondence rsel: resCorrs){
							for(ISchemaElementCorrespondence sel: gtCorrs ){
								if(sel.getTargetElement()!=null){
									System.out.println("source: "+sel.getSourceElement().getElementContext().getElementName()+
											" target: "+sel.getTargetElement().getElementContext().getElementName());}
							}
						}
					}
				}
			}


			//			System.out.println("Source name:"+schemaCorespondencesResult.get(0).getSourceSchema().getSchemaName());
			//			System.out.println("Target name:"+schemaCorespondencesResult.get(0).getTargetSchema().getSchemaName());
			//			System.out.println("Score: "+schemaCorespondencesResult.get(0).getSchemaCorrespondenceScore());
			//
			//			System.out.println("Target name:"+schemaCorespondencesResult.get(1).getTargetSchema().getSchemaName());
			//			System.out.println("Score: "+schemaCorespondencesResult.get(1).getSchemaCorrespondenceScore());
			//
			//			System.out.println("Target name:"+schemaCorespondencesResult.get(2).getTargetSchema().getSchemaName());
			//			System.out.println("Score: "+schemaCorespondencesResult.get(2).getSchemaCorrespondenceScore());
			//
			//			System.out.println("Target name:"+schemaCorespondencesResult.get(3).getTargetSchema().getSchemaName());
			//			System.out.println("Score: "+schemaCorespondencesResult.get(3).getSchemaCorrespondenceScore());
			//
			//			System.out.println("Target name:"+schemaCorespondencesResult.get(4).getTargetSchema().getSchemaName());
			//			System.out.println("Appearance: "+firstAppearance);
			//
			//			System.out.println("___________");
			//experimentResults =  compareSchemaCorrespondences(groundTruth, schemaCorespondencesResult.get(0));

		}
		int datasetSize = groundTruth.size();
		System.out.println("Appearance: "+firstAppearance);
		System.out.println("Within 5: "+withinFiveAppearance);
		System.out.println("Number of Mapped attribute in GT: "+numberOfMappedAttributes);
		System.out.println("Correctly mapped Attributes: "+ correctlyMappedAttribute);
		float attrRate = (float)correctlyMappedAttribute/(float)numberOfMappedAttributes;
		System.out.println("Correctly mapped Attributes rates: " +attrRate);
		
		double precisionFive = (double)withinFiveAppearance/(double)datasetSize;
		double precision = (double)firstAppearance/(double)datasetSize;
		experimentResults.setPrecisionFive(precisionFive);
		experimentResults.setPrecision(precision);
		//System.out.println("Within 5 precision: "+experimentResults.getPrecision());
		return experimentResults;
	}

	//	private ExperimentResult compareSchemaCorrespondences(
	//			List<ISchemaCorrespondence> groundTruth,
	//			ISchemaCorrespondence scres) {
	//		ExperimentResult er = new ExperimentResult();
	//		int correctSchemaDetection=0;
	//		//int schemaCorrespondenceNum = scres.size();
	//		for (ISchemaCorrespondence scgt: groundTruth)
	//		{
	//			String gtSchemaSourceName=	scgt.getSourceSchema().getSchemaName();
	//			String gtSchemaTargetName = scgt.getTargetSchema().getSchemaName();
	//			//	for(ISchemaCorrespondence scres: schemaCorespondencesResult){
	//
	//			if(scres.getSourceSchema().getSchemaName().equals(gtSchemaSourceName)){
	//				if(scgt.getTargetSchema().getSchemaName().equals(scres.getTargetSchema().getSchemaName()))//bullshit 
	//				{
	//					correctSchemaDetection++;
	//					System.out.println("GT source: "+gtSchemaSourceName);
	//					System.out.println("GT Etype:" +gtSchemaTargetName);
	//
	//					System.out.println("Result source: "+scres.getSourceSchema().getSchemaName());
	//					System.out.println("Result etype: "+scres.getTargetSchema().getSchemaName());
	//					System.out.println("_______________________________");
	//					//computeElementMapping(sc,scres);
	//					//}
	//				}
	//			}
	//
	//
	//		}
	//		System.out.println(correctSchemaDetection);
	//		double precision = correctSchemaDetection;
	//		er.setPrecision(precision);
	//
	//		return null;
	//	}

	private void computeElementMapping(ISchemaCorrespondence sc,
			ISchemaCorrespondence scres) {
		// TODO Auto-generated method stub

	}

	public List<String> experimentMethodologies() {
		return null;
	}

	public List<String> matchingApproach() {
		return null;
	}

	public static List<ISchema> getAllSourceSchemas(final File folder) throws IOException {
		List<ISchema> schemas = new ArrayList<ISchema>();
		SchemaImport si = new SchemaImport();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				getAllSourceSchemas(fileEntry);
			} else {
				schemas.add(si.parseCSV(fileEntry));
			}
		}
		return schemas;
	}

	public static List<ISchema> getAllTargetSchemas() throws SchemaMatcherException{
		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypeList = etypeService.getAllEntityTypes();
		List<ISchema> targetSchemas = new ArrayList<ISchema>();
		SchemaImport si = new SchemaImport();

		for (IEntityType etype:etypeList){
			ISchema schemaEtype=si.extractSchema(etype, Locale.ITALIAN);
			targetSchemas.add(schemaEtype);
		}
		return targetSchemas;
	}

}
