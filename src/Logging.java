

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import weka.core.Attribute;
import weka.core.Debug.Log;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NumericToNominal;

//import com.example.biosens.Comparable;
//import com.example.biosens.Exception;
//import com.example.biosens.Integer;
//import com.example.biosens.Logging.CollectionsUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;



public class Logging {
	boolean mFirstWrite=true;
	String[] mSensorNames;
	String[] mSensorFormats;
	String[] mSensorUnits;
	String mFileName="";
	BufferedWriter writer=null;
	File outputFile;
	String mDelimiter=","; //default is comma
	
	
	//WEKA STUFF
	ArrayList<Attribute>      atts;
    ArrayList<Attribute>      attsRel;
    ArrayList<Attribute>      attVals;
    ArrayList<Attribute>      attValsRel;
    Instances       data;
    Instances       dataRel;
    double[]        vals;
    double[]        valsRel;
    String gesName;
    ArrayList<Attribute> gestureList;
    Instances filteredData;
    ArrayList<String> gestureNum;
   
	/**
	 * @param myName is the file name which will be used
	 */
	public Logging(String myName){
		mFileName=myName;
		
		File root = new File("DATA");
		System.out.println( root.getAbsolutePath());
		if(!root.exists())
	    	{
	        	if(root.mkdir()); //directory is created;
	    	}
		outputFile = new File(root, mFileName+".csv");
		
		
	}
	/**
	 * 
	 * @param myName is the file name
	 * @param fileType is the file type
	 * @param useless is useless
	 */
	public Logging(String myName, String fileType, String gesture){
		mFileName=myName;
		//File root = Environment.getExternalStorageDirectory();
		File root = new File("EMGDATA"); //C:/Users/David Costa/workspace/BioSensWin/EMGDATA
		if(!root.exists())
	    {
	        if(root.mkdir()); //directory is created;
	    }
	   outputFile = new File(root, mFileName+fileType);
		//Log.d("AbsolutePath", root.getAbsolutePath());
		//outputFile = new File(root, mFileName+fileType);
		gesName = gesture;
		
		//WEKA STUFF
		atts = new ArrayList<Attribute>();
		//name of the gesture
	
		
		//atts.add(new Attribute("Gesture", gestureList));
		//atts.add(new Attribute("Gesture", (ArrayList<Attribute>) null));
		
		//ENERGY
		atts.add(new Attribute("AC_ENERGY_X"));
		atts.add(new Attribute("AC_ENERGY_Y"));
		atts.add(new Attribute("AC_ENERGY_Z"));
		//LOW ENERGY
		atts.add(new Attribute("AC_LOW_ENERGY_X"));
		atts.add(new Attribute("AC_LOW_ENERGY_Y"));
		atts.add(new Attribute("AC_LOW_ENERGY_Z"));
		//ABSOLUTE MEAN
		atts.add(new Attribute("AC_ABSOLUTE_MEAN_X"));
		atts.add(new Attribute("AC_ABSOLUTE_MEAN_Y"));
		atts.add(new Attribute("AC_ABSOLUTE_MEAN_Z"));
		
		gestureNum = new ArrayList<String>();
		
		gestureNum.add("NoGesture");
		gestureNum.add("SwipeRight");
		gestureNum.add("SwipeLeft");
		
		atts.add(new Attribute("Gesture", gestureNum));
		
		data = new Instances("Gesture_Windows", atts, 0);
	}
	
	/**
	 * 
	 * @param myName
	 * @param delimiter
	 */
	public Logging(String myName,String delimiter){
		mFileName=myName;
		mDelimiter=delimiter;
		File root = new File("unit03");
		System.out.println(root.getAbsolutePath());
		outputFile = new File(root, mFileName+".dat");
	}
	
	/**
	 * @param myName
	 * @param delimiter
	 * @param folderName will create a new folder if it does not exist
	 */
	/*public Logging(String myName,String delimiter, String folderName){
		mFileName=myName;
		mDelimiter=delimiter;

		 File root = new File(folderName); //C:/Users/David Costa/workspace/BioSensWin/

		   if(!root.exists())
		    {
		        if(root.mkdir()); //directory is created;
		    }
		   outputFile = new File(root, mFileName+".dat");
	}*/
	
