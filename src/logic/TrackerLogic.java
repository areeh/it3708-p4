package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import display.TrackerGui;

/*
 * This class executes moves on the model and calls the GUI to display the changes
 */


//TODO: No wrapping
//TODO: Handle edge case when collision happens while agent is wrapping

public class TrackerLogic {
	
	int[] leftAgentPos;
	int[] rightAgentPos;
	int[] prevLeftAgentPos;
	int[] prevRightAgentPos;
	
	//default
	int agentSize = 5;
	
	int[] leftObjectPos;
	int[] rightObjectPos;
	int[] prevRightObjectPos;
	int[] prevLeftObjectPos;
	int objectSize;
	
	//default
	int maxObjectSize = 6;	
	
	String[][] board;
	double score;
	TrackerGui gui;
	
	//default
	int delay = 50;
	
	Random rng;
	
	boolean spawn;
	boolean repaintAgent;
	private int sequenceCount;
	private List<int[]> objectSequence;
	private boolean pull;
	private boolean first;
	private Settings set;
	private String prevMove = "";
	private String prevprevMove = "";
	private String prevprevprevMove = "";
	private boolean hitLeft;

	public TrackerLogic(TrackerGui gui, List<int[]> objectSequence, Settings set) {
		this.objectSequence = objectSequence;
		rng = new Random();
		this.gui = gui;
		board = UtilMethods.createTrackerBoard();
		leftAgentPos = new int[] {10, 14};
		rightAgentPos = new int[] {14, 14};
		prevLeftAgentPos = leftAgentPos;
		prevRightAgentPos = rightAgentPos;
		score = 0;
		sequenceCount = 0;
		spawn = true;
		repaintAgent = false;
		pull = false;
		first = true;
		this.set = set;
		hitLeft = true;
	}
	
	
	//Gui calls
	
	public void moveAgentGui(String direction) {
		if (set.isNoWrap()) {
			if (hitLeft && leftAgentPos[0] == 0) {
				if (gui != null) {
					System.out.println("Got score");
				}
				score += 7;
				hitLeft = false;
			} else if (!hitLeft && rightAgentPos[0] == 29) {
				if (gui != null) {
					System.out.println("Got score");
				}
				score += 7;
				hitLeft = true;
			}
			
		}
		if (direction == "left") {
			if (set.isNoWrap()) {
				if (prevMove == "left" && prevprevMove == "left" && prevprevprevMove=="left") {
					if (gui != null) {
						System.out.println("Got move seq score");
						
					}
					score+=0.04;
				}
				prevprevprevMove = prevprevMove;
				prevprevMove = prevMove;
				prevMove = "left";
			
				if (leftAgentPos[0] == 0) {
					score -= 1;
				} else if (rightAgentPos[0] == 29) {
					score += 0;
				}
				moveAgentNoWrap("left");
			} else {
				moveAgent("left");				
			}
			if (gui != null) {
				if (repaintAgent) {
					repaintAgent();
					gui.removeContainedItem("agent", prevRightAgentPos[0], prevRightAgentPos[1], 0);	
				} else {
					gui.setContainedItem("agent", leftAgentPos[0], leftAgentPos[1], 0);
					gui.removeContainedItem("agent", prevRightAgentPos[0], prevRightAgentPos[1], 0);					
				}				
			}
			
		} else if (direction == "right") {
			if (set.isNoWrap()) {
				if (prevMove == "right" && prevprevMove == "right" && prevprevprevMove == "right") {
					if (gui != null) {
						System.out.println("Got move seq score");
						
					}
					score+=0.04;
				}
				prevprevprevMove = prevprevMove;
				prevprevMove = prevMove;
				prevMove = "right";
			
				if (leftAgentPos[0] == 0) {
					score += 0;
				} else if (rightAgentPos[0] == 29) {
					score -= 1;
				}
				moveAgentNoWrap("right");
			} else {
				moveAgent("right");		
			}
			if (gui != null) {
				if (repaintAgent) {
					repaintAgent();
					gui.removeContainedItem("agent", prevLeftAgentPos[0], prevLeftAgentPos[1], 0);	
				} else {
					gui.setContainedItem("agent", rightAgentPos[0], rightAgentPos[1], 0);
					gui.removeContainedItem("agent", prevLeftAgentPos[0], prevLeftAgentPos[1], 0);	
				}				
			}
			
		} else if (direction == "pull") {
			dropObjectGui();
			pull = true;
			
		} else if (direction == "") {
		
		}
		catchOrCollision();
		
	}
	
