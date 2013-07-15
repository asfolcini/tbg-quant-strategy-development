/*
 * TBG-QUANT
 * The Bonnot Gang Quantitative Trading Framework 
 *  
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */
package examples.ml.neuralBox;

import java.text.DecimalFormat;
import java.util.Iterator;

import com.tbg.ml.neuralbox.DataSet;
import com.tbg.ml.neuralbox.NeuralBox;

/** 
 * NeuralBox and DataSet tests <br>
 * <br>
 * <b>History:</b><br>
 *  - [17/giu/2013] Created. (sfl)<br>
 *
 *  @author Alberto Sfolcini 
 */
public class NeuralBoxTest {

	
	/** **************************************************************************
	 * Testing AND problem
	 */
	public static void TestAND(){
		
		Double[] input1 = {0.0 , 0.0 , 1.0 , 1.0 , 0.0 , 1.0 , 0.0 , 1.0};
		Double[] input2 = {0.0 , 1.0 , 0.0 , 1.0 , 0.0 , 0.0 , 1.0 , 1.0};
		Double[] ideal1 = {0.0 , 0.0 , 0.0 , 1.0};
		
		DataSet ds = new DataSet();
		ds.addInputDataSet("Input1", input1);
		ds.addInputDataSet("Input2", input2);
		ds.addIdealDataSet("Ideal", ideal1);
		
		System.out.println(ds.displayDataSet());
		
		ds.setDataSetSplitParameters(100, 0, 0);
		DataSet trainingSet = ds.getTrainingSet();
		System.out.println(trainingSet.displayDataSet());
		
		DataSet actualSet = ds.getActualDataSet();
		System.out.println(actualSet.displayDataSet());
		
		NeuralBox nn = new NeuralBox(2,3,0,1);
		
		System.out.println("Network training.. .");
		double error = nn.train(trainingSet, 1000000, false);
		System.out.println("done with error="+error);
		
		try {
			nn.predict(actualSet);
			System.out.println(actualSet.displayDataSet());					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nn.shutdown();
		
	}
	
	/** **************************************************************************
	 * Testing a+b+c/3
	 */
	public static void Test3(){
		// doubles
		DataSet ds = new DataSet();
		
		Double[] a = new Double[105];
		Double[] b = new Double[105];
		Double[] c = new Double[105];
		Double[] ideal = new Double[100];
		
		// Build a DataSet
		for(int i=0;i<105;i++){
			a[i] = Math.random()*33;
			b[i] = Math.random()*10;
			c[i] = Math.random()*2;
			if (i<100)
					ideal[i] = (a[i]+b[i]+c[i])/3;
		}
		
		
		ds.addInputDataSet("Input1", a);
		ds.addInputDataSet("Input2", b);
		ds.addInputDataSet("Input3", c);
		ds.addIdealDataSet("Ideal1", ideal);		
		
		System.out.println("===== ORIGINAL (Denormalized) DATASET =====");
		System.out.println(ds.displayDataSet());
		
		System.out.println(ds.displayNormalizeParameters());
		
		System.out.println("===== NORMALIZED DATASET =====");
		try {
			ds.normalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println(ds.displayDataSet());
								
		System.out.println("===== NORMALIZATION PARAMETERS =====");
		System.out.println(ds.displayDataSetSplitParameters());
		ds.setDataSetSplitParameters(60, 20, 20);
		System.out.println(ds.displayDataSetSplitParameters());
		
		// TRAININGSET
		DataSet dsTraining = ds.getTrainingSet();
		System.out.println("\n===== TRAINING SET =====");
		System.out.println(dsTraining.displayDataSet());
		
		// CROSS-VALIDATION SET
		DataSet dsCrossValidation = ds.getCrossValidationSet();
		System.out.println("===== CROSS-VALIDATION SET =====");
		System.out.println(dsCrossValidation.displayDataSet(10));

		// TESTING SET
		DataSet dsTesting = ds.getTestingSet();
		System.out.println("===== TESTING SET =====");
		System.out.println(dsTesting.displayDataSet(10));

		// ACTUAL SET
		DataSet dsActual = ds.getActualDataSet();
		System.out.println("===== ACTUAL DATA SET =====");
		System.out.println(dsActual.displayDataSet(10));

		System.out.println("Actual DataSet is Normalized ? "+ dsActual.isNormalized()+"\n");
						
		
		NeuralBox nn = new NeuralBox(3,4,0,1);
		
		System.out.print("=> Training...");
		double training_error = nn.train(dsTraining, 100000, false);
		System.out.println("done with error: "+training_error);				

		System.out.print("=> CrossValidation...");
		double cv_error = nn.train(dsCrossValidation, 100000, false);
		System.out.println("done with error: "+cv_error);				

		System.out.print("=> Testing...");
		double test_error = nn.train(dsTesting, 100000, false);
		System.out.println("done with error: "+test_error);				

		
		System.out.println("\nPREDICTION...");
		try {
			dsActual = nn.predict(dsActual);
			System.out.println(dsActual.displayDataSet());
			try {
				dsActual.denormalize();
				System.out.println(dsActual.displayDataSet());				
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		nn.shutdown();
	}
	

	/**
	 * HugeDS  1ml inputs 
	 */
	private static void TestHugeDataSet(){
		
		DataSet ds = new DataSet();
		
		Double[] in1 = new Double[100010];
		Double[] in2 = new Double[100010];
		Double[] in3 = new Double[100010];
		Double[] in4 = new Double[100010];
		Double[] in5 = new Double[100010];
		Double[] in6 = new Double[100010];
		Double[] in7 = new Double[100010];
		Double[] in8 = new Double[100010];
		Double[] in9 = new Double[100010];
		Double[] in10 = new Double[100010];
		Double[] ideal = new Double[100000];
		
		// Build a DataSet
		for(int i=0;i<100010;i++){
			in1[i] = Math.random()*10;
			in2[i] = Math.random()*10;
			in3[i] = Math.random()*10;
			in4[i] = Math.random()*10;
			in5[i] = Math.random()*10;
			in6[i] = Math.random()*10;
			in7[i] = Math.random()*10;
			in8[i] = Math.random()*10;
			in9[i] = Math.random()*10;
			in10[i] = Math.random()*10;			
			if (i<100000)
					ideal[i] = (in1[i]+in2[i]+in3[i]+in4[i]+in5[i]+in6[i]+in7[i]+in8[i]+in9[i]+in10[i])/10;
		}
		
		
		ds.addInputDataSet("Input1", in1);
		ds.addInputDataSet("Input2", in2);
		ds.addInputDataSet("Input3", in3);
		ds.addInputDataSet("Input4", in4);
		ds.addInputDataSet("Input5", in5);
		ds.addInputDataSet("Input6", in6);
		ds.addInputDataSet("Input7", in7);
		ds.addInputDataSet("Input8", in8);
		ds.addInputDataSet("Input9", in9);
		ds.addInputDataSet("Input10", in10);
		ds.addIdealDataSet("Ideal1", ideal);		

		System.out.println("===== ORIGINAL (Denormalized) DATASET =====");
		System.out.println(ds.displayDataSet(20));

		System.out.println(ds.displayNormalizeParameters());
		
		try{
			if (!ds.isNormalized())
				ds.normalize();
				System.out.println(ds.displayDataSet(20));
		}catch(Throwable e){
			e.printStackTrace();
		}
		
		ds.setDataSetSplitParameters(100, 0, 0);
		System.out.println(ds.displayDataSetSplitParameters());
		
		// TRAININGSET
		DataSet dsTraining = ds.getTrainingSet();
		
		System.out.println("=> Training...");
		NeuralBox nn = new NeuralBox(10,16,0,1);
		double error = nn.train(dsTraining, 100, true);
		System.out.println("Jtrain(x)="+error);
		
		
		
		System.out.println("\nPREDICTION...");
		DataSet dsActual = new DataSet();
		dsActual = ds.getActualDataSet();
		try {
			dsActual = nn.predict(dsActual);
			System.out.println(dsActual.displayDataSet());
			try {
				dsActual.denormalize();
				System.out.println(dsActual.displayDataSet());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		nn.shutdown();
		
	}
	
	/** **************************************************************************
	 * Testing MultiTrain ( avoid bad local minima )
	 */
	public static void TestMultiTrain(){
		// doubles
		DataSet ds = new DataSet();
		
		Double[] a = new Double[105];
		Double[] b = new Double[105];
		Double[] c = new Double[105];
		Double[] ideal = new Double[100];
		
		// Build a DataSet
		for(int i=0;i<105;i++){
			a[i] = Math.random()*33;
			b[i] = Math.random()*10;
			c[i] = Math.random()*2;
			if (i<100)
					ideal[i] = (a[i]+b[i]+c[i])/3;
		}
		
		
		ds.addInputDataSet("Input1", a);
		ds.addInputDataSet("Input2", b);
		ds.addInputDataSet("Input3", c);
		ds.addIdealDataSet("Ideal1", ideal);		
		
		System.out.println("===== ORIGINAL (Denormalized) DATASET =====");
		System.out.println(ds.displayDataSet());
		
		System.out.println(ds.displayNormalizeParameters());
		
		System.out.println("===== NORMALIZED DATASET =====");
		try {
			ds.normalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println(ds.displayDataSet());
								
		System.out.println("===== NORMALIZATION PARAMETERS =====");
		System.out.println(ds.displayDataSetSplitParameters());
		ds.setDataSetSplitParameters(100, 0, 0);
		System.out.println(ds.displayDataSetSplitParameters());
		
		// TRAININGSET
		DataSet dsTraining = ds.getTrainingSet();
		System.out.println("\n===== TRAINING SET =====");
		System.out.println(dsTraining.displayDataSet());
		
		// CROSS-VALIDATION SET
		DataSet dsCrossValidation = ds.getCrossValidationSet();
		System.out.println("===== CROSS-VALIDATION SET =====");
		System.out.println(dsCrossValidation.displayDataSet(10));

		// TESTING SET
		DataSet dsTesting = ds.getTestingSet();
		System.out.println("===== TESTING SET =====");
		System.out.println(dsTesting.displayDataSet(10));

		// ACTUAL SET
		DataSet dsActual = ds.getActualDataSet();
		System.out.println("===== ACTUAL DATA SET =====");
		System.out.println(dsActual.displayDataSet(10));

		System.out.println("Actual DataSet is Normalized ? "+ dsActual.isNormalized()+"\n");
						
		
		NeuralBox nn = new NeuralBox(3,4,0,1);
		
		System.out.print("=> Training...\n");
		//double training_error = nn.train(dsTraining, 10000, false);
		double training_error = nn.multipleTrain(dsTraining, 10000, false,5);
		System.out.println("done with error: "+training_error);				

		
		System.out.println("\nPREDICTION...");
		try {
			dsActual = nn.predict(dsActual);
			System.out.println(dsActual.displayDataSet());
			try {
				dsActual.denormalize();
				System.out.println(dsActual.displayDataSet());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		nn.shutdown();
		
		
		// Setting the right values and see the difference with prediction values
		double[][] in = dsActual.getInputDataSet();
		Double[] ideal_val = new Double[in.length];
		double sum = 0.0;
		for(int i=0;i<in.length;i++){			
			for(int j=0;j<in[i].length;j++){				
				sum += in[i][j];
			}
			ideal_val[i]=sum/dsActual.getInputsCount();
			sum = 0.0;			
		}					
		dsActual.addIdealDataSet("Ideal1", ideal_val);		
		System.out.println(dsActual.displayDataSet());
		
		
		// show differences
		double[][] ideals = dsActual.getIdealDataSet();
		double[][] predicted = dsActual.getPredictedDataSet();
		DecimalFormat df1 = new DecimalFormat("#########0.0000000000");
		String ideal_head = "";
		String pred_head = "";
		String diff_head = "";
		for ( Iterator<String> iter = dsActual.getIdealKeySet().iterator(); iter.hasNext(); ) {
		    String _str = iter.next();
			ideal_head += _str+"\t\t";
		    pred_head += "Predicted_"+_str+"\t\t";
		    diff_head += "Predicted_vs_"+_str+"\t\t";
		}
		System.out.println(ideal_head+pred_head+diff_head);
		for(int i=0;i<ideals.length;i++){
			for(int j=0;j<ideals[i].length;j++){				
				double id = ideals[i][j];
				double pr = predicted[i][j];
				System.out.println(df1.format(id)+"\t"+df1.format(pr)+"\t\t"+df1.format((id-pr)));
			}
		}
		
	}
	
	
	
	public static void TestMultipleOutput(){
		// doubles
		DataSet ds = new DataSet();
		
		Double[] a = new Double[105];
		Double[] b = new Double[105];
		Double[] c = new Double[105];
		Double[] ideal1 = new Double[100];  // average (a+b+c)/3
		Double[] ideal2 = new Double[100];  // sum (a+b+c)
		
		// Build a DataSet
		for(int i=0;i<105;i++){
			a[i] = Math.random()*33;
			b[i] = Math.random()*10;
			c[i] = Math.random()*2;
			if (i<100){
				ideal1[i] = (a[i]+b[i]+c[i])/3;
				ideal2[i] = (a[i]+b[i]+c[i]);
			}
		}
		
		
		ds.addInputDataSet("Input1", a);
		ds.addInputDataSet("Input2", b);
		ds.addInputDataSet("Input3", c);
		ds.addIdealDataSet("Ideal1", ideal1);
		ds.addIdealDataSet("Ideal2", ideal2);
		
		System.out.println("===== ORIGINAL (Denormalized) DATASET =====");
		System.out.println(ds.displayDataSet());
		
		System.out.println(ds.displayNormalizeParameters());
		
		System.out.println("===== NORMALIZED DATASET =====");
		try {
			ds.normalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println(ds.displayDataSet());
								
		System.out.println("===== NORMALIZATION PARAMETERS =====");
		System.out.println(ds.displayDataSetSplitParameters());
		ds.setDataSetSplitParameters(100, 0, 0);
		System.out.println(ds.displayDataSetSplitParameters());
		
		// TRAININGSET
		DataSet dsTraining = ds.getTrainingSet();
		System.out.println("\n===== TRAINING SET =====");
		System.out.println(dsTraining.displayDataSet());
		
		// ACTUAL SET
		DataSet dsActual = ds.getActualDataSet();
		System.out.println("===== ACTUAL DATA SET =====");
		System.out.println(dsActual.displayDataSet(10));

		System.out.println("Actual DataSet is Normalized ? "+ dsActual.isNormalized()+"\n");
						
		
		NeuralBox nn = new NeuralBox(3,4,0,2);
		
		System.out.print("=> Training...\n");
		//double training_error = nn.train(dsTraining, 10000, false);
		double training_error = nn.multipleTrain(dsTraining, 10000, false,10);
		System.out.println("done with error: "+training_error);				

		
		System.out.println("\nPREDICTION...");
		try {
			dsActual = nn.predict(dsActual);
			System.out.println(dsActual.displayDataSet());
			try {
				dsActual.denormalize();
				System.out.println(dsActual.displayDataSet());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		nn.shutdown();
		
		
		// Setting the right values and see the difference with prediction values
		double[][] in = dsActual.getInputDataSet();
		Double[] ideal_val1 = new Double[in.length];
		Double[] ideal_val2 = new Double[in.length];
		double sum = 0.0;
		for(int i=0;i<in.length;i++){			
			for(int j=0;j<in[i].length;j++){				
				sum += in[i][j];
			}
			ideal_val1[i]=sum/dsActual.getInputsCount();
			ideal_val2[i]=sum;
			sum = 0.0;			
		}					
		dsActual.addIdealDataSet("Ideal1", ideal_val1);
		dsActual.addIdealDataSet("Ideal2", ideal_val2);
		System.out.println(dsActual.displayDataSet());
		
		
	}
	
	
	public static void TestDataSet(){
		
		DataSet ds = new DataSet();
		
		Double[] a = new Double[110];
		Double[] b = new Double[110];
		Double[] c = new Double[110];
		Double[] d = new Double[110];
		Double[] e = new Double[110];
		Double[] ideal1 = new Double[100];
		
		// Build a DataSet
		for(int i=0;i<110;i++){
			a[i] = Math.floor(Math.random()*1000);
			b[i] = Math.floor(Math.random()*200);
			c[i] = Math.floor(Math.random()*16);
			d[i] = Math.random()*7;
			e[i] = Math.floor(Math.random()*10);			
			if (i<100){
				ideal1[i] = ((a[i]+b[i]+c[i]+d[i])/3)*e[i];				
			}
		}
		
		ds.addInputDataSet("Input1", a);
		ds.addInputDataSet("Input2", b);
		ds.addInputDataSet("Input3", c);
		ds.addInputDataSet("Input4", d);
		ds.addInputDataSet("Input5", e);
		
		ds.addIdealDataSet("Ideal", ideal1);
		
		System.out.println("===== ORIGINAL (Denormalized) DATASET =====");
		System.out.println(ds.displayDataSet());
		
		System.out.println("===== NORMALIZATION PARAMETERS =====");
		System.out.println(ds.displayNormalizeParameters());
		
		System.out.println("===== NORMALIZED DATASET =====");
		try {
			ds.normalize();
			System.out.println(ds.displayDataSet());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		System.out.println(ds.displayDataSetInfo()+"\n");
		
		System.out.println("===== DATASET SPLIT PARAMETERS =====");
		System.out.println(ds.displayDataSetSplitParameters());
		
		// TRAININGSET
		DataSet dsTraining = ds.getTrainingSet();
		System.out.println("\n===== TRAINING SET =====");
		System.out.println(dsTraining.displayDataSet());
		
		// CROSS-VALIDATION SET
		DataSet dsCrossValidation = ds.getCrossValidationSet();
		System.out.println("===== CROSS-VALIDATION SET =====");
		System.out.println(dsCrossValidation.displayDataSet());

		// TESTING SET
		DataSet dsTesting = ds.getTestingSet();
		System.out.println("===== TESTING SET =====");
		System.out.println(dsTesting.displayDataSet());

		// ACTUAL SET
		DataSet dsActual = ds.getActualDataSet();
		System.out.println("===== ACTUAL DATA SET =====");
		System.out.println(dsActual.displayDataSet());		
		
	}
	
	
	public static void TestCsvDataSet(){
		
		DataSet ds = new DataSet();
		String csvFilePath = "C:\\temp\\SynchData\\SPY_Multi.csv";
		String separator = ";";
		Double[] input1 = DataSet.loadCSVColumn(csvFilePath, separator, 5);
		Double[] input2 = DataSet.loadCSVColumn(csvFilePath, separator, 6);
		Double[] input3 = DataSet.loadCSVColumn(csvFilePath, separator, 7);
		Double[] ideal1 = DataSet.loadCSVColumn(csvFilePath, separator, 8);
		
		ds.addInputDataSet("Volume(t)", input1);
		ds.addInputDataSet("Yield(t-1)", input2);
		ds.addInputDataSet("SMA50(t)", input3);
		
		ds.addIdealDataSet("Yield(t+1)", ideal1);
		
		System.out.println(ds.displayDataSet(20));
		System.out.println(ds.displayDataSetInfo());
		
		try {
			ds.normalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.out.println(ds.displayDataSet(20));
				
		ds.setDataSetSplitParameters(100, 0, 0);
		DataSet dsTraining = ds.getTrainingSet();
		
		NeuralBox nn = new NeuralBox(3,4,0,1);
		System.out.print("\n==> Training...");
		double error = nn.train(dsTraining, 100000, false);
		System.out.println("Network trained with error="+error);
		
		DataSet dsActual = ds.getActualDataSet(); 
		
		try {
			dsActual = nn.predict(dsActual);
			System.out.println(dsActual.displayDataSet());			
			try {				
				dsActual.denormalize();
				System.out.println(dsActual.displayDataSet());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		nn.shutdown();
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		/**
		 * Testing DataSet
		 */
		//TestDataSet();
		
		/**
		 *  Learning the AND operation
		 */
		//TestAND();		
		
		/**
		 * Learning (a+b+c)/3
		 */
		//Test3();
		
		/**
		 * MultiTrain, trying to avoid local minima, learning (a+b+c)/3
		 */
		TestMultiTrain();

		/**
		 * Testing big dataSet
		 */
		//TestHugeDataSet();
		
		/**
		 * Testing multiple outputs
		 */
		//TestMultipleOutput();
		
		/**
		 * Load DataSet from CSV
		 */
		//TestCsvDataSet();
		
	}

}
