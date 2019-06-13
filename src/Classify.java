

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.*;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.bayes.BayesNet;
import weka.core.Utils;

public class Classify {
	public static final String NAIVEBAYES = "NaiveBayes";
	public static final String BAYESNET = "BayesNet";
	public static final String J48 = "J48";
	public static final String RANDOMTREE = "RandomTree";
	public static final String RANDOMFOREST = "RandomForest";
	public static final String SVM = "SVM";
	public static final String SMO = "SMO";
	private Classifier cModel;
	private Evaluation eTest;
	private int folds;
	private Classifier[] listClassi = { (Classifier) new NaiveBayes(), (Classifier) new BayesNet(),  (Classifier) new J48(), (Classifier) new RandomTree(), (Classifier) new RandomForest(), (Classifier) new weka.classifiers.functions.SMO(), (Classifier) new LibSVM()};
	private Random rand;
	private String _classifierName = RANDOMFOREST;
	String strSummary = "";
	String accuracy = "";

	
	String _options = ( /*"-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1"*/ "-S 0 -K 1 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1" );
	//String[] optionsArray = _options.split( " " );
	
	public Classify(int seed, int nFolds)
	{
		rand = new Random(seed);
		folds = nFolds;
		
	}
	/**
	 * Train the classifier with given data 
	 * @param data
	 * @param classifierName
	 */
	public void TrainClassifier(Instances data, String classifierName, String options)
	{
		_classifierName = classifierName;
		_options = options;
		
		if(_classifierName == NAIVEBAYES)
		{
			TrainEval(data, listClassi[0]);
		}else if(_classifierName == BAYESNET){
			TrainEval(data, listClassi[1]);
		}else if(_classifierName == J48){
			TrainEval(data, listClassi[2]);
		}else if(_classifierName == RANDOMTREE){
			TrainEval(data, listClassi[3]);
		}else if(_classifierName == SMO){
			TrainEval(data, listClassi[5]);
		}
		else if(_classifierName == RANDOMFOREST)
		{
			TrainEval(data, listClassi[4]);
		}else if(_classifierName == SVM)
		{
			
			TrainEval(data, listClassi[6]);
			
			   
		}	
	}
	
	/**
	 * Auxiliary function to train the classifier
	 * @param data
	 * @param classi
	 */
	private void TrainEval(Instances data, Classifier classi)
	{
		try {
				//OPTIONAL - You can discretize the data to balance the classification. 
				//What it does is to transform numerical data into nominal with a defined number of intervals
			    //See WEKA filter (Discretize)
				
				if(_classifierName == NAIVEBAYES)
					((weka.classifiers.bayes.NaiveBayes) classi).setOptions(Utils.splitOptions(_options));
				else if(_classifierName == BAYESNET)
					((weka.classifiers.bayes.BayesNet) classi).setOptions(Utils.splitOptions(_options));
				else if(_classifierName == J48)
					((weka.classifiers.trees.J48) classi).setOptions(Utils.splitOptions(_options));
				else if(_classifierName == RANDOMTREE)
					((weka.classifiers.trees.RandomTree) classi).setOptions(Utils.splitOptions(_options));
				else if(_classifierName == RANDOMFOREST)
					((weka.classifiers.trees.RandomForest) classi).setOptions(Utils.splitOptions(_options));
				else if(_classifierName == SMO)
					((weka.classifiers.functions.SMO) classi).setOptions(Utils.splitOptions(_options));
				else if(_classifierName == SVM)
					((weka.classifiers.functions.LibSVM) classi).setOptions(Utils.splitOptions(_options));
				
				data.setClassIndex(data.numAttributes()-1);//set the index of the attribute that classifies (labels) the  instance
				classi.buildClassifier(data); //train the classifier
				
				//Perform an evaluation with a cross validation model
				eTest = new Evaluation(data);
				eTest.crossValidateModel(classi, data, folds, rand);
				strSummary += eTest.toSummaryString();
				accuracy += eTest.correct()/(eTest.correct()+eTest.incorrect())*100+"%";
				eTest.toSummaryString();
				
				System.out.println(eTest.toSummaryString());//prints the result
				System.out.println(eTest.toMatrixString());//prints the result
				cModel = classi;
				cModel.buildClassifier(data);
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			strSummary = "error "+e;
			accuracy = "error "+e;
		}
	}
	
	public String GetAccuracyResult() {
		// TODO Auto-generated method stub
		return accuracy;
	}
	/**
	 * Classify unlabelled instances
	 * @param unlabeled - instances with the instance
	 * @return - index of the defined set of labels in the data structure
	 */
	public int ClassifyInstance(Instances unlabelled)
	{
		double clsLabel = -1;
		
		//System.out.println("unallbeled "+ unlabelled.get(0).toString());
		try {
			unlabelled.setClassIndex(unlabelled.numAttributes()-1);
			clsLabel = cModel.classifyInstance(unlabelled.get(0));
			
			System.out.println("result "+clsLabel+ " "+unlabelled.classAttribute().toString());
			//for(int i = 0; i < cModel.distributionForInstance(unlabelled.get(0)).length;i++)
				//System.out.println(cModel.distributionForInstance(unlabelled.get(0))[i]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return (int)clsLabel;
	}
	
	/**
	 * print the summary result
	 * @return
	 */
	public String GetSummary()
	{
		return eTest.toSummaryString();
	}
	
	/**
	 * Load the trained classifier from file
	 */
	public void LoadTrainedClassifier( String classifierName)
	{
		ObjectInputStream ois;
		
			try {
				ois = new ObjectInputStream(new FileInputStream("DATA/classifier"+classifierName+".model"));
				cModel = (Classifier) ois.readObject();
				ois.close();
				System.out.println("Classifier successfuly loaded..."+cModel.toString()+" end");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}
	/**
	 * Save the trained classifier to a file
	 */
	public void SaveTrainedClassifier()
	{
		ObjectOutputStream oos; 
		int index = 0;
		try {
			oos = new ObjectOutputStream( new FileOutputStream("DATA/classifier"+_classifierName+".model"));
			oos.writeObject(cModel);
			oos.flush();
			oos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
