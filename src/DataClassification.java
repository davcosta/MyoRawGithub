import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.primitives.Doubles;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/*
 * FLICKTEK VERSION
 */
public class DataClassification {
	
		private PrintWriter out;
		private int portMobile = 5454;
		private ServerSocket serverSocket;
	
		private String recogGesture ="";
		//store sensor data from file 
		private ArrayList<String[]> dataFromFile = new ArrayList<String[]>();
		
		//Auxiliary buffers to build the windows with overlapped values  
		private CopyOnWriteArrayList<Double> buffer1 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> buffer2 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> buffer3 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> buffer4 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> buffer5 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> buffer6 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> buffer7 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> buffer8 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Integer> bufferG = new CopyOnWriteArrayList<Integer>();
		
		//8 sensor values  
		private CopyOnWriteArrayList<Double> windowValues1 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> windowValues2 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> windowValues3 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> windowValues4 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> windowValues5 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> windowValues6 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> windowValues7 = new CopyOnWriteArrayList<Double>();
		private CopyOnWriteArrayList<Double> windowValues8 = new CopyOnWriteArrayList<Double>();
		
		//labelled gesture associated with 8 sensors values 
		private CopyOnWriteArrayList<Integer> gestureValues = new CopyOnWriteArrayList<Integer>();
		
		//class that extract the features of the window data
		EMGFeatureExtraction features = new EMGFeatureExtraction();
		
		//Wavelength, Wamp, RMS 
		private CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>  RMSValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
		private CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>  WLValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
		private CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>  WAMPValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
		
		private LowPassFilter lp = new LowPassFilter();
		private EMGFeatureExtraction[] fe = {new EMGFeatureExtraction(),new EMGFeatureExtraction(),new EMGFeatureExtraction(),new EMGFeatureExtraction(),new EMGFeatureExtraction(),new EMGFeatureExtraction(),new EMGFeatureExtraction(),new EMGFeatureExtraction()};
		
		
		private CopyOnWriteArrayList<Integer> GestureList = new CopyOnWriteArrayList<>();
		//boolean to check if it is training or testing the classifier
		private boolean isTrainningClassifier = false;
		
		//Usual frequency values 
		public static final int FREQUENCY_1HZ = 1;
		public static final int FREQUENCY_10HZ = 10;
		public static final int FREQUENCY_100HZ = 100;
		public static final int FREQUENCY_1000HZ = 1000;
		
		//Usual low frequency values
		public static final int FREQUENCY_6HZ = 6;
		
		
		//Configuration values
		private int _frequency = 1000;
		private int _window_size = 1024;
		private int _lowpass_frequency = 6;
		private int _threshold = 23;
		private int _overlap = 20;
		private int _seed = 0;
		private int _nFolds = 10;
		private String _classifierName = Classify.RANDOMFOREST;
		private String _path = "DATA/datafromsensor.txt";
		private Classify _classifier;
		private String _options;
		
		//WEKA
		//data structure for the classifier 
		private Instances data; 
		private ArrayList<Attribute> atts;
		private ArrayList<String> gestureNum;
		//values to add an instance of data (to be added to Instances)
		private double[] vals;
		
		private String accuracy = "";
		private Logging saveTrainedInstances;
		private Logging saveTestedInstances;
		private Logging saveFiles;
		public DataClassification()
		{
			
			for(int i = 0; i < 8; i++)
			{
				RMSValues.add(new CopyOnWriteArrayList<Double>());
				WAMPValues.add(new CopyOnWriteArrayList<Integer>());
				WLValues.add(new CopyOnWriteArrayList<Double>());
			}
			
			initiateDataInstances();
			createSocketServerMobile();
		}
		