	public void spawnObjectGui() {
		spawn = false;
		spawnObject(objectSequence);
		first = false;
		
		List<int[]> oP = getPositions("object");
		List<int[]> pOP = getPrevPositions("object");
		boolean wait = true;
		
		if (gui != null) {
			for (int i=0; i<oP.size(); i++) {
				if (wait) {
					gui.setContainedItem("object", oP.get(i)[0], oP.get(i)[1], delay);
					gui.removeContainedItem("object", pOP.get(i)[0], pOP.get(i)[1], 0);	
					wait = false;
				} else {
					gui.setContainedItem("object", oP.get(i)[0], oP.get(i)[1], 0);
					gui.removeContainedItem("object", pOP.get(i)[0], pOP.get(i)[1], 0);	
				}			
			}				
			
		}
	}
	
	
	public void dropObjectGui() {
		spawn = true;
		
		int count = 0;
		double[] sense = getSensorInputs();
		for (int i=0; i<5; i++) {
			if (sense[i] > 0.5) {
				count++;
			}
		}
		List<int[]> oP = getPositions("agent");
		if (gui != null) {
			boolean wait = true;
			for (int i=0; i<oP.size(); i++) {
				if (wait) {
					gui.setContainedItem("agentIsGreen", oP.get(i)[0], oP.get(i)[1], 100);
					wait = false;
				} else {
					gui.setContainedItem("agentIsGreen", oP.get(i)[0], oP.get(i)[1], 0);
				}			
			}
			for (int i=0; i<oP.size(); i++) {
				if (wait) {
					gui.setContainedItem("agent", oP.get(i)[0], oP.get(i)[1], 100);
					wait = false;
				} else {
					gui.setContainedItem("agent", oP.get(i)[0], oP.get(i)[1], 0);
				}			
			}
			
		}
		
		
		
		if (count==objectSize && objectSize < 5) {
			score += 7;
		} else if (count > 0 && objectSize > 4) {
			score -= 10;
		} else {
			score -=1;
		}
	}
	
	public void moveObjectGui() {
		if (spawn) {
			spawnObjectGui();
		} else {
			moveObject();
			
			//Object positions and previous object positions
			List<int[]> oP = getPositions("object");
			List<int[]> pOP = getPrevPositions("object");
			boolean wait = true;
			
			if (gui != null) {
				for (int i=0; i<oP.size(); i++) {
					if (wait) {
						gui.setContainedItem("object", oP.get(i)[0], oP.get(i)[1], delay);
						gui.removeContainedItem("object", pOP.get(i)[0], pOP.get(i)[1], 0);	
						wait = false;
					} else {
						gui.setContainedItem("object", oP.get(i)[0], oP.get(i)[1], 0);
						gui.removeContainedItem("object", pOP.get(i)[0], pOP.get(i)[1], 0);	
					}			
				}				
				
			}
		}
		
		
	}
	
	//local helpers
	
	public void spawnObject(List<int[]> objectSequence) {
		objectSize = rng.nextInt(6)+1;
		int pos;
		if (set.isNoWrap()) {
			pos = rng.nextInt(30-objectSize-1);
		} else {
			//TODO: fix edge case
			pos = rng.nextInt(30-objectSize-4)+2;
		}
		
		//objectSize = objectSequence.get(sequenceCount)[0];
		//int pos = objectSequence.get(sequenceCount)[1];
		if (!first) {
			prevLeftObjectPos = leftObjectPos.clone();
			prevRightObjectPos = rightObjectPos.clone();
		}
		sequenceCount ++;
		leftObjectPos  = new int[] {pos, 0};
		rightObjectPos  = new int[] {pos+objectSize-1, 0};
		if (first) {
			prevLeftObjectPos = leftObjectPos.clone();
			prevRightObjectPos = rightObjectPos.clone();
		}
	}
	
	
	//TODO: No wrap
	public void moveAgent(String direction) {
		if (direction == "left") {
			prevLeftAgentPos = leftAgentPos.clone();
			if (leftAgentPos[0] == 0) {
				leftAgentPos[0] = board.length-1;
			} else {
				leftAgentPos[0] -= 1;				
			}
			prevRightAgentPos = rightAgentPos.clone();
			if (rightAgentPos[0] <= 0) {
				rightAgentPos[0] = board.length-1;
			} else {
				rightAgentPos[0] -= 1;				
			}
		} else if (direction == "right") {
			prevLeftAgentPos = leftAgentPos.clone();
			if (leftAgentPos[0] >= board.length-1) {
				leftAgentPos[0] = 0;
			} else {
				leftAgentPos[0] += 1;				
			}
			prevRightAgentPos = rightAgentPos.clone();
			if (rightAgentPos[0] >= board.length-1) {
				rightAgentPos[0] = 0;
			} else {
				rightAgentPos[0] += 1;				
			}
		}
	}
	
