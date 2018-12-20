package com.mycompany.imagej;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.util.Arrays;

public class Project5_PlugIn implements PlugInFilter {
    
	final int BLACK = 0;
	final int WHITE = 255;
	
	// specifies the size of the filter as a radius (r = 1 --> 3x3)
	final int radius = 3;	
	// specifies the amount of pixel left to each border (before the filter begins)
	int pxToBorder = 2;
	 
	final int [][] structure01 = {
			{1,1,1},
			{1,2,1},
			{1,1,1}
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
        	pxToBorder = radius;
        	break;
        //Morphologischen Öffnen
        case 2:
        	erosion(ip, structure01);
        	dilation(ip, structure01);
        	pxToBorder = 2;
            break;
        //Morpholohisches Schließen
        case 3:
        	dilation(ip, structure01);
        	erosion(ip, structure01);
        	pxToBorder = 2;
        	break;
        default:       
        }
        
        makeBorderBlack(ip, pxToBorder);              
    }

	private void dilation(ImageProcessor ip, int[][] se) {
        ImageProcessor copy = ip.duplicate();
        int M = ip.getWidth();
        int N = ip.getHeight();
        
        for (int u = 1; u <= M - 2; u++) {
	        for (int v = 1; v <= N - 2; v++) {
		        // compute filter result for position (u,v):
		        int max = 0;
		        for (int i = -1; i <= 1; i++) {
			        for (int j = -1; j <= 1; j++) {
				        int p = copy.getPixel(u + i, v + j) + se[i+1][j+1];
				        // check for new max
				        if(p > max) {
				        	max = p;
				        }
			        }
		        }
	        ip.putPixel(u, v, max);
	        }
        }
	}

	private void erosion(ImageProcessor ip, int[][] se) {
        ImageProcessor copy = ip.duplicate();
        int M = ip.getWidth();
        int N = ip.getHeight();
        
        for (int u = 1; u <= M - 2; u++) {
	        for (int v = 1; v <= N - 2; v++) {
		        // compute filter result for position (u,v):
		        int min = 300;
		        for (int i = -1; i <= 1; i++) {
			        for (int j = -1; j <= 1; j++) {
				        int p = copy.getPixel(u + i, v + j) - se[i+1][j+1];
				        // check for new max
				        if(p < min) {
				        	min = p;
				        }
			        }
		        }
	        ip.putPixel(u, v, min);
	        }
        }
	}

	private void medianFilter(ImageProcessor ip) {
		int N = ip.getHeight();
		int M = ip.getWidth();
		ImageProcessor copy = ip.duplicate();
		// vector to hold pixels from (2r+1)x(2r+1) neighborhood:
		int[] A = new int[(2 * radius + 1) * (2 * radius + 1)];
		
		// index of center vector element n = 2(r2 + r):
		int n = 2 * (radius * radius + radius);
		
		for (int u = radius; u <= M - radius - 2; u++) {
			for (int v = radius; v <= N - radius - 2; v++) {
				// fill the pixel vector A for filter position (u,v):
				int k = 0;
				for (int i = -radius; i <= radius; i++) {
					for (int j = -radius; j <= radius; j++) {
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
	
	/**
	 * paints the outer border (= everything not touched by the filters) of the picture black
	 * @param ip image
	 * @param pxToBorder width of the border (in pixel)
	 */
	private void makeBorderBlack(ImageProcessor ip, int pxToBorder) {
		int height = ip.getHeight();
		int width = ip.getWidth();
		
		for(int i = 0; i <= width; i++) {
			for(int j = 0; j <= height; j++) {
				if(i < pxToBorder || i >= (width - 1 - pxToBorder) || j < pxToBorder || j >= (height - 1 - pxToBorder)) {
					ip.putPixel(i, j, BLACK);
					// IJ.log("x: " + i + "  y: " + j + "  height: " + height + "  width: " + width);
				}
			}
		}
	}	
}