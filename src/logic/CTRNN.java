package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.org.apache.xerces.internal.parsers.IntegratedParserConfiguration;

import display.TrackerGui;

public class CTRNN {
	
	//Weights from the mentioned neuron layer unless stated otherwise
	
	//nr = input neurons*first layer hidden
	public double[] inputWeights;
	
	//nr = hidden*next layer for each layer
	public double[][] hiddenWeights;
	
	//nr = hidden neurons + output neurons
	public double[] biasWeights;
	
	
	public double[] gains;
	
	//nr = hidden neurons + output neurons
	public double[] timeConstants;
	
	//nr = hidden + output neurons
	public double[] internalStates;
	
	//ann layout
	public int[] layout;
	
	Settings set;

	public CTRNN(int[] layout, List<Double> phenotype, Settings set) {
		this.set = set;
		this.layout = layout;
		int hiddenPlusOutputNeurons = 0;
		for (int i=1; i<layout.length; i++) {
			hiddenPlusOutputNeurons += layout[i];
		}
		internalStates = new double[hiddenPlusOutputNeurons];
		gains = new double[hiddenPlusOutputNeurons];
		timeConstants = new double[hiddenPlusOutputNeurons];
		biasWeights = new double[hiddenPlusOutputNeurons];
		
		inputWeights = new double[layout[0]*layout[1]];
		
		hiddenWeights = new double[layout.length-2][0];
		for (int i=0; i<layout.length-2; i++) {
			hiddenWeights[i] = new double[layout[i+1]*layout[i+2]];
		}
		
		//Sorts phenotype values into weights/gains/timeconstants
		int count = 0;
		for (int i=0; i<layout.length+2; i++) {
			if (i<layout.length-1) {
				for (int j=0; j<layout[i]*layout[i+1]; j++) {
					if (i==0) {
						inputWeights[j] = phenotype.get(j);
					} else  {
						hiddenWeights[i-1][j] = phenotype.get(count+j);
					}
				}
				if (i==0) {
					count += inputWeights.length;					
				} else {
					count += hiddenWeights[i-1].length;
				}
				
			} else if (i == layout.length-1) {
				for (int j=0; j<biasWeights.length; j++) {
					biasWeights[j] = phenotype.get(count+j);
				}
				count += biasWeights.length;
				
			} else if (i == layout.length) {
				for (int j=0; j<gains.length; j++) {
					gains[j] = phenotype.get(count+j);
				}
				count += gains.length;
				
			} else if (i == layout.length + 1) {
				for (int j=0; j<timeConstants.length; j++) {
					timeConstants[j] = phenotype.get(count+j);
				}
				count += timeConstants.length;
			} else {
				System.err.println("Got unused else in CTRNN constructor switch");
			}
		}
		
		
		scaleRanges();
	}
	
