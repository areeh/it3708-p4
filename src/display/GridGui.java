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
import logic.UtilMethods;

public class GridGui {
	private TestPane pane;
	
	public GridGui() {
		
	}
	
    public GridGui(final String[][] board) {
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
                pane = new TestPane(board);
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

    public class TestPane extends JPanel {
    	
    	CellPane[][] components;
    	
    	public TestPane() {
    		
    	}

        public TestPane(String[][] board) {
            setLayout(new GridBagLayout());
            
            //Check which is which in this impl
            int width = board.length;
            int height = board[0].length;
            
            components = new CellPane[width][height];

            GridBagConstraints gbc = new GridBagConstraints();
            for (int row = 0; row < height; row++) {
            	
                for (int col = 0; col < width; col++) {
                    gbc.gridx = col;
                    gbc.gridy = row;

                    CellPane cellPane = new CellPane(board[col][row]);
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
                    cellPane.setBorder(border);
                    components[col][row] = cellPane;
                    add(cellPane, gbc);
                }
            }
        }
        
        public CellPane[][] getComponentArray() {
        	return components;
        }
    }

    public class CellPane extends JPanel {
    	
    	protected String containedItem;
        private Color defaultBackground;
        protected Border defaultBorder;

        public CellPane(String containedItem) {
        	
        	this.containedItem = containedItem;
        	
        	/*
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    defaultBackground = getBackground();
                    setBackground(Color.BLUE);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(defaultBackground);
                }
            });
            */
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(50, 50);
        }
        
        @Override
        public void paintComponent(Graphics g) {
        	g.clearRect(0, 0, 50, 50);
        	super.paintComponent(g);
        	
        	/*
        	if (containedItem == "left") {
        		addShape(g, left, "agent");        		
        	} else if (containedItem == "right") {
        		addShape(g, right, "agent");
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
        
        public void addShape(Graphics g, Polygon shape, String type) {
        	
        }
        
        public void setContainedItem(String item) {
        	containedItem = item;
        	repaint();
        }
        
        public void setDefaultBorder(Border border) {
        	defaultBorder = border;
        }

		public String getContainedItem() {
			return containedItem;
			
		}
    }
    
    public static void main(String[] args) {
    	String[][] board = UtilMethods.createTrackerBoard();
    	GridGui gui = new GridGui(board);
        
        /*
        System.out.println(fl.pane);
        System.out.println(fl.pane.getComponentArray());
        */
        
    }
}