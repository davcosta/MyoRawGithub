import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public  class EMGFeatureExtraction {

	
	 private double rms;
	    private double wl;
	    private int wamp;
	    public EMGFeatureExtraction()
	    {
	    }

	    public void CalculateFeatures(double[] values, double threshold)
	    {
	        rms = 0; // result
	        double x = 0; // number coming in
	        double n = 0; // running count of numbers
	        double t = 0; // running total

	        wl = 0;

	        wamp = 0;
	        double val = 0;

	        for (int i =0; i < values.length-1; i++)
	        {
	            //RMS CALCULATION
	            x = values[i];
	            t = t + Math.pow(x,2); // running total
	            n = n + 1;
	            if(i == values.length-2)
	            {
	                x = values[i+1];
	                t = t + Math.pow(x,2); // running total
	                n = n + 1;

	            }

	            //WL CALCULATION
	            wl += Math.abs(values[i+1] - values[i]);

	            //WAMP
	            val = Math.abs(values[i] - values[i + 1]);
	            if(val > threshold)
	                wamp += 1;
	            else
	                wamp += 0;
	        }
	        //RMS CALCULATION
	        rms = Math.sqrt(t / n);
	    }

	    public double getRMS()
	    {
	        return rms;
	    }

	    public double getWL()
	    {
	        return wl;
	    }

	    public int getWAMP()
	    {
	        return wamp;
	    }
	
}