	public CTRNN(int[] layout, List<Double> phenotype, Settings set, boolean t) {
		this.set = set;
		this.layout = layout;
		int hiddenPlusOutputNeurons = 0;
		for (int i=1; i<layout.length; i++) {
			hiddenPlusOutputNeurons += layout[i];
		}
		internalStates = new double[hiddenPlusOutputNeurons];
		gains = new double[hiddenPlusOutputNeurons];
		timeConstants = new double[hiddenPlusOutputNeurons];
		biasWeights = new double[hiddenPlusOutputNeurons];
		
		inputWeights = new double[layout[0]*layout[1]];
		
		hiddenWeights = new double[layout.length-2][0];
		for (int i=0; i<layout.length-2; i++) {
			hiddenWeights[i] = new double[layout[i+1]*layout[i+2]];
		}
		
		//Sorts phenotype values into weights/gains/timeconstants
		int count = 0;
		for (int i=0; i<layout.length+2; i++) {
			if (i<layout.length-1) {
				for (int j=0; j<layout[i]*layout[i+1]; j++) {
					if (i==0) {
						inputWeights[j] = phenotype.get(j);
					} else  {
						hiddenWeights[i-1][j] = phenotype.get(count+j);
					}
				}
				if (i==0) {
					count += inputWeights.length;					
				} else {
					count += hiddenWeights[i-1].length;
				}
				
			} else if (i == layout.length-1) {
				for (int j=0; j<biasWeights.length; j++) {
					biasWeights[j] = phenotype.get(count+j);
				}
				count += biasWeights.length;
				
			} else if (i == layout.length) {
				for (int j=0; j<gains.length; j++) {
					gains[j] = phenotype.get(count+j);
				}
				count += gains.length;
				
			} else if (i == layout.length + 1) {
				for (int j=0; j<timeConstants.length; j++) {
					timeConstants[j] = phenotype.get(count+j);
				}
				count += timeConstants.length;
			} else {
				System.err.println("Got unused else in CTRNN constructor switch");
			}
		}
		
		
		scaleRangesForPrint();
	}
	
	
	public String chooseMoveNoPull(double[] input) {
		
		double threshold;
		if (set.isNoWrap()) {
			threshold = 0.0001;
		} else {
			threshold = 0.002;
		}
		
		double[] out = runNN(input);
		
		//System.out.println(out[0] + ", " + out[1]);
		
		double max = Math.max(out[0], out[1]);
		
		if (max == out[0]) {
			if (out[0] > out[1] + threshold) {
				return "right";
			} else {
				return "";
			}
		} else if (max == out[1]) {
			if (out[1] > out[0] + threshold) {
				return "left";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	public String chooseMovePull(double[] input) {
		double[] out = runNN(input);
		double max = out[0];
		double maxIndex = 0;
		
		for (int i=1; i<out.length; i++) {
			if (out[i] > max) {
				max = out[i];
				maxIndex = i;
			}
		}
		
		if (maxIndex == 0) {
			if (out[0] > out[1] + 0.003 && out[0] > out[2] + 0.003) {
				return "left";
			} else {
				return "";
			}
		} else if (maxIndex == 1) {
			if (out[1] > out[0] + 0.003 && out[1] > out[2] + 0.003) {
				return "right";
			} else {
				return "";
			}
		}
		else if (maxIndex == 2) {
			if (out[2] > out[0] + 0.001 && out[2] > out[1] + 0.001) {
				return "pull";
			} else {
				return "";
			}	
		}
		else {
			return "";
		}
	}
	
	//Runs the neural network, returning the output from output nodes
	
	/*
	 * Steps:
	 * calc si's
	 * calc dyi's
	 * from these, calc
	 * update yi's
	 * calc oi's
	 */
	
	
	//TODO: check if focus is on correct layer for most funcs ("from" layer or "to" layer)
	public double[] runNN(double[] input) {
		double[] output = new double[layout[layout.length-1]];
		double[] ois = new double[layout[layout.length-1]];
		double[] dyis;
		int count = 0;
		//ann depth is layout.length
		for (int i=0; i<layout.length-1; i++) {
			//calc si's
			double[] sis;
			if (i==0) {
				sis = integrateInputs(input, inputWeights);
			} else {
				sis = integrateInputs(ois, hiddenWeights[i-1]);
			}
			//calc dyi's
			 dyis = layerNeuronChanges(count, count+layout[i+1], sis);
			
			
			//update yi's
			for (int j=count; j<count+layout[i+1]; j++) {
				internalStates[j] += dyis[j-count];	
			}
			
			//calc oi's
			ois = layerActivationValues(count, count+layout[i+1]);
			count+=layout[i+1];
		}
		if (ois.length != output.length) {
			System.err.println("different length on final activations and nr of output neurons");
			System.out.println("Printing");
			System.out.println(ois.length);
			System.out.println(output.length);
		}
		output = ois;
		return output;
	}
	
	//All activation values for one layer
	public double[] layerActivationValues(int start, int end) {
		double[] res = new double[end-start];
		for (int i=start; i<end; i++) {
			res[i-start] = activationValue(gains[i], internalStates[i]);
		}
		return res;
	}
	
	//all neuron changes in one layer
	public double[] layerNeuronChanges(int start, int end, double[] integratedInputs) {
		double[] res = new double[end-start];
		
		for (int i=start; i<end; i++) {
			res[i-start] = neuronChange(timeConstants[i], internalStates[i], integratedInputs[i-start], biasWeights[i]);
		}
		
		return res;
	}
	
	//Formulas
	
	
	//"si" in formula, combines the inputs going into a neuron layer
	public double[] integrateInputs(double[] prevLayerOutputs, double[] connectingWeights) {
		int resLength = connectingWeights.length/prevLayerOutputs.length;
		double[] res = new double[resLength];
		
		for (int i=0; i<resLength; i++) {
			double temp = 0;
			for (int j=0; j<prevLayerOutputs.length; j++) {
				
				//j+i*prevLayerOutputs.length gives first j weights for first neuron, next j weights for next neuron, etc
				
				temp += prevLayerOutputs[j]*connectingWeights[j+i*prevLayerOutputs.length];
			}
			res[i] = temp;
		}
		
		return res;
		
	}
	
	//"dyi/dt" in formula, Calculates change in neuron at each time step
	public double neuronChange(double timeConstant, double internalState, double integratedInput, double bias){
		return (-internalState+integratedInput+bias)/timeConstant;
	}
	
	//"oi" in formula, Activation value for output
	public double activationValue(double gain, double internalState) {
		return 1/(1+Math.exp(-gain*internalState));
	}
	
	
	//Calc length of phenotype given CTRNN layout
	/*
	 * The value is all ANN weights (each layer multiplied by the next)
	 * Biasweights, gains, timeconstants (all equal to hidden plus output neurons)
	 * 
	 */
	
	
	public int lengthOfPhenotype() {
		int res = 0;
		int nrHiddenPlusOutput = 0;
		
		for (int i=0; i<layout.length-1; i++) {
			res += layout[i]*layout[i+1];
			nrHiddenPlusOutput += layout[i+1];
		}
		
		res+= nrHiddenPlusOutput*3;
		
		return res;
		
	}
	
	public void scaleRanges() {
		int max = (int)Math.pow(2, 8);
		
		//weights range -5.0 +5.0
		for (int i=0; i<inputWeights.length; i++) {
			inputWeights[i] = UtilMethods.remap(inputWeights[i], 0, max, -5.0, 5.0);
		}
		
		for (int i=0; i<hiddenWeights.length; i++) {
			for (int j=0; j<hiddenWeights[i].length; j++) {
				hiddenWeights[i][j] = UtilMethods.remap(hiddenWeights[i][j], 0, max, -5.0, 5.0);
			}
		}
		
		for (int i=0; i<gains.length; i++) {
			//bias range -10.0 0.0
			biasWeights[i] = UtilMethods.remap(biasWeights[i], 0, max, -10.0, 0.0);
			//gains range +1.0 +5.0
			gains[i] = UtilMethods.remap(gains[i], 0, max, 1.0, 5.0);
			//time constant range +1.0 +2.0
			timeConstants[i] = UtilMethods.remap(timeConstants[i], 0, max, 1.0, 2.0);
		}
	}
	
	public void scaleRangesForPrint() {
		int max = (int)Math.pow(2, 8);
		
		//weights range -5.0 +5.0
		for (int i=0; i<inputWeights.length; i++) {
			inputWeights[i] = UtilMethods.remap(inputWeights[i], 0, max, -5.0, 5.0);
		}
		
		for (int i=0; i<hiddenWeights.length; i++) {
			for (int j=0; j<hiddenWeights[i].length; j++) {
				hiddenWeights[i][j] = UtilMethods.remap(hiddenWeights[i][j], 0, max, -5.0, 5.0);
			}
		}
		
		for (int i=0; i<gains.length; i++) {
			//bias range -10.0 0.0
			biasWeights[i] = UtilMethods.remap(biasWeights[i], 0, max, -10.0, 0.0);
			//gains range +1.0 +5.0
			gains[i] = UtilMethods.remap(gains[i], 0, max, 1.0, 5.0);
			//time constant range +1.0 +2.0
			timeConstants[i] = UtilMethods.remap(timeConstants[i], 0, max, 1.0, 2.0);
		}
		
		System.out.println("Input weights :");
		for (int i=0; i<inputWeights.length; i++) {
			System.out.print(inputWeights[i] + ", ");
		}
		System.out.println("Hidden Weights :");
		for (int j=0; j<hiddenWeights[0].length; j++) {
			System.out.print(hiddenWeights[0][j] + ", ");
		}
		System.out.println("Bias Weights :");
		for (int i=0; i<gains.length; i++) {
			System.out.print(biasWeights[i] + ", ");
		}
		System.out.println("Gains :");
		for (int i=0; i<gains.length; i++) {
			System.out.print(gains[i] + ", ");
		}
		System.out.println("timeConstants :");
		for (int i=0; i<gains.length; i++) {
			System.out.print(timeConstants[i] + ", ");
		}
		
	}
	
	public static void main(String[] args) {
		Settings set = new Settings();
		double[] pheno = new double[] {-2.7734375, -2.109375, 3.203125, 1.015625, -4.5703125, -3.8671875, 3.046875, 0.703125, 4.84375, -4.53125,
				-0.1171875, 3.125, -0.9765625, -2.2265625,
				-0.6640625, -1.6796875, -7.96875, -0.8984375,
				4.828125, 4.4375, 2.90625, 2.109375,
				1.5390625, 1.1640625, 1.84765625, 1.2109375};
		Double[] doubleArray = ArrayUtils.toObject(pheno); 
		List<Double> pheno2 = Arrays.asList(doubleArray);
				
		System.out.println("Creating CTRNN");
		CTRNN ctrnn = new CTRNN(set.getLayout(), pheno2, set);
		double[] objectMiddle = new double[] {0, 0, 1, 0, 0};
		double[] objectMiddle2 = new double[] {0, 1, 1, 1, 0};
		double[] objectMiddle3 = new double[] {0, 1, 0, 0, 0};
		double[] objectMiddle4 = new double[] {0, 0, 0, 1, 0};
		double[] objectLeft = new double[] {1, 0, 0, 0, 0};
		double[] objectRight = new double[] {0, 0, 0, 0, 1};
		double[] noObject = new double[] {0, 0, 0, 0, 1};
		
		System.out.println("Choosing moves");
		/*
		System.out.println(ctrnn.chooseMoveNoPull(objectMiddle));
		System.out.println(ctrnn.chooseMoveNoPull(objectMiddle2));
		System.out.println(ctrnn.chooseMoveNoPull(objectLeft));
		*/
		System.out.println(ctrnn.chooseMoveNoPull(objectLeft));
		System.out.println(ctrnn.chooseMoveNoPull(objectMiddle3));
		System.out.println(ctrnn.chooseMoveNoPull(objectMiddle));
		System.out.println(ctrnn.chooseMoveNoPull(objectMiddle4));

		
		

		/*
		System.out.println(ctrnn.chooseMoveNoPull(noObject));
		System.out.println(ctrnn.chooseMoveNoPull(objectRight));
		System.out.println(ctrnn.chooseMoveNoPull(objectRight));
		*/
		
		

		
	}

}
