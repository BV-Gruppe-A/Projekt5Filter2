package com.mycompany.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.util.Arrays;

public class Project5_PlugIn implements PlugInFilter {
    
	final int BLACK = 0;
	final int WHITE = 255;
	
	final int r = 3; // specifies the size of the filter
	
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
        
        int whichMethod = (int)IJ.getNumber("Which of the three Filters or combinations should be used? (Input: 1-3)", 1);
        
        switch(whichMethod) {
        // Medianfilter
        case 1:
        	medianFilter(ip);
            break;
        //Morphologischen Öffnen
        case 2:
        	filter(ip, h2);
            break;
        //Morpholohisches Schließen
        case 3:
        	filter(ip, h3);
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

	private void medianFilter(ImageProcessor ip) {
		int N = ip.getHeight();
		int M = ip.getWidth();
		ImageProcessor copy = ip.duplicate();
		// vector to hold pixels from (2r+1)x(2r+1) neighborhood:
		int[] A = new int[(2 * r + 1) * (2 * r + 1)];
		
		// index of center vector element n = 2(r2 + r):
		int n = 2 * (r * r + r);
		
		for (int u = r; u <= M - r - 2; u++) {
			for (int v = r; v <= N - r - 2; v++) {
				// fill the pixel vector A for filter position (u,v):
				int k = 0;
				for (int i = -r; i <= r; i++) {
					for (int j = -r; j <= r; j++) {
						A[k] = copy.getPixel(u + i, v + j);
						k++;
					}
				}
				// sort vector A and take the center element A[n]:
				Arrays.sort(A);
				ip.putPixel(u, v, A[n]);
			}
		}
	}
	
}