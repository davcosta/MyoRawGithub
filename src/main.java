
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.LookAndFeel;


import com.google.common.primitives.Doubles;

public class main extends JFrame implements KeyListener, ActionListener {
	
	

	
	private ServerSocket middleman;
    private int port = 8080;
    private Socket client;
    private static final int WINDOW_SIZE = 500;
    private static final int SAMPLE_RATE = 1000;
    private static final int CUT_FREQ = 6;
    private ArrayList<Double> emg1, emg2, emg3, emg4, emg5, emg6, emg7, emg8;
   
    private static final int RANDOMFOREST = 0;
    private static final int BAYESNET = 1;
    private static final int J48 = 2;
    private static final int NAIVESBAYES = 3;
    private static final int RANDOMTREE = 4;
    private static final int SMO = 5;
    private static final int SVM = 6;
    
		
   
    static final String newline = System.getProperty("line.separator");
    private int gestureID = 0;
    private Logging log = new Logging("datafromgemg_NOGESTURE");
    int counter = 0;
    private ArrayList<String[]> dataFromFile = new ArrayList<String[]>();
    
    DataClassification classGesLive = new DataClassification();
   
    
    JTextArea displayArea;
    JTextField typingArea;
    
    
  //SERVER STUFF
    public void createSocketServer()
    {
    	new Thread(new Runnable(){
			public void run(){ 
		try{
//			loadClassifier(Classify.RANDOMFOREST, DataClassification.FREQUENCY_1000HZ, 512, 18, 6,6);
			loadClassifier(Classify.RANDOMFOREST, DataClassification.FREQUENCY_1000HZ, 256, 33, 6,6);
		//create the socket server object
		middleman = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
            //System.out.println("Waiting for client request");
            //creating socket and waiting for client connection
            Socket socket = middleman.accept();
            //System.out.println("connected...");
            //read from socket to ObjectInputStream object
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //convert ObjectInputStream object to String
            while (!reader.ready());
            
            String message = (String) reader.readLine();
            //System.out.println("Message Received: " + message + "size "+reader.readLine().length());
            //create ObjectOutputStream object
            //System.out.println("message: "+message);
            
            String[] split = message.split(",");
            sendData(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]), Integer.parseInt(split[7]));
            //System.out.println("Veio do c++ "+ Integer.parseInt(split[0])+ " " + Integer.parseInt(split[1])+ " " + Integer.parseInt(split[2])+ " " +Integer.parseInt(split[3])+ " " +Integer.parseInt(split[4])+ " " +Integer.parseInt(split[5])+ " " +Integer.parseInt(split[6])+ " " +Integer.parseInt(split[7])+ " ");
            saveToFile(message);
            reader.close();
           
            socket.close();
            //terminate the server if client sends exit request
            if(message.equalsIgnoreCase("exit")) break;
        }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        middleman.close();
				
		}catch(Exception e)
		{
			//handle this shit
			e.printStackTrace();
		}
			}}).start();
    }
	
    private void saveToFile(String message)
    {
    	try{
    		log.logData(message+","+gestureID);
    		log.closeFile();
    	}catch(Exception e)
    	{
    		displayArea.append("error "+e);
            displayArea.setCaretPosition(displayArea.getDocument().getLength());
    	}
    	
    	
    }
    
    
    //KEY EVENTS STUFF
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
    	 displayInfo(e, "KEY TYPED: ");
    }

    /** Handle the key-pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
    	 displayInfo(e, "KEY PRESSED: ");
    }

    /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {
    	 displayInfo(e, "KEY RELEASED: ");
    }
	
    
    /*
     * We have to jump through some hoops to avoid
     * trying to print non-printing characters
     * such as Shift.  (Not only do they not print,
     * but if you put them in a String, the characters
     * afterward won't show up in the text area.)
     */
    private void displayInfo(KeyEvent e, String keyStatus){
         
        //You should only rely on the key char if the event
        //is a key typed event.
        int id = e.getID();
        String keyString;
        int keyCode = e.getKeyCode();
        keyString = "key code = " + keyCode
                + " ("
                + KeyEvent.getKeyText(keyCode)
                + ")";
        if(id == KeyEvent.KEY_PRESSED)
        {
        	

        	switch(e.getKeyCode())
        	{
        	case 49: gestureID = 1;break;//TODO
        	case 50: gestureID = 2;break;//TODO
        	case 51: gestureID = 3;break;//TODO
        	case 52: gestureID = 4;break;//TODO
        	case 53: gestureID = 5;break;//TODO
        	case 54: gestureID = 6;break;//TODO
        	case 55: gestureID = 7;break;//TODO
        	case 56: gestureID = 8;break;//TODO
        	case 57: createSocketServer();break;//TODO
        	case 58: gestureID = 10;break;//TODO
        	case 59: break;//TODO
        	default: gestureID = 0; counter =0;break;
        	}
        }else if(id == KeyEvent.KEY_RELEASED)
        {
        	gestureID = 0;
        	switch(e.getKeyCode())
        	{
        	case 49: counter++;System.out.println(counter);break;//TODO17072
        	case 50: counter++;System.out.println(counter);break;//TODO
        	case 51: counter++;System.out.println(counter);break;//TODO
        	case 52: counter++;System.out.println(counter);break;//TODO
        	case 53: counter++;System.out.println(counter);break;//TODO
        	case 54: counter++;System.out.println(counter);break;//TODO
        	case 55: counter++;System.out.println(counter);break;//TODO
        	case 56: counter++;System.out.println(counter);break;//TODO
        	case 58: gestureID = 10;break;//TODO
        	case 59: break;//TODO
        	default: gestureID = 0; counter =0;break;
        	}
        }
        
        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }
         
        String actionString = "action key? ";
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }
         
        String locationString = "key location: ";
        int location = e.getKeyLocation();
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown";
        }
         
        displayArea.append(keyStatus + newline
                + "    " + keyString + newline
                + "    " + modString + newline
                + "    " + actionString + newline
                + "    " + locationString + newline);
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }
    