		private void initiateDataInstances()
		{
			//WEKA
			//Create the data structure for the classifier
			//In this example an instance is composed of energy, low energy and absolute mean values for each 
			//axis. However, for other sensors (EMG, ECG, etc.) these features may differ.
			atts = new ArrayList<Attribute>();
			
			//RMS
			atts.add(new Attribute("RMS_1"));
			atts.add(new Attribute("RMS_2"));
			atts.add(new Attribute("RMS_3"));
			atts.add(new Attribute("RMS_4"));
			atts.add(new Attribute("RMS_5"));
			atts.add(new Attribute("RMS_6"));
			atts.add(new Attribute("RMS_7"));
			atts.add(new Attribute("RMS_8"));
			//WL
			atts.add(new Attribute("WL_1"));
			atts.add(new Attribute("WL_2"));
			atts.add(new Attribute("WL_3"));
			atts.add(new Attribute("WL_4"));
			atts.add(new Attribute("WL_5"));
			atts.add(new Attribute("WL_6"));
			atts.add(new Attribute("WL_7"));
			atts.add(new Attribute("WL_8"));
			//WAMP
			atts.add(new Attribute("WAMP_1"));
			atts.add(new Attribute("WAMP_2"));
			atts.add(new Attribute("WAMP_3"));
			atts.add(new Attribute("WAMP_4"));
			atts.add(new Attribute("WAMP_5"));
			atts.add(new Attribute("WAMP_6"));
			atts.add(new Attribute("WAMP_7"));
			atts.add(new Attribute("WAMP_8"));
			
			//Label classes (in this case the possible gesture that the features represent)
			gestureNum = new ArrayList<String>();
			gestureNum.add("NoGesture");
			gestureNum.add("Fist"); //ENTER
			gestureNum.add("Wave_Out"); //CHANGE WINDOW
			gestureNum.add("Wave_In"); //NEXT
			
			
			
			/*gestureNum.add("Double_Tap"); //ENTER
			gestureNum.add("Fist"); //CHANGE WINDOW
			gestureNum.add("Spread_Fingers"); //ACTIVATE REC
			gestureNum.add("Wave_Out"); //NEXT
			gestureNum.add("Wave_In"); //PREVIOUS
			gestureNum.add("Gesture_1"); //TODO
			gestureNum.add("Gesture_2"); //TODO*/
			
			atts.add(new Attribute("Gesture", gestureNum));
			
			data = new Instances("Gesture_Windows", atts, 0);
		}
		/**
		 * Set values to configure window
		 * @param window_size - power of 2 values
		 * @param overlap - % values between 0 and 99
		 * @param frequency - frequency of data acquisition from the sensor in hertz
		 */
		public void ConfigureWindowValues(int window_size, int overlap, int frequency, int lowpass_frequency, int WAMP_threshold)
		{
			_window_size = window_size;
			_overlap = overlap;		
			_frequency = frequency;
			_lowpass_frequency = lowpass_frequency;
			_threshold = WAMP_threshold;
		}
		private void InitiateBufferLists(){
			//Auxiliary buffers to build the windows with overlapped values  
			buffer1 = new CopyOnWriteArrayList<Double>();
			buffer2 = new CopyOnWriteArrayList<Double>();
			buffer3 = new CopyOnWriteArrayList<Double>();
			buffer4 = new CopyOnWriteArrayList<Double>();
			buffer5 = new CopyOnWriteArrayList<Double>();
			buffer6 = new CopyOnWriteArrayList<Double>();
			buffer7 = new CopyOnWriteArrayList<Double>();
			buffer8 = new CopyOnWriteArrayList<Double>();
			bufferG = new CopyOnWriteArrayList<Integer>();
			
		}
		
		private void FreeBufferLists()
		{
			buffer1 = null;
			buffer2 = null;
			buffer3 = null;
			buffer4 = null;
			buffer5 = null;
			buffer6 = null;
			buffer7 = null;
			buffer8 = null;
			bufferG = null;
			System.gc();
		}
		
		private void InitiateWindowValues(){
			//8 sensor values  
			windowValues1 = new CopyOnWriteArrayList<Double>();
			windowValues2 = new CopyOnWriteArrayList<Double>();
			windowValues3 = new CopyOnWriteArrayList<Double>();
			windowValues4 = new CopyOnWriteArrayList<Double>();
			windowValues5 = new CopyOnWriteArrayList<Double>();
			windowValues6 = new CopyOnWriteArrayList<Double>();
			windowValues7 = new CopyOnWriteArrayList<Double>();
			windowValues8 = new CopyOnWriteArrayList<Double>();
			gestureValues = new CopyOnWriteArrayList<Integer>();
		
		}
		
		private void FreeWindowValues(){
			windowValues1 = null;
			windowValues2 = null;
			windowValues3 = null;
			windowValues4 = null;
			windowValues5 = null;
			windowValues6 = null;
			windowValues7 = null;
			windowValues8 = null;
			gestureValues = null;
			System.gc();
		}
		private void InitiateFeaturesLists(){
			
			
			
			RMSValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
			WAMPValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
			WLValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
			for(int i = 0; i < 8; i++)
			{
				RMSValues.add(new CopyOnWriteArrayList<Double>());
				WAMPValues.add(new CopyOnWriteArrayList<Integer>());
				WLValues.add(new CopyOnWriteArrayList<Double>());
			}
		}
		private void FreeFeaturesLists(){
			RMSValues = null;
			WAMPValues = null;
			WLValues = null;
			System.gc();
		}
		
