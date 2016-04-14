package logic;

import java.math.BigDecimal;
import java.math.RoundingMode;


//Collection of static helpers
public class UtilMethods {

	public UtilMethods() {
		// TODO Auto-generated constructor stub
	}
    public static BigDecimal scale(final BigDecimal valueIn, final BigDecimal baseMin, final BigDecimal baseMax, final BigDecimal limitMin, final BigDecimal limitMax) {
        return ((limitMax.subtract(limitMin)).multiply((valueIn.subtract(baseMin)).divide((baseMax.subtract(baseMin)), 10, RoundingMode.HALF_UP).add(limitMin)));
    }
    
    public static double scale(final double oldVal, final double oldMin, final double oldMax, final double newMin, final double newMax) {
    	double oldRange = (oldMax - oldMin);
    	if (oldRange == 0) {
    		return newMin;
    	}
    	double newRange = (newMax - newMin); 
    	double newVal = (((oldVal - oldMin) * newRange) / oldRange) + newMin;
    	if (Double.isNaN(newVal)) {
    		System.err.println("Scale returned NaN");
    	} else if (Double.isInfinite(newVal)) {
    		return newMin;
    	}
        return newVal;
    }
    
    public static double remap(double x, double oMin, double oMax, double nMin, double nMax) {
        //range check
        if (oMin == oMax) {
        	System.out.println("Warning: Zero input range");
        	return (nMin+nMax)/2;
        	
        }

        if (nMin == nMax) {
        	System.out.println("Warning: Zero output range");
        	return (nMin+nMax)/2;        	
        }

        //check reversed input range
        boolean reverseInput = false;
        double oldMin = Math.min( oMin, oMax );
        double oldMax = Math.max( oMin, oMax );
        if (oldMin != oMin) {
        	reverseInput = true;        	
        }

        //check reversed output range
        boolean reverseOutput = false;   
        double newMin = Math.min( nMin, nMax );
        double newMax = Math.max( nMin, nMax );
        if (newMin != nMin) {
        	reverseOutput = true;
        }

        double portion = (x-oldMin)*(newMax-newMin)/(oldMax-oldMin);
        if (reverseInput) {
        	portion = (oldMax-x)*(newMax-newMin)/(oldMax-oldMin);      	
        }

        double result = portion + newMin;
        if (reverseOutput) {
        	result = newMax - portion;        	
        }

        return result;

    }
    
    public static double remap2(double x, double oMin, double oMax, double nMin, double nMax) {
        //range check
        if (oMin == oMax) {
        	System.out.println("Warning: Zero input range");
        	return 0;
        }

        if (nMin == nMax) {
        	System.out.println("Warning: Zero output range");
        	return 0;       	
        }

        //check reversed input range
        boolean reverseInput = false;
        double oldMin = Math.min( oMin, oMax );
        double oldMax = Math.max( oMin, oMax );
        if (oldMin != oMin) {
        	reverseInput = true;        	
        }

        //check reversed output range
        boolean reverseOutput = false;   
        double newMin = Math.min( nMin, nMax );
        double newMax = Math.max( nMin, nMax );
        if (newMin != nMin) {
        	reverseOutput = true;
        }

        double portion = (x-oldMin)*(newMax-newMin)/(oldMax-oldMin);
        if (reverseInput) {
        	portion = (oldMax-x)*(newMax-newMin)/(oldMax-oldMin);      	
        }

        double result = portion + newMin;
        if (reverseOutput) {
        	result = newMax - portion;        	
        }

        return result;

    }
    
	public static String arrayToString(String[][] a) {
	    String aString;     
	    aString = "";
	    int column;
	    int row;

	    for (row = 0; row < a.length; row++) {
	        for (column = 0; column < a[0].length; column++ ) {
	        aString = aString + " " + a[row][column];
	        }
	    aString = aString + "\n";
	    }

	    return aString;
	}
	public static String[][] createTrackerBoard() {
		String[][] board = new String[30][15];
		
		for (int i=0; i<board.length; i++) {
			for (int j=0; j<board[0].length; j++) {
				
			}
		}
		
		for (int i=0; i<5; i++) {
			board[i+10][14] = "agent";
		}
		
		return board;
		
	}
}
