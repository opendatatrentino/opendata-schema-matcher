

	/*
	 * Encog(tm) Java Examples v3.3
	 * http://www.heatonresearch.com/encog/
	 * https://github.com/encog/encog-java-examples
	 *
	 * Copyright 2008-2014 Heaton Research, Inc.
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *     http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 *   
	 * For more information on Heaton Research copyrights, licenses 
	 * and trademarks visit:
	 * http://www.heatonresearch.com/copyright
	 */
	package eu.trentorise.opendata.schemamatcher.implementation.services;

	import java.io.File;

	import org.encog.StatusReportable;
	import org.encog.util.normalize.DataNormalization;
	import org.encog.util.normalize.input.InputField;
	import org.encog.util.normalize.input.InputFieldCSV;
	import org.encog.util.normalize.output.OutputField;
	import org.encog.util.normalize.output.OutputFieldDirect;
	import org.encog.util.normalize.output.OutputFieldRangeMapped;
	import org.encog.util.normalize.output.nominal.OutputEquilateral;
	import org.encog.util.normalize.output.nominal.OutputOneOf;
	import org.encog.util.normalize.segregate.IntegerBalanceSegregator;
	import org.encog.util.normalize.segregate.index.IndexSampleSegregator;
	import org.encog.util.normalize.target.NormalizationStorageCSV;

	public class ANNDataNormalization {
		
		
		public void buildOutputOneOf(DataNormalization norm, InputField coverType)
		{
			OutputOneOf outType = new OutputOneOf(1.0,0.0);
			outType.addItem(coverType, 1);
			outType.addItem(coverType, 2);
			outType.addItem(coverType, 3);
			outType.addItem(coverType, 4);
			outType.addItem(coverType, 5);
			outType.addItem(coverType, 6);
			outType.addItem(coverType, 7);
			norm.addOutputField(outType, true);
		}
		
		public void buildOutputEquilateral(DataNormalization norm, InputField coverType)
		{
			OutputEquilateral outType = new OutputEquilateral();
			outType.addItem(coverType, 1);
			outType.addItem(coverType, 2);
			outType.addItem(coverType, 3);
			outType.addItem(coverType, 4);
			outType.addItem(coverType, 5);
			outType.addItem(coverType, 6);
			outType.addItem(coverType, 7);
			norm.addOutputField(outType, true);
		}
		
		public void copy(File source,File target,int start,int stop,int size)
		{
			InputField inputField[] = new InputField[55];
			
			DataNormalization norm = new DataNormalization();
			
			norm.setTarget(new NormalizationStorageCSV(target));
			for(int i=0;i<55;i++)
			{
				inputField[i] = new InputFieldCSV(true,source,i);
				norm.addInputField(inputField[i]);
				OutputField outputField = new OutputFieldDirect(inputField[i]);
				norm.addOutputField(outputField);
			}
					
			// load only the part we actually want, i.e. training or eval
			IndexSampleSegregator segregator2 = new IndexSampleSegregator(start,stop,size);
			norm.addSegregator(segregator2);
							
			norm.process();
		}
		
		public void narrow(File source,File target,int field, int count)
		{
			InputField inputField[] = new InputField[55];
			
			DataNormalization norm = new DataNormalization();
			norm.setTarget(new NormalizationStorageCSV(target));
			for(int i=0;i<55;i++)
			{
				inputField[i] = new InputFieldCSV(true,source,i);
				norm.addInputField(inputField[i]);
				OutputField outputField = new OutputFieldDirect(inputField[i]);
				norm.addOutputField(outputField);
			}
					
			IntegerBalanceSegregator segregator = new IntegerBalanceSegregator(inputField[field],count);
			norm.addSegregator(segregator);
							
			norm.process();
			System.out.println("Samples per tree type:");
			System.out.println(segregator.dumpCounts());
		}
		
		public void step1()
		{
			System.out.println("Step 1: Generate training and evaluation files");
			System.out.println("Generate training file");
			System.out.println("Generate evaluation file");
		}
		
		public void step2()
		{
			System.out.println("Step 2: Balance training to have the same number of each tree");
		}
		
		public DataNormalization step3(boolean useOneOf)
		{
			System.out.println("Step 3: Normalize training data");
			InputField inputElevation;
			InputField inputAspect;
			InputField inputSlope;
			InputField hWater;
			InputField vWater;
			InputField roadway;
			InputField shade9;
			InputField shade12;
			InputField shade3;
			InputField firepoint;
			InputField[] wilderness = new InputField[4];
			InputField[] soilType = new InputField[40];
			InputField coverType;	
			
			DataNormalization norm = new DataNormalization();
			
			
			
			
			for(int i=0;i<40;i++)
			{
				norm.addOutputField(new OutputFieldDirect(soilType[i]));
			}
			
//			if( useOneOf )
//				buildOutputOneOf(norm,coverType);
//			else
//				buildOutputEquilateral(norm,coverType);
					
			norm.process();
			return norm;
		}
		
		public void report(int total, int current, String message) {
			System.out.println( current + "/" + total + " " + message );
			
		}
	}

	
