package pageRank;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PageRakeMain {
	private static final double DELTA = 0.001;
	private static int outPageNUmber = 0;
	private static int[] myFrom;
	private static int[] myTo;
	private static PageRankMatrix myMatrix;
	private static double[] myRank;
	private static double[] myOutRank;
	private static int[] myOutPage;
	
	private static JFileChooser myFilePicker;
	
	private static int counter = 1;
	private static long start = 0;
	private static long end = 0;
	
	private static final JFrame frame = new JFrame("Page Rank");

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
			
			start = System.currentTimeMillis();
			showFrame();
			readInput();
			System.out.println("finish reading file");
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
	private static void showFrame() {
		
		JLabel label = new JLabel("Calculating...");
		label.setPreferredSize(new Dimension(200, 30));
		frame.add(label);
		frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
		
	}
	private static void readInput() {
		List<Integer> fromList = new ArrayList<Integer>();
		List<Integer> toList = new ArrayList<Integer>();
		String line = null;
		Scanner scanner = null;
        try {
            //scanner = new Scanner(new FileInputStream("Adjacency Matrix.txt"));
        	
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
        }finally {
            if (scanner != null) {
                scanner.close();
            }
        }
	}
	private static void findPageRank() {
		myMatrix = new PageRankMatrix(myFrom, myTo);
		System.out.println("build matrix finish");
		
		if (myMatrix.getDimension() < outPageNUmber)
			throw new IllegalArgumentException(
					"total page number smaller than " + outPageNUmber);
		
		double[] last = myMatrix.getFirstVector();
		double[] current = myMatrix.multVector(myMatrix.getFirstVector());
		
		System.out.println(counter + " multiplication done");
		while (!testSame(last, current)) {
			//System.out.println("mult time: " + counter);
			counter++;
			last = current;
			current = myMatrix.multVector(last);
			System.out.println(counter + " multiplication done");
		}
		
		myRank = current;
	}
	
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
	
	private static boolean testSame(double[] aLast, double[] aCurrent) {
		if (aLast.length != aCurrent.length)
			throw new IllegalArgumentException(
					"the length of two vector must be same");
		
		double[] last = aLast.clone();
		double[] current = aCurrent.clone();
		
		for (int i = 0; i < outPageNUmber; i++){
			double lastMax = 0.0;
			double currentMax = 0.0;
			int lastIndex = 0;
			int currentIndex = 0;
			for (int j = 0; j < last.length; j++) {
				if (last[j] > lastMax) {
					lastMax = last[j];
					lastIndex = j;
				}
				if (current[j] > currentMax) {
					currentMax = current[j];
					currentIndex = j;
				}
			}
			
			last[lastIndex] = 0.0;
			current[currentIndex] = 0.0;
			if (Math.abs(currentMax - lastMax) > DELTA)
				return false;
		}
		return true;
	}
}