		/**
		 * Configure parameters for the classifier's training
		 * @param seed - seed value 
		 * @param nFolds - number of folds for cross validation training
		 */
		public void ConfigureClassifierTrainValues( int seed, int nFolds)
		{
			_seed = seed;
			_nFolds = nFolds;
			
		}
		public void SetDataFromList(ArrayList<String[]> list)
		{
			if(dataFromFile == null)
				dataFromFile = new ArrayList<String[]>();
			dataFromFile.addAll(list);
		}
		/**
		 * Set path for the raw data file
		 * @param path
		 */
		public void SetDataFilePath(String path)
		{
			_path = path;
			//ReadFromFile(_path);
		}
		
		/**
		 * Set which classifier will be used
		 * @param classifierName - final values from class Classify
		 */
		public void SetClassifier( String classifierName, String options)
		{
			saveFiles = new Logging("configurationResults"+classifierName);
			_classifierName = classifierName;
			_options = options;
			saveTrainedInstances = new Logging("InstancesTrained");
			saveTestedInstances = new Logging("InstancesTested");
		}
		
		/**
		 * Train the classifier with the data obtained from the file
		 */
		public void TrainClassifier()
		{
			isTrainningClassifier = true;
			//ReadFromFile(_path);
			SetDataFromFileIntoWindows();
			data = MergeDataIntoInstances();
			//System.out.println("TAMANHO DO DATA DEPOIS DO EQUILIBRIO "+data.size());
			_classifier = new Classify(_seed, _nFolds);
			_classifier.TrainClassifier(data, _classifierName, _options);
			_classifier.SaveTrainedClassifier();
			accuracy = _classifier.GetAccuracyResult();
			initiateDataInstances();
		}
		
		/**
		 * Load a classifier and use it for live streaming
		 */
		public void UseClassifier(String classifier)
		{
			isTrainningClassifier = false;
			_classifier = new Classify(_seed, _nFolds);
			_classifier.LoadTrainedClassifier(classifier);
			
		
		}
		
		/**
		 * send values directly from the sensors to the classifier
		 * @param sensor1
		 * @param sensor2
		 * @param sensor3
		 * @param sensor4
		 * @param sensor5
		 * @param sensor6
		 * @param sensor7
		 * @param sensor8
		 */
		public void streamValues(double sensor1, double sensor2, double sensor3, double sensor4,double sensor5, double sensor6, double sensor7, double sensor8)
		{
			addValueToWindow(sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8, 0);
		}
		
		/**
		 * Tests the trained classifier with data from a file (in this case it is the same file from training).
		 * In future implement the data acquisition from live sensor or a file with different data from the train.
		 */
		public void TestClassifier(String classifier)
		{
			isTrainningClassifier = false;
			//_classifier = new Classify(_seed, _nFolds);
			//_classifier.LoadTrainedClassifier(classifier);
			ReadFromFile(_path);
			SetDataFromFileIntoWindows();
			
		}
		
		public void saveConfigurationToFile(String configuration)
		{
			try{
			saveFiles.logData(configuration+", "+accuracy);
			saveFiles.closeFile();
			}catch(Exception e)
			{
				System.out.println("Error "+e);
			}
		}
		
		/**
		 * Read raw data from file
		 * @param path - relative path
		 */
		private void ReadFromFile(String path) {

		    
		    try {
		        FileInputStream inputStream = new FileInputStream(path); //"DATA/datafromsensor.txt"

		        if ( inputStream != null ) {
		            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		            String receiveString = "";
		            
		            int counter = 0;
		            while ( (receiveString = bufferedReader.readLine()) != null )
		            {
		            	
		            	String[] values = receiveString.split(",");
		            	try{
		            		//System.out.println("file: "+ new String[]{values[2], values[3],values[4],values[5],values[6]});
		            		dataFromFile.add(new String[]{values[0], values[1],values[2],values[3],values[4],values[5], values[6],values[7],values[8]}); //save data from file
		            		counter++;
		            	}catch (Exception e){
		            		//System.out.println("line: "+counter);
		            	}
		            }

		            inputStream.close();
		        }
		    }
		    catch (FileNotFoundException e) {
		        System.out.println( "File not found: " + e.toString());
		    } catch (IOException e) {
		        System.out.println("Can not read file: " + e.toString());
		    }
		    
		}
		
