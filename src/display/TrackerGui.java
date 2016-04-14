package display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Polygon;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import display.GridGui.CellPane;
import display.GridGui.TestPane;
import logic.UtilMethods;

public class TrackerGui extends GridGui{
	private TestPane pane;
	

    public TrackerGui(final String[][] board) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                pane = new TrackerTestPane(board);
                frame.add(pane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public void setContainedItem(final String item, final int x, final int y, final int delay) {
    	
		  try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
			      // Here, we can safely update the GUI
			      // because we'll be called from the
			      // event dispatch thread
			    	try {
			    		Thread.sleep(delay);
			    	} catch (InterruptedException e) {
			    		// TODO Auto-generated catch block
			    		e.printStackTrace();
			    	}
			    	pane.getComponentArray()[x][y].setContainedItem(item);
			    }
			  });
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void removeContainedItem(final String removeItem, final int x, final int y, final int delay) {
    	
		  try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
			      // Here, we can safely update the GUI
			      // because we'll be called from the
			      // event dispatch thread
			    	try {
			    		Thread.sleep(delay);
			    	} catch (InterruptedException e) {
			    		// TODO Auto-generated catch block
			    		e.printStackTrace();
			    	}
			    	String contained = pane.getComponentArray()[x][y].getContainedItem();
			    	if (contained == removeItem || contained == null) {
			    		pane.getComponentArray()[x][y].setContainedItem("");			    		
			    	}
			    	if (contained == "agentIsUnder" && removeItem == "agent") {
			    		pane.getComponentArray()[x][y].setContainedItem("");
			    	}
			    }
			  });
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public class TrackerTestPane extends TestPane {
    	
    	CellPane[][] components;

        public TrackerTestPane(String[][] board) {
            setLayout(new GridBagLayout());
            
            //Check which is which in this implementation
            int width = board.length;
            int height = board[0].length;
            
            components = new TrackerCellPane[width][height];

            GridBagConstraints gbc = new GridBagConstraints();
            for (int row = 0; row < height; row++) {
            	
                for (int col = 0; col < width; col++) {
                    gbc.gridx = col;
                    gbc.gridy = row;

                    CellPane cellPane = new TrackerCellPane(board[col][row]);
                    Border border = null;
                    if (row < height-1) {
                        if (col < width-1) {
                            border = new MatteBorder(1, 1, 0, 0, Color.GRAY);
                        } else {
                            border = new MatteBorder(1, 1, 0, 1, Color.GRAY);
                        }
                    } else {
                        if (col < width-1) {
                            border = new MatteBorder(1, 1, 1, 0, Color.GRAY);
                        } else {
                            border = new MatteBorder(1, 1, 1, 1, Color.GRAY);
                        }
                    }
                    cellPane.setDefaultBorder(border);
                    if (cellPane.getBorder() == null) {
                    	cellPane.setBorder(border);
                    }
                    components[col][row] = cellPane;
                    add(cellPane, gbc);
                }
            }
        }
        
        public CellPane[][] getComponentArray() {
        	repaint();
        	return components;
        }
    }
    

    public class TrackerCellPane extends CellPane {
    	
        private Color defaultBackground;

        public TrackerCellPane(String containedItem) {
        	super(containedItem);
        	
        	defaultBackground = getBackground();
        	
        	if (containedItem == "agent") {
        		changeBorder("agent");
        		if (getBackground() != defaultBackground) {
        			setBackground(defaultBackground);        			
        		}
        	}
        		/*
        	 else if (containedItem == "agentIsUnder") {
        		changeBorder("agent");
        		defaultBackground = getBackground();
        		setBackground(Color.BLACK);
        		}
        		*/
        	else if (containedItem == "") {
        		if (getBackground() != defaultBackground) {
        			setBackground(defaultBackground);        			
        		}
        	}
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(50, 50);
        }
        
        @Override
        public void paintComponent(Graphics g) {
        	g.clearRect(0, 0, 50, 50);
        	super.paintComponent(g);
        	
        	if (containedItem == "agent") {
        		changeBorder("agent");
        		if (getBackground() != defaultBackground) {
        			setBackground(defaultBackground);        			
        		}
        	} else if (containedItem == "agentIsUnder") {
        		changeBorder("agent");        		
        		setBackground(Color.BLACK);
        		
        	} else if (containedItem == "object") {
        		changeBorder("object");
        		if (getBackground() != defaultBackground) {
        			setBackground(defaultBackground);        			
        		}
        	} else if (containedItem == "") {
        		changeBorder("default");
        		if (getBackground() != defaultBackground) {
        			setBackground(defaultBackground);        			
        		}
        		
        	} else if (containedItem == "agentIsGreen") {
        		setBackground(Color.green);
        	}
        	
        	
        	/*
        	} else if (containedItem == "down") {
        		addShape(g, down, "agent");
        	} else if (containedItem == "up") {
        		addShape(g, up, "agent");
        	} else if (containedItem == "poison") {
        		addShape(g, null, "poison");
        	} else if (containedItem == "food") {
        		addShape(g, null, "food");
        	}
        	*/
        	
        }
        
        public void changeBorder(String type) {
        	if (type == "agent") {
        		Border newBorder = new MatteBorder(4, 4, 4, 4, Color.RED);
        		setBorder(newBorder);
        	} else if (type == "object") {
        		Border newBorder = new MatteBorder(4, 4, 4, 4, Color.BLUE);
        		setBorder(newBorder);	
        	} else if (type == "default") {
        		setBorder(defaultBorder);
        	}
        	repaint();
        	
        }
        
        public void addShape(Graphics g, Polygon shape, String type) {
        	
        }
        
        public void setContainedItem(String item) {
        	containedItem = item;
        	
        }
        
        
    }
    
    public static void main(String[] args) {
    	String[][] board = UtilMethods.createTrackerBoard();
    	TrackerGui gui = new TrackerGui(board);
        
        /*
        System.out.println(fl.pane);
        System.out.println(fl.pane.getComponentArray());
        */
        
    }

}