	public void moveAgentNoWrap(String direction) {
		if (direction == "left") {
			if (leftAgentPos[0] != 0) {
				prevLeftAgentPos = leftAgentPos.clone();
				leftAgentPos[0] -= 1;				
				prevRightAgentPos = rightAgentPos.clone();
				rightAgentPos[0] -= 1;				
			}
		} else if (direction == "right") {
			if (rightAgentPos[0] < board.length-1) {
				prevRightAgentPos = rightAgentPos.clone();
				rightAgentPos[0] += 1;	
				prevLeftAgentPos = leftAgentPos.clone();
				leftAgentPos[0] += 1;				
			}
		}
	}
	
	public void moveObject() {
		prevLeftObjectPos = leftObjectPos.clone();
		prevRightObjectPos = rightObjectPos.clone();
		
		if (leftObjectPos[1] < 14) {
			leftObjectPos[1] += 1;
			rightObjectPos[1] += 1;			
		} else {
			spawn = true;
		}
	}
	
	
	//TODO: handle collision edge case, think done
	public List<int[]> getPositions(String type) {
		List<int[]> res = new ArrayList<int[]>();
		
		if (type == "agent") {
			res.add(leftAgentPos);
			if (agentSize > 2) {
				for (int i=0; i<agentSize-2; i++) {
					int pos = leftAgentPos[0]+i+1;
					if (pos > 29) {
						pos = 0+(pos-30);
					}
					res.add(new int[] {pos, leftAgentPos[1]});
				}
			res.add(rightAgentPos);
			} else {
				return res;
			}
			
		} else if (type == "object") {
			res.add(leftObjectPos);
			if (objectSize > 2) {
				for (int i=0; i<objectSize-2; i++) {
					int pos = leftObjectPos[0]+i+1;
					if (pos > 29) {
						pos = 0+(pos-30);
					}
					res.add(new int[] {pos, leftObjectPos[1]});
				}
			res.add(rightObjectPos);
			} else {
				return res;
			}
			
		} else {
			System.err.println("invalid type positions");
		}
		
		return res;
	}
	
	public List<int[]> getPrevPositions(String type) {
		List<int[]> res = new ArrayList<int[]>();
		
		if (type == "agent") {
			res.add(prevLeftAgentPos);
			if (agentSize > 2) {
				for (int i=0; i<agentSize-2; i++) {
					res.add(new int[] {prevLeftAgentPos[0]+i+1, prevLeftAgentPos[1]});
				}
			res.add(prevRightAgentPos);
			} else {
				return res;
			}
			
		} else if (type == "object") {
			res.add(prevLeftObjectPos);
			if (objectSize > 2) {
				for (int i=0; i<objectSize-2; i++) {
					res.add(new int[] {prevLeftObjectPos[0]+i+1, prevLeftObjectPos[1]});
				}
			res.add(prevRightObjectPos);
			} else {
				return res;
			}
			
		} else {
			System.err.println("invalid type prevPositions");
		}
		
		return res;
	}


	
	public void catchOrCollision() {
		if (leftObjectPos[1] == leftAgentPos[1]) {
			//max object size 6 gives either end inside agent if collision
			//left object end inside agent? 
			boolean leftInside = (leftObjectPos[0] >= leftAgentPos[0] && leftObjectPos[0] <= rightAgentPos[0]);
			//right object end inside agent?
			boolean rightInside = (rightObjectPos[0] >= leftAgentPos[0] && rightObjectPos[0] <= rightAgentPos[0]);
			
			//Catch
			if (leftInside && rightInside && objectSize < 5) {
				if (gui != null) {
					System.out.println("Printing");
					System.out.println("Catched");					
				}
				score += 3;
				//handle graphics
				repaintAgent = true;
			}
			//Collision
			else if (leftInside || rightInside) {
				if (objectSize > 4) {
					score -= 7;
				} else {
					score-=1;
				}
				if (gui != null) {
					System.out.println("Printed");
					System.out.println("Collided");
					
				}
				//handle graphics
				repaintAgent = true;
				}
			}
		}
		
