package eu.trentorise.opendata.schemamatcher.implementation.services;

import java.io.File;
import java.io.IOException;

import org.encog.mathutil.Equilateral;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldArray1D;
import org.encog.util.normalize.input.InputFieldCSV;
import org.encog.util.normalize.output.OutputField;
import org.encog.util.normalize.output.OutputFieldDirect;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.output.nominal.OutputOneOf;
import org.encog.util.normalize.target.NormalizationStorageCSV;
import org.encog.util.obj.SerializeObject;
import org.encog.util.simple.EncogUtility;

public class ANNElementMatcher {

	static InputField element;	
	static double INPUT1[] ={ 0.0, 0.0, 0.4 };
	static double INPUT2[] ={ 1.0, 0.0, 0.5 };
	static double INPUT3[] ={ 1.0, 1.0, 0.1 };
	static double INPUTEls[] ={ 1, 2, 3 };
	
	static InputField input1;
	static InputField input2;
	static InputField input3;
	
	public static void createANN() throws IOException{

	
		
		
//	//	double INPUT[][] ={
//				{ 0.0, 0.0, 0.4 },
//				{ 1.0, 0.0, 0.5  }, 
//				{ 0.0, 1.0, 0.7  }, 
//				{ 1.0, 1.0, 0.1  } };

		//	double OUTPUT_IDEAL[][] = {{ -1,-1,-1,1}, { -1,-1,1,-1 }, {-1,1,-1,-1 }, { 1,-1,-1,-1} };

//		OutputOneOf outType = new OutputOneOf(1.0,0.0);




//		OutputOneOf oon = new OutputOneOf();

		BasicNetwork network = new BasicNetwork(); 
		network.addLayer(new BasicLayer(2)); 
		network.addLayer(new BasicLayer(2)); 
		network.addLayer(new BasicLayer(1)); 
		network.getStructure().finalizeStructure(); 
		network.reset();

		//		NeuralDataSet trainingSet = new 
		//				BasicNeuralDataSet(INPUT, OUTPUT_IDEAL);

		BufferedMLDataSet trainingSet  = createOutput(network); 

		final Train train = new ResilientPropagation(network, 
				trainingSet);

		int epoch = 1;

		do { 
			train.iteration(); 
			System.out.println("Epoch #" + epoch + " Error:" 
					+ train.getError()); 
			epoch++; 
		} while(train.getError() > 0.01);

		System.out.println("Neural Network Results:");

		for(MLDataPair pair: trainingSet ) { 
			final MLData output = 
					network.compute(pair.getInput()); 
			System.out.println(pair.getInput().getData(0) 
					+ "," + pair.getInput().getData(1) 
					+ ", actual=" + output.getData(0) + ",ideal=" + 
					pair.getIdeal().getData(0)); 

		}

	}

	public static void buildOutputOneOf(DataNormalization norm)
	{
		InputField element=new InputFieldArray1D(true, INPUTEls);
		OutputOneOf outType = new OutputOneOf(0.0,1.0);
		outType.addItem(element, 1);
		outType.addItem(element, 2);
		outType.addItem(element, 3);
		
		norm.addOutputField(outType, true);
	}

	public static BufferedMLDataSet createOutput(BasicNetwork network) throws IOException{

		DataNormalization norm = new DataNormalization();
		buildOutputOneOf(norm);
                File outputDir = new File("output");
                outputDir.mkdir();
		File csvFile = new File("output/csvfile.csv");
                
		norm.setTarget(new NormalizationStorageCSV(csvFile));

		norm.addInputField(input1 = new InputFieldArray1D(true, INPUT1));
		norm.addInputField(input2 =new InputFieldArray1D(true, INPUT2));
		norm.addInputField( input3 = new InputFieldArray1D(true, INPUT3));
		OutputField outputField1 = new OutputFieldDirect(input1);
		OutputField outputField2 = new OutputFieldDirect(input2);
		OutputField outputField3 = new OutputFieldDirect(input3);

		norm.addOutputField(outputField1, false );
		norm.addOutputField(outputField2, false );
		norm.addOutputField(outputField3, false );


		
		norm.process();

                
		File binFile = new File("output/binFile");

		EncogUtility.convertCSV2Binary(csvFile,
				binFile, network.getInputCount(), 
				network.getOutputCount(), false);

		BufferedMLDataSet trainingSet = new BufferedMLDataSet(
				binFile);
		//return norm;
		//		SerializeObject.save(binFile, norm);

		//		double[] input = new double[norm.getInputFields().size()];
		//		MLData inputData = norm.buildForNetworkInput(input);
		//		MLData output = network.compute(inputData);



		return trainingSet;
	}

}