/**
 * Create the GUI and show it.  For thread safety,
 * this method should be invoked from the
 * event-dispatching thread.
 */
private static void createAndShowGUI() {
    //Create and set up the window.
    main frame = new main("KeyEventDemo");
    
   
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //Set up the content pane.
    frame.addComponentsToPane();
     
     
    //Display the window.
    frame.pack();
    frame.setVisible(true);
    
 
}
 
private void addComponentsToPane() {
     
    JButton button = new JButton("Clear");
    button.addActionListener(this);
     
    typingArea = new JTextField(20);
    typingArea.addKeyListener(this);
     
    //Uncomment this if you wish to turn off focus
    //traversal.  The focus subsystem consumes
    //focus traversal keys, such as Tab and Shift Tab.
    //If you uncomment the following line of code, this
    //disables focus traversal and the Tab events will
    //become available to the key event listener.
    //typingArea.setFocusTraversalKeysEnabled(false);
     
    displayArea = new JTextArea();
    displayArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(displayArea);
    scrollPane.setPreferredSize(new Dimension(375, 125));
     
    getContentPane().add(typingArea, BorderLayout.PAGE_START);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(button, BorderLayout.PAGE_END);
}
/** Handle the button click. */
public void actionPerformed(ActionEvent e) {
    //Clear the text components.
    displayArea.setText("");
    typingArea.setText("");
     
    //Return the focus to the typing area.
    typingArea.requestFocusInWindow();
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

public void runConfigs()
{
	String[] optionsBayesNet = {"",
			"-D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5", 
			"-D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2",
			"-D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5", 
			"-D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5", 
			"-D -Q weka.classifiers.bayes.net.search.local.GeneticSearch -- -L 10 -A 100 -U 10 -R 1 -M -C -S BAEYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5", 
			"-D -Q weka.classifiers.bayes.net.search.local.HillClimber -- -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5", 
			"-D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 2 -G 5 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5", 
			"-D -Q weka.classifiers.bayes.net.search.local.RepeatedHillClimber -- -U 10 -A 1 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- -A 10.0 -U 10000 -D 0.999 -R 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.TabuSearch -- -L 5 -U 10 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.TAN -- -S BAEYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.GeneticSearch -- -L 10 -A 100 -U 10 -R 1 -M -C -S BAEYES -E weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2",
			"-D -Q weka.classifiers.bayes.net.search.local.GeneticSearch -- -L 10 -A 100 -U 10 -R 1 -M -C -S BAEYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.GeneticSearch -- -L 10 -A 100 -U 10 -R 1 -M -C -S BAEYES -E weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.HillClimber -- -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2",
			"-D -Q weka.classifiers.bayes.net.search.local.HillClimber -- -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.HillClimber -- -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 2 -G 5 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2",
			"-D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 2 -G 5 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.LAGDHillClimber -- -L 2 -G 5 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.RepeatedHillClimber -- -U 10 -A 1 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2",
			"-D -Q weka.classifiers.bayes.net.search.local.RepeatedHillClimber -- -U 10 -A 1 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.RepeatedHillClimber -- -U 10 -A 1 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- -A 10.0 -U 10000 -D 0.999 -R 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2",
			"-D -Q weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- -A 10.0 -U 10000 -D 0.999 -R 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- -A 10.0 -U 10000 -D 0.999 -R 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.TabuSearch -- -L 5 -U 10 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2",
			"-D -Q weka.classifiers.bayes.net.search.local.TabuSearch -- -L 5 -U 10 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.TabuSearch -- -L 5 -U 10 -P 1 -S BAEYES -E weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.TAN -- -S BAEYES -E weka.classifiers.bayes.net.estimate.MultiNomialBMAEstimator -- -k2",
			"-D -Q weka.classifiers.bayes.net.search.local.TAN -- -S BAEYES -E weka.classifiers.bayes.net.estimate.BMAEstimator -- -A 0.5",
			"-D -Q weka.classifiers.bayes.net.search.local.TAN -- -S BAEYES -E weka.classifiers.bayes.net.estimate.BayesNetEstimator -- -A 0.5",};
	String[] optionsTree = {"",
			"-K 0 -M 1.0 -V 0.001 -S 1",
			"-K 0 -M 2.0 -V 0.001 -S 1",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 1",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 1",
			"-K 0 -M 1.0 -V 0.001 -S 1 -N 1",
			"-K 0 -M 2.0 -V 0.001 -S 1 -N 1",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 1",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 1",
			"-K 0 -M 1.0 -V 0.001 -S 1 -N 2",
			"-K 0 -M 2.0 -V 0.001 -S 1 -N 2",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 2",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 2",
			"-K 0 -M 1.0 -V 0.001 -S 1 -N 3",
			"-K 0 -M 2.0 -V 0.001 -S 1 -N 3",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 3",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 3",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 2",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 2",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 1",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 1",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 2",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 2",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 3",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 3",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 1",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 1",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 2",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 2",
			"-K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 3",
			"-K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 3",
			};
	
	String[] optionsForest = {
			"",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -N 4",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -N 4",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 4",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 4",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -N 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -N 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -N 3",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -N 3",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 3",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 3",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 4",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 4",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 3",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 3",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 4",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 4",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 2",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 3",
			"-P 100 -I 100 -num-slots 1 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 3",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -N 4",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -N 4",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 4",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 4",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -N 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -N 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -N 3",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -N 3",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 3",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 3",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 4",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 4",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 3",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 3",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 4",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 4",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 2",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 3",
			"-P 100 -I 100 -num-slots 2 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 3",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -N 4",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -N 4",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 4",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 4",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -N 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -N 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -N 3",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -N 3",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 1 -N 3",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 1 -N 3",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 4",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 4",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 2 -N 3",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 2 -N 3",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 4",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 4",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 2",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 1.0 -V 0.001 -S 1 -depth 3 -N 3",
			"-P 100 -I 100 -num-slots 3 -K 0 -M 2.0 -V 0.001 -S 1 -depth 3 -N 3",
			};
	
	String[] optionsJ48 = {"",
			"-C 0.25 -M 2", 
			"-C 0.40 -M 2",
			"-C 0.05 -M 2",
			"-C 0.25 -M 10",
			"-C 0.40 -M 10",
			"-C 0.05 -M 10",
			"-C 0.25 -M 50",
			"-C 0.40 -M 50",
			"-C 0.05 -M 50",
			"-C 0.25 -M 100",
			"-C 0.40 -M 100",
			"-C 0.05 -M 100",
			"-U -C 0.25 -M 2", 
			"-U -C 0.40 -M 2",
			"-U -C 0.05 -M 2",
			"-U -C 0.25 -M 10",
			"-U -C 0.40 -M 10",
			"-U -C 0.05 -M 10",
			"-U -C 0.25 -M 50",
			"-U -C 0.40 -M 50",
			"-U -C 0.05 -M 50",
			"-U -C 0.25 -M 100",
			"-U -C 0.40 -M 100",
			"-U -C 0.05 -M 100",
			};
	String[] optionsNaiveBayes = {"","-K", "-D"};
	
	String[] optionsSMO = {"",
			"-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 0.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.NormalizedPolyKernel -E 2.0 -C 250007\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 0.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.NormalizedPolyKernel -E 2.0 -C 250007\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PrecomputedKernelMatrixKernel -M kernelMatrix.matrix\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 0.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PrecomputedKernelMatrixKernel -M kernelMatrix.matrix\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.Puk -O 1.0 -S 1.0 -C 250007\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 0.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.Puk -O 1.0 -S 1.0 -C 250007\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 0.01\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 0.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 0.01\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.StringKernel -P 0 -C 250007 -IC 200003 -L 0.5 -ssl 3 -ssl-max 9\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",
			"-C 0.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.StringKernel -P 0 -C 250007 -IC 200003 -L 0.5 -ssl 3 -ssl-max 9\" -calibrator \"weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4\"",};
	
	String[] optionsSVM = {"-S 0 -K 2 -D 3 -G 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 0.0 -N 0.5 -M 40.0 -C 2.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 0.0 -N 0.5 -M 40.0 -C 4.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 0.0 -N 0.5 -M 40.0 -C 8.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 0.0 -N 0.5 -M 40.0 -C 16.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 2.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 2.0 -N 0.5 -M 40.0 -C 2.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 2.0 -N 0.5 -M 40.0 -C 4.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 2.0 -N 0.5 -M 40.0 -C 8.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 2.0 -N 0.5 -M 40.0 -C 16.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 2.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 4.0 -N 0.5 -M 40.0 -C 2.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 4.0 -N 0.5 -M 40.0 -C 4.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 4.0 -N 0.5 -M 40.0 -C 8.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 4.0 -N 0.5 -M 40.0 -C 16.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 8.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 8.0 -N 0.5 -M 40.0 -C 2.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 8.0 -N 0.5 -M 40.0 -C 4.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 8.0 -N 0.5 -M 40.0 -C 8.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1",
			"-S 0 -K 2 -D 3 -G 8.0 -N 0.5 -M 40.0 -C 16.0 -E 0.001 -P 0.1 -model \"C:\\Program Files\\Weka-3-8\" -seed 1"};
	String[] classifiers = {Classify.RANDOMFOREST, Classify.BAYESNET, Classify.J48, Classify.NAIVEBAYES, Classify.RANDOMTREE, Classify.SMO, Classify.SVM};
	
	ReadFromFile("DATA/datafromsensor.csv");
	
	for(int a = 0; a <= classifiers.length-1; a++)
	{
		final int  CLASSIFIERS = a;
		new Thread(new Runnable(){
			public void run(){ 
				String configuration = "";
		if(classifiers[CLASSIFIERS] == Classify.RANDOMFOREST)
		{
			
			
			for(int op = 0; op <= optionsForest.length -1; op++)
			{
				
				for(int b = 512; b <= 2048; b*=2 )
				{
					for(int c = 0; c <= 50; c++)
					{
						for(int d = 0;d < 40; d++){
							DataClassification classGes = new DataClassification();
							classGes.SetDataFromList(dataFromFile); //load list
							 
							classGes.SetDataFilePath("DATA/datafromsensor.csv"); //set file path of raw data (now it does nothing)
							classGes.SetClassifier(classifiers[CLASSIFIERS], optionsForest[op]);	//set which classifier you want to use and options
							classGes.ConfigureClassifierTrainValues(1000, 10); //set the seed, folds and bins values needed for training the classifier
							classGes.ConfigureWindowValues(b, c, DataClassification.FREQUENCY_1000HZ, DataClassification.FREQUENCY_6HZ,d); //set window values for its size, overlap and frequency
							
							classGes.TrainClassifier(); //train the classifier
							//classGes.TestClassifier(); //test the train classifier with data from a file
							configuration = "Classifier: "+classifiers[CLASSIFIERS]+" options: "+optionsForest[op]+" Window size: "+b+" Overlap: "+c+" Wamp threshold value "+d;
							classGes.saveConfigurationToFile(configuration);
							classGes = null;
							System.gc();
						}
					}
				}
			}
			
		}else if(classifiers[CLASSIFIERS] == Classify.RANDOMTREE)
		{
			for(int op = 0; op <= optionsTree.length -1; op++)
			{
				
				for(int b = 512; b <= 2048; b*=2 )
				{
					for(int c = 0; c <= 50; c++)
					{
						for(int d = 0;d < 40; d++){
							DataClassification classGes = new DataClassification();
							classGes.SetDataFromList(dataFromFile); //load list
							classGes.SetClassifier(classifiers[CLASSIFIERS], optionsTree[op]);	//set which classifier you want to use and options
							classGes.ConfigureClassifierTrainValues(1000, 10); //set the seed, folds and bins values needed for training the classifier
							classGes.ConfigureWindowValues(b, c, DataClassification.FREQUENCY_1000HZ, DataClassification.FREQUENCY_6HZ,d); //set window values for its size, overlap and frequency
							classGes.SetDataFilePath("DATA/datafromsensor.csv"); //set file path of raw data
							classGes.TrainClassifier(); //train the classifier
							//classGes.TestClassifier(); //test the train classifier with data from a file
							configuration = "Classifier: "+classifiers[CLASSIFIERS]+" options: "+optionsTree[op]+" Window size: "+b+" Overlap: "+c+" Wamp threshold value "+d;
							classGes.saveConfigurationToFile(configuration);
							classGes = null;
							System.gc();
						}
					}
				}
			}
		}else if(classifiers[CLASSIFIERS] == Classify.NAIVEBAYES)
		{
			for(int op = 0; op <= optionsNaiveBayes.length -1; op++)
			{
				
				for(int b = 512; b <= 2048; b*=2 )
				{
					for(int c = 0; c <= 50; c++)
					{
						for(int d = 0;d < 40; d++){
							DataClassification classGes = new DataClassification();
							classGes.SetDataFromList(dataFromFile); //load list
							classGes.SetClassifier(classifiers[CLASSIFIERS], optionsNaiveBayes[op]);	//set which classifier you want to use and options
							classGes.ConfigureClassifierTrainValues(1000, 10); //set the seed, folds and bins values needed for training the classifier
							classGes.ConfigureWindowValues(b, c, DataClassification.FREQUENCY_1000HZ, DataClassification.FREQUENCY_6HZ,d); //set window values for its size, overlap and frequency
							classGes.SetDataFilePath("DATA/datafromsensor.csv"); //set file path of raw data
							classGes.TrainClassifier(); //train the classifier
							//classGes.TestClassifier(); //test the train classifier with data from a file
							configuration = "Classifier: "+classifiers[CLASSIFIERS]+" options: "+optionsNaiveBayes[op]+" Window size: "+b+" Overlap: "+c+" Wamp threshold value "+d;
							classGes.saveConfigurationToFile(configuration);
							classGes = null;
							System.gc();
						}
					}
				}
			}
		}else if(classifiers[CLASSIFIERS] == Classify.BAYESNET)
		{
			for(int op = 0; op <= optionsBayesNet.length -1; op++)
			{
				
				for(int b = 512; b <= 2048; b*=2 )
				{
					for(int c = 0; c <= 50; c++)
					{
						for(int d = 0;d < 40; d++){
							DataClassification classGes = new DataClassification();
							classGes.SetDataFromList(dataFromFile); //load list
							classGes.SetClassifier(classifiers[CLASSIFIERS], optionsBayesNet[op]);	//set which classifier you want to use and options
							classGes.ConfigureClassifierTrainValues(1000, 10); //set the seed, folds and bins values needed for training the classifier
							classGes.ConfigureWindowValues(b, c, DataClassification.FREQUENCY_1000HZ, DataClassification.FREQUENCY_6HZ,d); //set window values for its size, overlap and frequency
							classGes.SetDataFilePath("DATA/datafromsensor.csv"); //set file path of raw data
							classGes.TrainClassifier(); //train the classifier
							//classGes.TestClassifier(); //test the train classifier with data from a file
							configuration = "Classifier: "+classifiers[CLASSIFIERS]+" options: "+optionsBayesNet[op]+" Window size: "+b+" Overlap: "+c+" Wamp threshold value "+d;
							classGes.saveConfigurationToFile(configuration);
							classGes = null;
							System.gc();
						}
					}
				}
			}
		}else if(classifiers[CLASSIFIERS] == Classify.SMO)
		{
			for(int op = 0; op <= optionsSMO.length -1; op++)
			{
				
				for(int b = 512; b <= 2048; b*=2 )
				{
					for(int c = 0; c <= 50; c++)
					{
						for(int d = 0;d < 40; d++){
							DataClassification classGes = new DataClassification();
							classGes.SetDataFromList(dataFromFile); //load list
							classGes.SetClassifier(classifiers[CLASSIFIERS], optionsSMO[op]);	//set which classifier you want to use and options
							classGes.ConfigureClassifierTrainValues(1000, 10); //set the seed, folds and bins values needed for training the classifier
							classGes.ConfigureWindowValues(b, c, DataClassification.FREQUENCY_1000HZ, DataClassification.FREQUENCY_6HZ,d); //set window values for its size, overlap and frequency
							classGes.SetDataFilePath("DATA/datafromsensor.csv"); //set file path of raw data
							classGes.TrainClassifier(); //train the classifier
							//classGes.TestClassifier(); //test the train classifier with data from a file
							configuration = "Classifier: "+classifiers[CLASSIFIERS]+" options: "+optionsSMO[op]+" Window size: "+b+" Overlap: "+c+" Wamp threshold value "+d;
							classGes.saveConfigurationToFile(configuration);
							classGes = null;
							System.gc();
						}
					}
				}
			}
		}else if(classifiers[CLASSIFIERS] == Classify.SVM)
		{
			for(int op = 0; op <= optionsSVM.length -1; op++)
			{
				
				for(int b = 512; b <= 2048; b*=2 )
				{
					for(int c = 0; c <= 50; c++)
					{
						for(int d = 0;d < 40; d++){
							DataClassification classGes = new DataClassification();
							classGes.SetDataFromList(dataFromFile); //load list
							classGes.SetClassifier(classifiers[CLASSIFIERS], optionsSVM[op]);	//set which classifier you want to use and options
							classGes.ConfigureClassifierTrainValues(1000, 10); //set the seed, folds and bins values needed for training the classifier
							classGes.ConfigureWindowValues(b, c, DataClassification.FREQUENCY_1000HZ, DataClassification.FREQUENCY_6HZ,d); //set window values for its size, overlap and frequency
							classGes.SetDataFilePath("DATA/datafromsensor.csv"); //set file path of raw data
							classGes.TrainClassifier(); //train the classifier
							//classGes.TestClassifier(); //test the train classifier with data from a file
							configuration = "Classifier: "+classifiers[CLASSIFIERS]+" options: "+optionsSVM[op]+" Window size: "+b+" Overlap: "+c+" Wamp threshold value "+d;
							classGes.saveConfigurationToFile(configuration);
							classGes = null;
							System.gc();
						}
					}
				}
			}
		}else if(classifiers[CLASSIFIERS] == Classify.J48)
		{
			for(int op = 0; op <= optionsJ48.length -1; op++)
			{
				
				for(int b = 512; b <= 2048; b*=2 )
				{
					for(int c = 0; c <= 50; c++)
					{
						for(int d = 0;d < 40; d++){
							DataClassification classGes = new DataClassification();
							classGes.SetDataFromList(dataFromFile); //load list
							classGes.SetClassifier(classifiers[CLASSIFIERS], optionsJ48[op]);	//set which classifier you want to use and options
							classGes.ConfigureClassifierTrainValues(1000, 10); //set the seed, folds and bins values needed for training the classifier
							classGes.ConfigureWindowValues(b, c, DataClassification.FREQUENCY_1000HZ, DataClassification.FREQUENCY_6HZ,d); //set window values for its size, overlap and frequency
							classGes.SetDataFilePath("DATA/datafromsensor.csv"); //set file path of raw data
							classGes.TrainClassifier(); //train the classifier
							//classGes.TestClassifier(); //test the train classifier with data from a file
							configuration = "Classifier: "+classifiers[CLASSIFIERS]+" options: "+optionsJ48[op]+" Window size: "+b+" Overlap: "+c+" Wamp threshold value "+d;
							classGes.saveConfigurationToFile(configuration);
							classGes = null;
							System.gc();
						}
					}
				}
			}
		}
			}}).start();
		
		
	}
	
	 
}

public void runSpecificConfig(int classifier, String options, int seed, int bins, int window_size, int overlap, int frequency, int cutoff_frequency, int wamp_value)
{
	ReadFromFile("DATA/datafromsensor.csv");
	String[] classifiers = {Classify.RANDOMFOREST, Classify.BAYESNET, Classify.J48, Classify.NAIVEBAYES, Classify.RANDOMTREE, Classify.SMO, Classify.SVM};
	DataClassification classGes = new DataClassification();
	classGes.SetDataFromList(dataFromFile); //load list
	classGes.SetClassifier(classifiers[classifier], options);	//set which classifier you want to use and options
	classGes.ConfigureClassifierTrainValues(seed, bins); //set the seed, folds and bins values needed for training the classifier
	classGes.ConfigureWindowValues(window_size, overlap, frequency, cutoff_frequency,wamp_value); //set window values for its size, overlap and frequency
	classGes.SetDataFilePath("DATA/datafromsensor.csv"); //set file path of raw data
	classGes.TrainClassifier(); //train the classifier
	//classGes.SetDataFromList(dataFromFile); //load list
	//classGes.TestClassifier(Classify.RANDOMFOREST); //test the train classifier with data from a file
	String configuration = "Classifier: "+classifiers[classifier]+" options: "+options+" Window size: "+window_size+" Overlap: "+overlap+" Wamp threshold value "+wamp_value;
	//classGes.saveConfigurationToFile(configuration);
	classGes = null;
	System.gc();
}

public void loadClassifier(String classifierName, int frequency, int window_value, int overlap, int lowpass_frequency, int wamp_value )
{
	classGesLive.ConfigureWindowValues(window_value, overlap, frequency, lowpass_frequency, wamp_value);
	classGesLive.UseClassifier(classifierName);
}

public void sendData(int sensor1, int sensor2, int sensor3, int sensor4, int sensor5, int sensor6, int sensor7, int sensor8)
{
	classGesLive.streamValues(sensor1, sensor2, sensor3, sensor4, sensor5, sensor6, sensor7, sensor8);
}




public main(String name) {
    super(name);
}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		main test = new main("TEST");
		
		//STUFF FOR ACQUIRING DATA
		/*try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
		
       
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                
            }
        });*/
		
        test.createSocketServer();
        
        
		//test.runConfigs();
        
		//test.runSpecificConfig(RANDOMFOREST, "", 1000, 10, 256, 33, DataClassification.FREQUENCY_1000HZ , DataClassification.FREQUENCY_6HZ, 6);
		
		
		
	}
	
	
	
	
}



