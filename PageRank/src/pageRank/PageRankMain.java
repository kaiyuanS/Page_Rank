package pageRank;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The main runner and GUI of the page rank calculator
 * @author Kaiyuan Shi
 * @version 1.0
 */
public class PageRankMain {
	
	/** the accuracy of the calculator */
	private static final double DELTA = 1e-5;
	/** the number of pages need to be presented */
	private static int outPageNUmber = 0;
	
	/** a list of pages contain links */
	private static int[] myFrom;
	/** a list of pages were linked */
	private static int[] myTo;
	/** the page rank matrix */
	private static PageRankMatrix myMatrix;
	
	/** the whole page rank */
	private static double[] myRank;
	/** the highest page rank score need to be presented */
	private static double[] myOutRank;
	/** the page ID with the output page rank score */
	private static int[] myOutPage;
	
	/** the file picker */
	private static JFileChooser myFilePicker;
	
	/** time and multiplication counter*/
	private static int counter = 0;
	private static long start = 0;
	private static long end = 0;
	
	/** frame of the GUI */
	private static final JFrame frame = new JFrame("Page Rank Calculator");

	public static void main(String[] args) {
		try {
			outPageNUmber = Integer.valueOf(
					JOptionPane.showInputDialog(
							null,
							"how many pages you want to show?",
							"Page Number",
							JOptionPane.QUESTION_MESSAGE));
			
			System.out.println("start");
			myFilePicker = new JFileChooser();

			readInput();
			start = System.currentTimeMillis();
			System.out.println("finish reading file");
			showFrame();
			findPageRank();
			System.out.println("multiplication finish");
			getRank();
			end = System.currentTimeMillis();
			output();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "Wrong input, please input a number!"
					, "Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()
					, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * this method build the frame
	 */
	private static void showFrame() {
		
		JLabel label = new JLabel("Calculating...");
		label.setPreferredSize(new Dimension(130, 30));
		JButton button = new JButton("Stop and Save");
		button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent anEvent) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                		"Do you wang to stop the calculation?",
                		"Stop and Save",
                		JOptionPane.YES_NO_OPTION)) {
                	if (counter == 0) {
                		JOptionPane.showMessageDialog(null, "No Page Rank Calculated", "Message", JOptionPane.PLAIN_MESSAGE);
                	} else {
                		getRank();
                		end = System.currentTimeMillis();
                		output();
                	}
                	
                	System.exit(0);
                }
                
            }
            
        });
		frame.add(label,BorderLayout.CENTER);
		frame.add(button,BorderLayout.EAST);
		frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
		
	}
	
	/**
	 * this method read the links from a text file
	 */
	private static void readInput() {
		List<Integer> fromList = new ArrayList<Integer>();
		List<Integer> toList = new ArrayList<Integer>();
		String line = null;
		Scanner scanner = null;
        try {

        	myFilePicker.resetChoosableFileFilters();
        	myFilePicker.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        	int select = myFilePicker.showOpenDialog(null);
        	if (!(select == JFileChooser.APPROVE_OPTION))
        		System.exit(0);
        	File result = myFilePicker.getSelectedFile();
        	
        	scanner = new Scanner(new FileInputStream(result.getAbsolutePath()));
            while (scanner.hasNextLine()) {
            	line = scanner.nextLine();
            	
            	String[] edge = line.split(" ");
            	
            	if (edge.length != 3 || !edge[2].equals("1"))
            		throw new IllegalArgumentException(
            				"Invalid input: " + line);
            	
            	fromList.add(Integer.valueOf(edge[0]));
            	toList.add(Integer.valueOf(edge[1]));
            	
            }
            myFrom = new int[fromList.size()];
        	myTo = new int[fromList.size()];
        	for (int i = 0; i < fromList.size(); i++) {
        		myFrom[i] = fromList.get(i);
        		myTo[i] = toList.get(i);
        	}
        } catch (final NoSuchElementException ex) {
            System.out.println("Input folder not found: " + ex.getMessage());
        } catch (final FileNotFoundException ex) {
            System.out.println("Input file not found: " + ex.getMessage());
        } catch (final NumberFormatException ex) {
        	System.out.println("Invalid input: " + line);
        	JOptionPane.showMessageDialog(null,
        			"Invalid input: " + ex.getMessage()
					, "Error", JOptionPane.ERROR_MESSAGE);
        }finally {
            if (scanner != null) {
                scanner.close();
            }
        }
	}
	
	/**
	 * this method keep calculating the page rank score vector
	 * until two of the vector before and after a multiplication are same
	 */
	private static void findPageRank() {
		myMatrix = new PageRankMatrix(myFrom, myTo);
		System.out.println("build matrix finish");
		
		if (myMatrix.getDimension() < outPageNUmber)
			throw new IllegalArgumentException(
					"total page number smaller than " + outPageNUmber);
		
		double[] last = myMatrix.getFirstVector();
		double[] current = myMatrix.multVector(myMatrix.getFirstVector());
		counter++;
		myRank = current;
		System.out.println(counter + " multiplication done");
		while (!testSame(last, current)) {
			last = current;
			current = myMatrix.multVector(last);
			counter++;
			System.out.println(counter + " multiplication done");
		}
		
		myRank = current;
	}
	
	/**
	 * this method get the output page rank score and page ID
	 */
	private static void getRank() {
		
		myOutRank = new double[outPageNUmber];
		myOutPage = new int[outPageNUmber];
		
		for (int i = 0; i < outPageNUmber; i++){
			double max = 0.0;
			int index = 0;
			for (int j = 0; j < myRank.length; j++) {
				if (myRank[j] > max) {
					max = myRank[j];
					index = j;
				}
			}
			myRank[index] = 0.0;
			myOutRank[i] = max;
			myOutPage[i] = index + 1;
		}
	}
	
	/**
	 * this method write the output information to a text file
	 */
	private static void output() {
		PrintWriter writer = null;
        try {
        	
        	JOptionPane.showMessageDialog(null, "finished!", "Message", JOptionPane.PLAIN_MESSAGE);
        	myFilePicker.resetChoosableFileFilters();
        	myFilePicker.setFileFilter(new FileNameExtensionFilter("Text format (*.txt)", "txt"));
        	int select = myFilePicker.showSaveDialog(null);
        	if (!(select == JFileChooser.APPROVE_OPTION))
        		System.exit(0);
        	File result = myFilePicker.getSelectedFile();
        	
            writer = new PrintWriter(new FileOutputStream(result.getAbsolutePath()));
            writer.println("Time used: " + (end - start) / 60000 + " min");
            writer.println("Dimension: " + myMatrix.getDimension());
            writer.println("Multiplication times: " + counter);
            for (int i = 0; i < outPageNUmber; i++)
            	writer.println("Page" + (i + 1) + ": #" + myOutPage[i]
            			+", Score: " + myOutRank[i]);
            
        } catch (final NoSuchElementException ex) {
            System.out.println("Output folder not found: " + ex.getMessage());
        } catch (final FileNotFoundException ex) {
            System.out.println("Output file not found: " + ex.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
	}
	
	/**
	 * this method calculate the highest user input numbers of
	 * page rank scores in the two vector are the same or not
	 * @param first the first vector
	 * @param second the second vector
	 * @return true if the page rank scores are the same, false otherwise
	 * @throws IllegalArgumentException if the length of two vectors are different
	 */
	private static boolean testSame(double[] first, double[] second) {
		if (first.length != second.length)
			throw new IllegalArgumentException(
					"the length of two vector must be same");
		
		double[] theFirst = first.clone();
		double[] theSecond = second.clone();
		
		for (int i = 0; i < outPageNUmber; i++){
			double firstMax = 0.0;
			double secondMax = 0.0;
			int firstIndex = 0;
			int secondIndex = 0;
			for (int j = 0; j < theFirst.length; j++) {
				if (theFirst[j] > firstMax) {
					firstMax = theFirst[j];
					firstIndex = j;
				}
				if (theSecond[j] > secondMax) {
					secondMax = theSecond[j];
					secondIndex = j;
				}
			}
			
			theFirst[firstIndex] = 0.0;
			theSecond[secondIndex] = 0.0;
			if (Math.abs(secondMax - firstMax) > DELTA)
				return false;
		}
		return true;
	}
}