	public Instances logData(CopyOnWriteArrayList<Double> list, CopyOnWriteArrayList<Double> list1, CopyOnWriteArrayList<Double> list2, CopyOnWriteArrayList<Double> list3, CopyOnWriteArrayList<Double> list4, CopyOnWriteArrayList<Double> list5, CopyOnWriteArrayList<Double> list6, CopyOnWriteArrayList<Double> list7, CopyOnWriteArrayList<Double> list8, CopyOnWriteArrayList<Integer> list9)
	{
	   //System.out.println("list "+list23.toString());
	   int lastValue = -1;
	   int counterGestures = 0;

	   ArrayList<Integer> orderedList = new ArrayList<Integer>();


	   //Ordena a lista de gestos
	   CollectionsUtil cu =  new CollectionsUtil();
	   for(int o = 0; o <list3.size();o++)
	      cu.addInOrder(orderedList, list9.get(o));

	   //System.out.println(orderedList.toString());
	   //System.out.println("Lista: "+list.toString());


	   //vais buscar o valor maximo da lista de gestos
	   counterGestures = orderedList.get(orderedList.size()-1)+1;

	   //calcula o numero de instancias para cada um dos gestos (moda)
	   int[] mode = new int[counterGestures];

	   mode = mode(counterGestures, list9);


	   int minIndex = 0;
	   for (int index = 0; index < mode.length; index ++) {
	      if (mode[index] < mode[minIndex]) {
	         minIndex = index;
	      }
	      //System.out.println("mode "+index+" = "+mode[index]);
	   }

	   int[] diff = new int[counterGestures];

	   for(int a = 0; a < counterGestures;a++)
	   {
	      diff[a] = mode[a] - mode[minIndex];
	      //System.out.println("diff "+a+" = "+diff[a]);
	   }


	   //System.out.println(" "+x+" "+y+" "+w+" "+z+" "+s+" "+t+" "+u);
	   //(u > 0 ||t > 0 ||s > 0 ||x > 0 || y > 0 || w > 0 || z > 0) && index < list3.size()
	   //System.out.println("Mode antes: "+Arrays.toString(mode)+" , Diff: "+Arrays.toString(diff)+" , are all true? "+ areAllTrue(diff));
	   int index = 0;
	   int indexOf = 0;
	   int indexOfEnd = 0;

	   CopyOnWriteArrayList<Double> newlist = new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Double> newlist1 = new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Double> newlist2= new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Double> newlist3 = new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Double> newlist4 = new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Double> newlist5 = new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Double> newlist6= new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Double> newlist7 = new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Double> newlist8 = new CopyOnWriteArrayList<Double>();
	   CopyOnWriteArrayList<Integer> newlist9 = new CopyOnWriteArrayList<Integer>();
	   



	   while( index < diff.length)
	   {


	      try {


	         indexOf = list9.indexOf(index);
	         indexOfEnd = indexOf + mode[minIndex];

	         newlist.addAll(list.subList(indexOf, indexOfEnd));
	         newlist1.addAll(list1.subList(indexOf, indexOfEnd));
	         newlist2.addAll(list2.subList(indexOf, indexOfEnd));
	         newlist3.addAll(list3.subList(indexOf, indexOfEnd));
	         newlist4.addAll(list4.subList(indexOf, indexOfEnd));
	         newlist5.addAll(list5.subList(indexOf, indexOfEnd));
	         newlist6.addAll(list6.subList(indexOf, indexOfEnd));
	         newlist7.addAll(list7.subList(indexOf, indexOfEnd));
	         newlist8.addAll(list8.subList(indexOf, indexOfEnd));
	         newlist9.addAll(list9.subList(indexOf, indexOfEnd));
	         
	         //System.out.println("Index: "+index+" indexOf "+indexOf+" indexOfEnd "+indexOfEnd);

	         index++;

	      }catch(Exception e){
	         //Log.d("ERROR","EXCEPTION!: "+e.getMessage());
	         System.out.println("ERRO!" + e.getMessage());
	      }


	   }
	   list = new CopyOnWriteArrayList<Double>(); list.addAll(newlist);newlist = new CopyOnWriteArrayList<Double>();
	   list1 = new CopyOnWriteArrayList<Double>(); list1.addAll(newlist1);newlist1 = new CopyOnWriteArrayList<Double>();
	   list2 = new CopyOnWriteArrayList<Double>(); list2.addAll(newlist2);newlist2 = new CopyOnWriteArrayList<Double>();
	   list3 = new CopyOnWriteArrayList<Double>(); list3.addAll(newlist3);newlist3 = new CopyOnWriteArrayList<Double>();
	   list4 = new CopyOnWriteArrayList<Double>(); list4.addAll(newlist4);newlist4 = new CopyOnWriteArrayList<Double>();
	   list5 = new CopyOnWriteArrayList<Double>(); list5.addAll(newlist5);newlist5 = new CopyOnWriteArrayList<Double>();
	   list6 = new CopyOnWriteArrayList<Double>(); list6.addAll(newlist6);newlist6 = new CopyOnWriteArrayList<Double>();
	   list7 = new CopyOnWriteArrayList<Double>(); list7.addAll(newlist7);newlist7 = new CopyOnWriteArrayList<Double>();
	   list8 = new CopyOnWriteArrayList<Double>(); list8.addAll(newlist8);newlist8 = new CopyOnWriteArrayList<Double>();
	   list9 = new CopyOnWriteArrayList<Integer>(); list9.addAll(newlist9);newlist9 = new CopyOnWriteArrayList<Integer>();

	   mode = mode(counterGestures, list9);
	   
		try {
				
			//Save File Stuff
			writer = new BufferedWriter(new FileWriter(outputFile,false));
			gesName = "BinaryGesture";
			// create Instances object
			//Log.d("COUNTER", "count: "+x+" "+y+" "+w+" "+z );
			
			int size = 0;
			if(list.size() > 0)
				size = list.size();
			else if(list1.size() >0)
				size = list1.size();
			else
				size = list2.size();
			writer.newLine();
			//fill with data
			//System.out.println("-------------------------"+size);
			for(int i=0; i < size; i++){
				//Log.d("LOGGING", gesName);
				vals = new double[data.numAttributes()];
				
				if(list9.get(i) == 1)
					vals[10] = gestureNum.indexOf("SwipeRight");
				else if(list9.get(i) == 2)
					vals[10] = gestureNum.indexOf("SwipeLeft");
				else 
					vals[10] = gestureNum.indexOf("NoGesture");
				
				
				data.add(new DenseInstance(1.0, vals));
				
				
				
				
			}
			
			writer.write(data.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return data;
	}
	
	
	
	public void logData(String summary) throws IOException
	{
			writer = new BufferedWriter(new FileWriter(outputFile,true));
			writer.write(summary);
			writer.newLine();
			
		
	}
	
	public void logData(double[] d1, double[] d2, double[] d3, double[] d4, double[] d5, double[] d6, double[] d7, double[] d8)
	{
		String string ="";
		try {
			writer = new BufferedWriter(new FileWriter(outputFile,true));
			
			for(int i = 0;i < d1.length;i++)
			{
				string = d1[i] + "," +d2[i] + ","+ d3[i] + ","+d4[i] + ","+d5[i] + ","+d6[i] + ","+d7[i] + ","+d8[i];
				writer.write(string);
				writer.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public int closeFile(){
		int saved = -1;
		if (writer != null){
			try {
				writer.close();
				saved = 1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				saved = 0;
			}
		
		}
		return saved;
	}
	
	public String getName(){
		return mFileName;
	}
	
	public String getAbsoluteName(){
		return outputFile.getAbsolutePath();
	}
	
	
	private boolean compareStringArray(String[] stringArray, String string){
		boolean uniqueString=true;
		int size = stringArray.length;
		for (int i=0;i<size;i++){
			if (stringArray[i]==string){
				uniqueString=false;
			}	
					
		}
		return uniqueString;
	}
		
		private static int[] mode(int size, CopyOnWriteArrayList<Integer> list)
		{
			int[] array = new int[size];
			for(int i = 0; i < list.size(); i++)
			{
				array[list.get(i)]++;
			}
			return array;
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


