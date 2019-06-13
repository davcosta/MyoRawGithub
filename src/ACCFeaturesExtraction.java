

import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Created by Portatil on 25-11-2015.
 */
public class ACCFeaturesExtraction {

    private double EnergyX, EnergyY, EnergyZ;
    private double LowEnergyX, LowEnergyY, LowEnergyZ;
    private double AbsMeanX, AbsMeanY,AbsMeanZ;
    
    public ACCFeaturesExtraction()
    {

    }
    
    public void CalculateFeatures(double[] x, double[] y, double[] z, int sampleFreq)
    {
        
        EnergyX = 0;
        EnergyY = 0;
        EnergyZ = 0;
        
        LowEnergyX = 0;
        LowEnergyY = 0;
        LowEnergyZ = 0;
        
   
        
        AbsMeanX = 0;
        AbsMeanY = 0;
        AbsMeanZ = 0;
        
     

        DescriptiveStatistics ds = new DescriptiveStatistics(x);
        
        double[] fftX = FFTbase.fft(x, new double[x.length] , true);
              
        LowEnergyX = CalculateLowEnergy(fftX, x, sampleFreq);
        EnergyX = CalculateEnergy(fftX);
        
        
        double[] absAccX = ToAbsolute(x);
        ds = new DescriptiveStatistics(absAccX);
        AbsMeanX = ds.getMean();
        
        
        ds = new DescriptiveStatistics(y);
        double[] fftY = FFTbase.fft(y, new double[y.length] , true);
        LowEnergyY = CalculateLowEnergy(fftY, y, sampleFreq);
        EnergyY = CalculateEnergy(fftY);
        
        
        double[] absAccY = ToAbsolute(y);
        ds = new DescriptiveStatistics(absAccY);
        AbsMeanY = ds.getMean();
        
        

        ds = new DescriptiveStatistics(z);
        double[] fftZ = FFTbase.fft(z, new double[z.length] , true);
        LowEnergyZ = CalculateLowEnergy(fftZ, z, sampleFreq);
        EnergyZ = CalculateEnergy(fftZ);
        
        
        double[] absAccZ = ToAbsolute(z);
        ds = new DescriptiveStatistics(absAccZ);
        AbsMeanZ = ds.getMean();
        
    }
    
    private double[] CalculateFFTFreq(double[] x1, double freq)
    {
    	double[] result = new double[x1.length];
    	for(int i = 0; i < x1.length-1; i++)
    	{
    		//System.out.println("x1 size: "+x1.length+"freq: "+freq);
    		result[i] = i/(x1.length/freq);
    		
    	}		
    	return result;
    }
    private double CalculateLowEnergy(double[] fft, double[] x, double sampleFreq)
    {

    	double lowEnergy = 0;
    	double[] freq = CalculateFFTFreq(x, sampleFreq);
    	int index = 0;
    	while(freq[index] <= 0.7)
    	{
    		lowEnergy += Math.pow(Math.abs(fft[index]),2);
    		index++;
    	}
    	return lowEnergy;
    }
    
    private double[] ToAbsolute(double[] a)
    {
    	double[] abs = new double[a.length];
    	
    	for(int i = 0; i < a.length; i++)
    		abs[i] = Math.abs(a[i]);
    	
    	return abs;
    }
    
    private double CalculateEnergy(double[] fft)
    {
    	double totalEnergy = 0;
    	for(int i = 2; i < fft.length/2; i+=2)
    		totalEnergy += Math.pow(Math.abs(fft[i]),2);
    	return totalEnergy;
    }
    
    public double GetEnergyX()
    {
        return EnergyX;
    }
    public double GetLowEnergyX()
    {
        return LowEnergyX;
    }
    public double GetLowEnergyY()
    {
        return LowEnergyY;
    }
    public double GetLowEnergyZ()
    {
        return LowEnergyZ;
    }
    public double GetEnergyY()
    {
        return EnergyY;
    }
    public double GetEnergyZ()
    {
        return EnergyZ;
    }
    public double GetAbsMeanX()
    {
    	return AbsMeanX;
    }
    public double GetAbsMeanY()
    {
    	return AbsMeanY;
    }
    public double GetAbsMeanZ()
    {
    	return AbsMeanZ;
    }
}