	public double[] getSensorInputs() {
		double[] res = new double[5];
		int squaresSensed;
		
		//left object end inside agent? 
		boolean leftObjectInside = (leftAgentPos[0] <= leftObjectPos[0] && rightAgentPos[0] >= leftObjectPos[0]);
		//right object end inside agent?
		boolean rightObjectInside = (rightAgentPos[0] >= rightObjectPos[0] && leftAgentPos[0] <= rightObjectPos[0]);
		

		if (leftObjectInside && rightObjectInside) {
			int distFromLeft = (leftObjectPos[0] - leftAgentPos[0]);
			for (int i=0; i<objectSize; i++) {
				res[i+distFromLeft] = 1;
				
				if (i+distFromLeft == 5) {
					return res;
				}
			}
		} else if (leftObjectInside) {
			squaresSensed = objectSize - (rightObjectPos[0] - rightAgentPos[0]);
			if (squaresSensed > 5) {
				squaresSensed = 5;
			}
			for (int i=res.length-squaresSensed; i<res.length; i++) {
				res[i] = 1.0;
			}

			
		} else if (rightObjectInside) {
			squaresSensed = objectSize - (leftAgentPos[0] - leftObjectPos[0]);
			if (squaresSensed > 5) {
				squaresSensed = 5;
			}
			for (int i=0; i<squaresSensed; i++) {
				res[i] = 1.0;
			}

			
		} else {
			//Nothing sensed, returning the 0 initialized res
		}
		
		int count = 0;
		
		for (int i=0; i<res.length; i++) {
			if (res[i] > 0.5) {
				count++;
			}
		}
		
		if (count > objectSize) {
			System.out.println("Should never happen");
		}
		
		return res;
	}
	
	public double[] getSensorInputsNoWrap() {
		double[] res = new double[7];
		int squaresSensed;
		
		//left object end inside agent? 
		boolean leftObjectInside = (leftAgentPos[0] <= leftObjectPos[0] && rightAgentPos[0] >= leftObjectPos[0]);
		//right object end inside agent?
		boolean rightObjectInside = (rightAgentPos[0] >= rightObjectPos[0] && leftAgentPos[0] <= rightObjectPos[0]);
		

		if (leftObjectInside && rightObjectInside) {
			int distFromLeft = (leftObjectPos[0] - leftAgentPos[0]);
			for (int i=0; i<objectSize; i++) {
				res[i+distFromLeft] = 1;
				
				if (i+distFromLeft == 5) {
					return res;
				}
			}
		} else if (leftObjectInside) {
			squaresSensed = objectSize - (rightObjectPos[0] - rightAgentPos[0]);
			if (squaresSensed > 5) {
				squaresSensed = 5;
			}
			for (int i=5-squaresSensed; i<5; i++) {
				res[i] = 1.0;
			}

			
		} else if (rightObjectInside) {
			squaresSensed = objectSize - (leftAgentPos[0] - leftObjectPos[0]);
			if (squaresSensed > 5) {
				squaresSensed = 5;
			}
			for (int i=0; i<squaresSensed; i++) {
				res[i] = 1.0;
			}

			
		} else {
			//Nothing sensed, returning the 0 initialized res
		}
		
		int count = 0;
		
		for (int i=0; i<5; i++) {
			if (res[i] > 0.5) {
				count++;
			}
		}
		
		if (count > objectSize) {
			System.out.println("Should never happen");
		}
		
		if (leftAgentPos[0] == 0) {
			res[5] = 2.0;
		} else if (rightAgentPos[0] == 29) {
			res[6] = 2.0;
		}
		
		return res;
	}
	
	public void repaintAgent() {
		if (gui != null) {
			List<int[]> aP = getPositions("agent");
			for (int i=0; i<aP.size(); i++) {
				gui.setContainedItem("agent", aP.get(i)[0], aP.get(i)[1], 0);
			}
			repaintAgent = false;			
		}
	}
	
	public void colorBackground() {
		if (gui != null) {
			List<int[]> pos = getPositions("agent");
			double[] sense = getSensorInputs();
			for (int i=0; i<sense.length; i++) {
				if (sense[i] == 1.0) {
					gui.setContainedItem("agentIsUnder", pos.get(i)[0], pos.get(i)[1], 0);
				}
			}
		}
	}
	
	public double getScore() {
		return score;
	}
	
 	
	public static void main(String[] args) {
		Settings set = new Settings();
		String[][] board = UtilMethods.createTrackerBoard();
		TrackerGui gui = new TrackerGui(board);
		TrackerLogic logic = new TrackerLogic(gui, set.getObjectSequence(), set);
		
		logic.spawnObjectGui();
		logic.moveAgentGui("left");
		logic.colorBackground();
		for (int i=0; i<18; i++) {
			if (i%2 == 0 || i%3 == 0) {
				logic.moveObjectGui();
				logic.moveAgentGui("right");
				logic.colorBackground();
			} else {
				logic.moveObjectGui();
				logic.moveAgentGui("left");
				logic.colorBackground();
				
			}
		}

		
	}
}
