package com.mycompany.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Project5_PlugIn implements PlugInFilter {
    
	final int BLACK = 0;
	final int WHITE = 255;
	
	// Values for the masks
	final double VALUE16 = 1.0 / 6.0;
	final double VALUE_MINUS16 = -1 * VALUE16;
	final double VALUE18 = 1.0 / 8.0;
	final double VALUE_MINUS18 = -1 * VALUE18;
	final double VALUE28 = 2.0 / 8.0;
	final double VALUE_MINUS28 = -1 * VALUE28;
	 
	final double [][] h1 = {
			{VALUE_MINUS16, VALUE_MINUS16, 0},
			{VALUE_MINUS16, 0, VALUE16},
			{0, VALUE16, VALUE16}
	};
	
	final double [][] h2 = {
			{0, VALUE16, VALUE16},
			{VALUE_MINUS16, 0, VALUE16},
			{VALUE_MINUS16, VALUE_MINUS16, 0}
	};
	
	final double [][] h3 = {
			{VALUE_MINUS28, VALUE_MINUS18, 0},
			{VALUE_MINUS18, 0, VALUE18},
			{0, VALUE18, VALUE28}
	};
	
	final double [][] h4 = {
			{0, VALUE18, VALUE28},
			{VALUE_MINUS18, 0, VALUE18},
			{VALUE_MINUS28, VALUE_MINUS18, 0}
	};
    
    @Override    
    public int setup(String args, ImagePlus im) {  
    	// this plugin accepts 8-bit greyscales
        return DOES_8G; 
    }

    @Override
    public void run(ImageProcessor ip) {
        
        int whichMethod = (int)IJ.getNumber("Which of the four Filters or combinations should be used? (Input: 1-6)", 1);
        
        switch(whichMethod) {
        // Filter h1
        case 1:
        	filter(ip, h1);
            break;
        //Filter h2
        case 2:
        	filter(ip, h2);
            break;
        //Filter h3    
        case 3:
        	filter(ip, h3);
        	break;
        //Filter h4	
        case 4:
        	filter(ip, h4);
        	break;
        case 5:
        	filter2(ip, h1, h2);
        	break;
        case 6:
        	filter2(ip, h3, h4);
        	break;
            
        default:
        
        }
       
    }

	private void filter(ImageProcessor ip, double[][] filter) {
        ImageProcessor copy = ip.duplicate();
        int M = ip.getWidth();
        int N = ip.getHeight();
        
        for (int u = 1; u <= M - 2; u++) {
	        for (int v = 1; v <= N - 2; v++) {
		        // compute filter result for position (u,v):
		        double sum = 0;
		        for (int i = -1; i <= 1; i++) {
			        for (int j = -1; j <= 1; j++) {
				        int p = copy.getPixel(u + i, v + j);
				        // get the corresponding filter coefficient:
				        double c = filter[j + 1][i + 1];
				        sum = sum + c * p;
			        }
		        }
	        int q = (int) Math.round(sum);
	        ip.putPixel(u, v, q);
	        }
        }
	}
	private void filter2(ImageProcessor ip, double[][] filter1, double[][] filter2) {
        ImageProcessor copy = ip.duplicate();
        int M = ip.getWidth();
        int N = ip.getHeight();
        
        for (int u = 1; u <= M - 2; u++) {
	        for (int v = 1; v <= N - 2; v++) {
		        // compute filter result for position (u,v):
		        double sum1 = 0;
		        double sum2 = 0;
		        for (int i = -1; i <= 1; i++) {
			        for (int j = -1; j <= 1; j++) {
				        int p = copy.getPixel(u + i, v + j);
				        // get the corresponding filter coefficient:
				        double c1 = filter1[j + 1][i + 1];
				        double c2 = filter2[j + 1][i + 1];
				        sum1 = sum1 + c1 * p;
				        sum2 = sum2 + c2 * p;
			        }
		        }
	        int q = (int) Math.round(Math.sqrt(Math.pow(sum1, 2)+Math.pow(sum2, 2)));
	        ip.putPixel(u, v, q);
	        }
        }
	}

}