		/**
		 * Obtain the raw data and distribute it in windows with defined size and overlap values.
		 * When a window is complete, features are extracted to be sent to the classifier to be trained or labelled.  
		 */
		public void SetDataFromFileIntoWindows()
		{
			InitiateBufferLists();
			InitiateWindowValues();
			
			for(int i = 0; i < dataFromFile.size();i++)
			{
				addValueToWindow(Double.valueOf(dataFromFile.get(i)[0]),Double.valueOf(dataFromFile.get(i)[1]),Double.valueOf(dataFromFile.get(i)[2]),Double.valueOf(dataFromFile.get(i)[3]), Double.valueOf(dataFromFile.get(i)[4]),
						Double.valueOf(dataFromFile.get(i)[5]),Double.valueOf(dataFromFile.get(i)[6]),Double.valueOf(dataFromFile.get(i)[7]), Integer.parseInt(dataFromFile.get(i)[8]));
		
			}
			
			dataFromFile.clear();
			dataFromFile = null;
			
			FreeBufferLists();
			FreeWindowValues();
			
			
		}
		
		/**
		 * Auxiliary function to add the values into windows
		 * @param accX - accelerometer values of X axis 
		 * @param accY - accelerometer values of Y axis
		 * @param accZ - accelerometer values of Z axis
		 * @param gesture - labelled gesture
		 */
		private void addValueToWindow(double sensor1, double sensor2, double sensor3, double sensor4,double sensor5, double sensor6, double sensor7, double sensor8, int gesture)
		{
			
			
			bufferG.add(gesture);
			//Add to buffer the absolute values for linear envelope
			buffer1.add(Math.abs(sensor1));
			buffer2.add(Math.abs(sensor2));
			buffer3.add(Math.abs(sensor3));
			buffer4.add(Math.abs(sensor4));
			buffer5.add(Math.abs(sensor5));
			buffer6.add(Math.abs(sensor6));
			buffer7.add(Math.abs(sensor7));
			buffer8.add(Math.abs(sensor8));
			
			
			//Check if the window is already filled with the defined size
			if(buffer1.size() >= _window_size)
			{
				int i = 0;
				//System.out.println("window size before: "+windowValues1.size());
				for(i = 0; windowValues1.size() <= _window_size-1; i++){
				
					windowValues1.add(buffer1.get(i));
					windowValues2.add(buffer2.get(i));
					windowValues3.add(buffer3.get(i));
					windowValues4.add(buffer4.get(i));
					windowValues5.add(buffer5.get(i));
					windowValues6.add(buffer6.get(i));
					windowValues7.add(buffer7.get(i));
					windowValues8.add(buffer8.get(i));
					gestureValues.add(bufferG.get(i));
				}
				//System.out.println("window size after: "+windowValues1.size());
				//System.out.println("buffer size before: "+buffer1.size());
				for(int j = 0; j < i; j++)
				{
					buffer1.remove(0);
					buffer2.remove(0);
					buffer3.remove(0);
					buffer4.remove(0);
					buffer5.remove(0);
					buffer6.remove(0);
					buffer7.remove(0);
					buffer8.remove(0);
					bufferG.remove(0);
				}
				//System.out.println("buffer size after: "+buffer1.size());
				//ADD FILTERS BEFORE PROCESSING AND CALCULATE FEATURES
				
				
				
				/*Logging lowpassLog = new Logging("LowPassDebug");
				lowpassLog.logData(lp0, lp1, lp2, lp3, lp4, lp5, lp6, lp7);
				lowpassLog.closeFile();*/
				
				fe[0].CalculateFeatures(lp.filterSignal(windowValues1, _frequency, _lowpass_frequency, 2, 0, 0), _threshold);
				fe[1].CalculateFeatures(lp.filterSignal(windowValues2, _frequency, _lowpass_frequency, 2, 0, 0), _threshold);
				fe[2].CalculateFeatures(lp.filterSignal(windowValues3, _frequency, _lowpass_frequency, 2, 0, 0), _threshold);
				fe[3].CalculateFeatures(lp.filterSignal(windowValues4, _frequency, _lowpass_frequency, 2, 0, 0), _threshold);
				fe[4].CalculateFeatures(lp.filterSignal(windowValues5, _frequency, _lowpass_frequency, 2, 0, 0), _threshold);
				fe[5].CalculateFeatures(lp.filterSignal(windowValues6, _frequency, _lowpass_frequency, 2, 0, 0), _threshold);
				fe[6].CalculateFeatures(lp.filterSignal(windowValues7, _frequency, _lowpass_frequency, 2, 0, 0), _threshold);
				fe[7].CalculateFeatures(lp.filterSignal(windowValues8, _frequency, _lowpass_frequency, 2, 0, 0), _threshold);
				
				//SAVE INTO FILE FOR DEBUG PURPOSES
				
				
				//EXTRACT SIGNAL FEATURES
				
				
				//Check if is training or testing
				if(!isTrainningClassifier){
					//if testing from a file or getting live values from a sensor, we should form the instance without a label and send it to be classified  
					
					
					
					DenseInstance ins = FeaturesDatatoInstance(fe[0].getRMS(),fe[1].getRMS(),fe[2].getRMS(),fe[3].getRMS(),fe[4].getRMS(),fe[5].getRMS(),fe[6].getRMS(),fe[7].getRMS(),
							fe[0].getWL(),fe[1].getWL(),fe[2].getWL(),fe[3].getWL(),fe[4].getWL(),fe[5].getWL(),fe[6].getWL(),fe[7].getWL(),
							fe[0].getWAMP(),fe[1].getWAMP(),fe[2].getWAMP(),fe[3].getWAMP(),fe[4].getWAMP(),fe[5].getWAMP(),fe[6].getWAMP(),fe[7].getWAMP());
					
					
					//System.out.println("isntance "+ins.toString());
					data.clear();
					
					/*try {
						saveTestedInstances.logData(ins.toString());
						saveTestedInstances.closeFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					ins.setDataset(data);
					data.add(ins);
					
					//classify Instance
					int resultIndex =  _classifier.ClassifyInstance(data);
					System.out.println("Result: "+data.attribute(data.numAttributes()-1).value(resultIndex));
					setRecogGesture(data.attribute(data.numAttributes()-1).value(resultIndex));
                    sendGestureToMobileClient(getRecogGesture());
					
				}else{
					//if training we should keep filling the instances with labelled data until the file is read
					//System.out.println("size of windows 1:"+ windowValues1.size() +" windows 2 "+windowValues2.size()+ " windows 3 "+ windowValues3.size()+" windows 4" + windowValues4.size());
					for(int sensor = 0; sensor < 8; sensor++)
					{
						
						RMSValues.get(sensor).add(fe[sensor].getRMS());
						WLValues.get(sensor).add(fe[sensor].getWL());
						WAMPValues.get(sensor).add(fe[sensor].getWAMP());
						//System.out.println(RMSValues.get(0).size());
						
					}
					
					
					
					
				}
				//System.out.println("size "+AverageAmplitudeExtractionList1.size() + " size "+SpectralCentroidExtractionList1.size());
				//Calculate and keep a % (defined by the overlap value)of the data  
				List<Double> temp1 = windowValues1.subList((100 - _overlap) * _window_size / 100, _window_size);
				ListIterator<Double> it1 = temp1.listIterator();

				windowValues1 = new CopyOnWriteArrayList<Double>();
				while (it1.hasNext()) {
					windowValues1.add(it1.next());
				}
				
				List<Double> temp2 = windowValues2.subList((100 - _overlap) * _window_size / 100, _window_size);
				ListIterator<Double> it2 = temp2.listIterator();

				windowValues2 = new CopyOnWriteArrayList<Double>();
				while (it2.hasNext()) {
					windowValues2.add(it2.next());
				}

				List<Double> temp3 = windowValues3.subList((100 - _overlap) * _window_size / 100, _window_size);
				ListIterator<Double> it3 = temp3.listIterator();

				windowValues3 = new CopyOnWriteArrayList<Double>();
				while (it3.hasNext()) {
					windowValues3.add(it3.next());
				}
				
				List<Double> temp4 = windowValues4.subList((100 - _overlap) * _window_size / 100, _window_size);
				ListIterator<Double> it4 = temp4.listIterator();

				windowValues4 = new CopyOnWriteArrayList<Double>();
				while (it4.hasNext()) {
					windowValues4.add(it4.next());
				}
				
				List<Double> temp5 = windowValues5.subList((100 - _overlap) * _window_size / 100, _window_size);
				ListIterator<Double> it5 = temp5.listIterator();

				windowValues5 = new CopyOnWriteArrayList<Double>();
				while (it5.hasNext()) {
					windowValues5.add(it5.next());
				}
				
				List<Double> temp6 = windowValues6.subList((100 - _overlap) * _window_size / 100, _window_size);
				ListIterator<Double> it6 = temp6.listIterator();

				windowValues6 = new CopyOnWriteArrayList<Double>();
				while (it6.hasNext()) {
					windowValues6.add(it6.next());
				}
				
				List<Double> temp7 = windowValues7.subList((100 - _overlap) * _window_size / 100, _window_size);
				ListIterator<Double> it7 = temp7.listIterator();

				windowValues7 = new CopyOnWriteArrayList<Double>();
				while (it7.hasNext()) {
					windowValues7.add(it7.next());
				}
				
				List<Double> temp8 = windowValues8.subList((100 - _overlap) * _window_size / 100, _window_size);
				ListIterator<Double> it8 = temp8.listIterator();

				windowValues8 = new CopyOnWriteArrayList<Double>();
				while (it8.hasNext()) {
					windowValues8.add(it8.next());
				}
				
				//If the data in the window belongs to different labels the window will be labelled with the most occurred label 
				ListIterator<Integer> itG = gestureValues.listIterator();
				int[] occurencies = new int[gestureNum.size()];
				while(itG.hasNext())
				{
					occurencies[itG.next()]++;
				}
				
				int maxIndex = 0;
				
			    for (int index = 0; index < occurencies.length; index ++) {
			    	if (occurencies[index] > occurencies[maxIndex]) {
			            maxIndex = index;
			        }
			    }
			    GestureList.add(maxIndex);
			    
				List<Integer> tempG1 = gestureValues.subList((100-_overlap)*_window_size/100, _window_size);
				ListIterator<Integer> itG1 = tempG1.listIterator();
				gestureValues = new CopyOnWriteArrayList<Integer>();
				while(itG1.hasNext())
				{
					gestureValues.add(itG1.next());
				}
				//FREE MEMORY
				temp1 = null;
				temp2 = null;
				temp3 = null;
				temp4 = null;
				temp5 = null;
				temp6 = null;
				temp7 = null;
				temp8 = null;
				occurencies = null;
				System.gc();
				
				
			}
			//System.out.println("Window Values 1 size: "+windowValues1.size());
			
		}
		
		/**
		 * Merge the features into instances. If the instances are unbalanced 
		 * (more data from one label than another) it also balances it.
		 * @return Instances - set of labelled instances to train the classifier
		 */
		private Instances MergeDataIntoInstances()
		{
		   //System.out.println(AverageAmplitudeExtractionList1.size());
		   int counterGestures = 0;

		   ArrayList<Integer> orderedList = new ArrayList<Integer>();


		   //Balance data
		   CollectionsUtil cu =  new CollectionsUtil();
		   for(int o = 0; o <RMSValues.get(0).size();o++)
		      cu.addInOrder(orderedList, GestureList.get(o));
		   
		   counterGestures = orderedList.get(orderedList.size()-1)+1;

		   int[] mode = new int[counterGestures];
		   	
		   mode = mode(counterGestures, GestureList);
		   //System.out.println(mode[0] +" "+ mode[1] +" "+ mode[2] +" "+mode[3] +" "+mode[4]+" "+mode[5]+" "+mode[6]+" "+mode[7]);

		   int minIndex = 0;
		   for (int index = 0; index < mode.length; index ++) {
		      if (mode[index] < mode[minIndex]) {
		         minIndex = index;
		      }
		   }

		   int[] diff = new int[counterGestures];

		   for(int a = 0; a < counterGestures;a++)
		   {
		      diff[a] = mode[a] - mode[minIndex];
		   }


		   int index = 0;
		   int indexOf = 0;
		   int indexOfEnd = 0;

	
		   CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> newlist1= new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
		   CopyOnWriteArrayList<CopyOnWriteArrayList<Double>> newlist2 = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
		   CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>> newlist3 = new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
		   
		   for(int i = 0; i < 8; i++)
			{
			   newlist1.add(new CopyOnWriteArrayList<Double>());
			   newlist3.add(new CopyOnWriteArrayList<Integer>());
			   newlist2.add(new CopyOnWriteArrayList<Double>());
			}
		   CopyOnWriteArrayList<Integer> newlist4= new CopyOnWriteArrayList<Integer>();

		   while( index < diff.length)
		   {
		      try {

		         indexOf = GestureList.indexOf(index);
		         indexOfEnd = indexOf + mode[minIndex];
		         //System.out.println("size no equilibrio: "+RMSValues.get(0).size()+"indexOf "+indexOf+" indexEnd "+indexOfEnd+"index "+index+" diff "+diff[index]);
		         for(int i = 0;i < 8 ; i++){
			         newlist1.get(i).addAll(RMSValues.get(i).subList(indexOf, indexOfEnd));
			         newlist2.get(i).addAll( WLValues.get(i).subList(indexOf, indexOfEnd));
			         newlist3.get(i).addAll( WAMPValues.get(i).subList(indexOf, indexOfEnd));
			         
		         }
		         //System.out.println("new size "+newlist1.get(0).size());
		         newlist4.addAll(GestureList.subList(indexOf, indexOfEnd));
		         index++;
		         

		      }catch(Exception e){
		         System.out.println("ERRO!" + e.getMessage());
		         //e.printStackTrace();
		      }


		   }
		   FreeFeaturesLists();
		   //InitiateFeaturesLists();
		   RMSValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
		   WAMPValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
		   WLValues = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
		  // System.out.println("tamanho do newlist1 "+newlist1.size()+" tamanho do newlist1 de 0 "+newlist1.get(0).size());
		   RMSValues.addAll(newlist1); newlist1 = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
		   WLValues.addAll(newlist2); newlist2 = new CopyOnWriteArrayList<CopyOnWriteArrayList<Double>>();
		   WAMPValues.addAll(newlist3); newlist3 = new CopyOnWriteArrayList<CopyOnWriteArrayList<Integer>>();
		   
		   GestureList = new CopyOnWriteArrayList<Integer>(); GestureList.addAll(newlist4);newlist4 = new CopyOnWriteArrayList<Integer>();
		   
		   
		   //System.out.println("tamanho do RMSValues "+RMSValues.size());
		   mode = mode(counterGestures, GestureList);
		   for(int m = 0; m < mode.length; m++)
			   System.out.println("mode: "+mode[m]);
	
				int size = 0;
				if(RMSValues.get(0).size() > 0)
					size = RMSValues.get(0).size();
				else if(WLValues.get(0).size() >0)
					size = WLValues.get(0).size();
				else
					size = WAMPValues.get(0).size();
				
				//System.out.println("tamanho do size"+size);
				
				for(int i=0; i < size; i++){
					vals = new double[data.numAttributes()];
					int indexVals = 0;
					for(int p = 0; p < 8; p++)
					{
						vals[indexVals] = RMSValues.get(p).get(i);
						indexVals++;
					}
					for(int p = 0; p < 8; p++)
					{
						vals[indexVals] = WLValues.get(p).get(i);
						indexVals++;
					}
					for(int p = 0; p < 8; p++)
					{
						vals[indexVals] = WAMPValues.get(p).get(i);
						indexVals++;
					}
					if(GestureList.get(i) == 0)
					vals[vals.length-1] = gestureNum.indexOf("NoGesture");
					else if(GestureList.get(i) == 1)
					vals[vals.length-1] = gestureNum.indexOf("Fist");
					else if(GestureList.get(i) == 2)
					vals[vals.length-1] = gestureNum.indexOf("Wave_Out");
					else if(GestureList.get(i) == 3)
						vals[vals.length-1] = gestureNum.indexOf("Wave_In");
						
					
					/*if(GestureList.get(i) == 0)
						vals[vals.length-1] = gestureNum.indexOf("NoGesture");
					else if(GestureList.get(i) == 1)
						vals[vals.length-1] = gestureNum.indexOf("Double_Tap");
					else if(GestureList.get(i) == 2)
						vals[vals.length-1] = gestureNum.indexOf("Fist");
					else if(GestureList.get(i) == 3)
						vals[vals.length-1] = gestureNum.indexOf("Spread_Fingers");
					else if(GestureList.get(i) == 4)
						vals[vals.length-1] = gestureNum.indexOf("Wave_Out");
					else if(GestureList.get(i) == 5)
						vals[vals.length-1] = gestureNum.indexOf("Wave_In");
					else if(GestureList.get(i) == 6)
						vals[vals.length-1] = gestureNum.indexOf("Gesture_1");
					else if(GestureList.get(i) == 7)
						vals[vals.length-1] = gestureNum.indexOf("Gesture_2");*/
					
					//add instance to Instances
					Instance ins = new DenseInstance(1.0, vals);
					/*try {
						saveTrainedInstances.logData(ins.toString());
						saveTrainedInstances.closeFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					data.add(ins);
					//System.out.println("foi adicionada instancia, tamanho do DATA "+data.size());
				}
			
				GestureList.clear();
				
				RMSValues.clear();
				WAMPValues.clear();
				WLValues.clear();
				
				FreeFeaturesLists();
				InitiateFeaturesLists();
				
			return data;
		}
		
		/**
		 * Auxiliary function to calculate the mode 
		 * @param size
		 * @param list
		 * @return
		 */
		private static int[] mode(int size, CopyOnWriteArrayList<Integer> list)
		{
			int[] array = new int[size];
			for(int i = 0; i < list.size(); i++)
			{
				array[list.get(i)]++;
			}
			return array;
		}
		
		/**
		 * Set features into an unlabelled instance to be classified
		 * @param eX - energy value
		 * @param eY - energy value
		 * @param eZ - energy value
		 * @param leX - low energy value
		 * @param leY - low energy value
		 * @param leZ - low energy value
		 * @param meanX - absolute mean value
		 * @param meanY - absolute mean value
		 * @param meanZ - absolute mean value
		 * @return
		 */
		private DenseInstance FeaturesDatatoInstance(double value1, double value2, double value3, double value4, double value5, double value6, double value7, double value8 , double value9, double value10, double value11, double value12, double value13, double value14, double value15, double value16, double value17, double value18, double value19, double value20, double value21, double value22, double value23, double value24)
		{
			double[] vals = new double[25];
			vals[0] = value1;
			vals[1] = value2;
			vals[2] = value3;
			vals[3] = value4;
			vals[4] = value5;
			vals[5] = value6;
			vals[6] = value7;
			vals[7] = value8;
			vals[8] = value9;
			vals[9] = value10;
			vals[10] = value11;
			vals[11] = value12;
			vals[12] = value13;
			vals[13] = value14;
			vals[14] = value15;
			vals[15] = value16;
			vals[16] = value17;
			vals[17] = value18;
			vals[18] = value19;
			vals[19] = value20;
			vals[20] = value21;
			vals[21] = value22;
			vals[22] = value23;
			vals[23] = value24;
		    vals[24] =  Utils.missingValue();
			return new DenseInstance(1.0, vals);
		}
		
		
		public String getRecogGesture() {
			return recogGesture;
		}

		public void setRecogGesture(String recogGesture) {
			this.recogGesture = recogGesture;
		}
		
		public void createSocketServerMobile()
		{
			new Thread(new Runnable(){
				public void run(){ 
			
			// TODO Auto-generated method stub
			//createSocketServer();
			while(true){
					serverSocket = null;
			
			       try {
			           serverSocket = new ServerSocket(portMobile);
			       } catch (IOException e) {
			           System.err.println("Could not listen on port: " + portMobile);
			           //System.exit(1);
			           break;
			       }
	
			       Socket clientSocket = null;
			       try {
			       	System.err.println("Waiting for Mobile connections to the STB.");
			           clientSocket = serverSocket.accept();
			       } catch (IOException e) {
			           System.err.println("Accept failed.");
			           //System.exit(1);
			       }
			       System.err.println("Connection Accepted.");
			       //out = new PrintWriter(clientSocket.getOutputStream(),true);
			       try {
			    	   out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
			    	  
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}}).start();
		} 
		
		private void sendGestureToMobileClient(String gesture)
		{
			if(out != null)
			{
				out.println(gesture);
			}
		}


		public class CollectionsUtil {

			  public <T extends Comparable<T>> int addInOrder(final List<T> list, final T item) {
			    final int insertAt;
			    // The index of the search key, if it is contained in the list; otherwise, (-(insertion point) - 1).
			    final int index = Collections.binarySearch(list, item);
			    if (index < 0) {
			      insertAt = -(index + 1);
			    } else {
			      insertAt = index + 1;
			    }

			    list.add(insertAt, item);
			    return insertAt;
			  }

			  private CollectionsUtil() {
			  }
			}


		
	}
