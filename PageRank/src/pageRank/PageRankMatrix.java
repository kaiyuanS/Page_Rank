package pageRank;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a simplified matrix that represents
 * a sparse matrix used to calculate page rank
 * @author Kaiyuan Shi
 * @version 1.0
 */
public class PageRankMatrix {
	
	/** the dampening value */
	private static final double DAMPENING = 0.15;
	
	/** the dimension of the matrix */
	private int myDimension = 0;
	/** a list of pages contain links */
	private int[] myFrom;
	/** a list of pages were linked */
	private int[] myTo;
	/** a list of the weight of links */
	private double[] myWeight;
	
	/** the dampening value */
	private double myDampeningValue;
	
	/**
	 * default constructor blocked
	 */
	private PageRankMatrix() {
		
	}
	
	/**
	 * the constructor of the page rank matrix
	 * @param aFrom a list of pages contain links
	 * @param aTo a list of pages were linked
	 * @throws IllegalArgumentException if the length of
	 *  two input arrays are different or equal to 0
	 */
	public PageRankMatrix(int[] aFrom, int[] aTo) {
		
		if (aFrom.length != aTo.length || aFrom.length == 0) {
			throw new IllegalArgumentException(
					"the length of two array should be the same,"
					+ "and can not be zero");
		}
		
		for (int i = 0; i < aFrom.length; i++) {
			if (aFrom[i] <= 0 || aTo[i] <= 0)
				throw new IllegalArgumentException(
						"the node number must be positive");
		}
		
		myFrom = aFrom;
		myTo = aTo;
		myWeight = new double[aFrom.length];
		
		//find biggest page ID as the dimasion
		for (int i = 0; i < aFrom.length; i++) {
			if (aFrom[i] > myDimension)
				myDimension = aFrom[i];
			if (aTo[i] > myDimension)
				myDimension = aTo[i];
		}
		
		
		System.out.println("got dimension: " + myDimension);
		calculateWeight();
		System.out.println("got weight");
		calculateGoogleMatrix();
		System.out.println("got Google matrix");
		
	}
	
	/**
	 * this method can get the first page rank vector
	 * @return the first page rank vector
	 */
	public double[] getFirstVector() {
		double[] ret = new double[myDimension];
		double temp = (double)1 / myDimension;
		for (int i = 0; i < myDimension; i++)
			ret[i] = temp;
		return ret;
	}

	/**
	 * this method returns a list of pages contain links
	 * @return a list of pages contain links
	 */
	public int[] getFrom() { 
		return myFrom;
	}
	
	/**
	 * this method returns a list of pages were linked
	 * @return a list of pages were linked
	 */
	public int[] getTo() {
		return myTo;
	}
	
	/**
	 * this method returns a list of the weight of links
	 * @return a list of the weight of links
	 */
	public double[] getWeight() {
		return myWeight;
	}
	
	/**
	 * this method returns the dimension of the matrix
	 * @return the dimension of the matrix
	 */
	public int getDimension() {
		return myDimension;
	}
	
	/**
	 * this method multiply the current matrix to a vector
	 * @param aVector the vector was multiplied
	 * @return the answer of the multiplication
	 * @throws IllegalArgumentException if the length of the vector
	 *  do not equals to the dimension of the matrix
	 */
	public double[] multVector(double[] aVector) {
		if (aVector.length != myDimension)
			throw new IllegalArgumentException(
					"the length of the vector should"
					+ " same as the dimension of the matrix");
		double[] ret = new double[myDimension];
		
		for (int i = 1; i <= myDimension; i++) {
			Set<Integer> nonZero = new HashSet<Integer>();
			double sum = 0.0;
			for (int j = 0; j < myTo.length; j++) {
				if (myTo[j] == i) {
					sum += myWeight[j] * aVector[myFrom[j] - 1];
					nonZero.add(myFrom[j] - 1);
				}
			}
			
			for (int j = 0; j < myDimension; j++) {
				if (!nonZero.contains(j))
					sum += myDampeningValue * aVector[j];
			}
			
			ret[i - 1] = sum;
		}
		
		return ret;
	}
	
	/**
	 * this method returns a copy of the current matrix
	 */
	@Override
	public PageRankMatrix clone() {
		PageRankMatrix ret = new PageRankMatrix();
		ret.myDimension = this.myDimension;
		ret.myFrom = this.myFrom.clone();
		ret.myTo = this.myTo.clone();
		ret.myWeight = this.myWeight.clone();
		return ret;
		
	}
	
	/**
	 * this method calculate the weight of every edges
	 */
	private void calculateWeight() {
		
		for (int i = 0; i < myDimension; i++) {
			int counter = 0;
			for (int j = 0; j < myFrom.length; j++) {
				if (myFrom[j] == i + 1)
					counter++;
			}
			
			if (counter != 0) {
				for (int j = 0; j < myFrom.length; j++) {
					if (myFrom[j] == i + 1)
						myWeight[j] = (double)1 / counter;
				}
			}
		}
	}
	
	/**
	 * this method adding the dampening value to the edges
	 */
	private void calculateGoogleMatrix() {
		myDampeningValue = DAMPENING / myDimension;
		for (int i = 0; i < myWeight.length; i++)
			myWeight[i] = (1 - DAMPENING) * myWeight[i] + myDampeningValue;
	}
}
