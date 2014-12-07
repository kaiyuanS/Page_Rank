package pageRank;

import java.util.HashSet;
import java.util.Set;



public class PageRankMatrix {
	
	private static final double DAMPENING = 0.85;
	
	private int myDimension = 0;
	private int[] myFrom;
	private int[] myTo;
	private double[] myWeight;
	private double myDampeningValue;
	
	private PageRankMatrix() {
		
	}
	
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
		
		for (int i = 0; i < aFrom.length; i++) {
			if (aFrom[i] > myDimension)
				myDimension = aFrom[i];
			else if (aTo[i] > myDimension)
				myDimension = aTo[i];
		}
		System.out.println("got dimension: " + myDimension);
		calculateWeight();
		System.out.println("got weight");
		calculateGoogleMatrix();
		System.out.println("got Google matrix");
		
	}
	
	/*public double[] multVector(double[] aVector) {
		if (aVector.length != myDimension)
			throw new IllegalArgumentException(
					"the length of the vector should"
					+ " same as the dimension of the matrix");
		double[] ret = new double[myDimension];
		for (int i = 1; i <= myDimension; i++) {
			double sum = 0.0;
			double tempSum = 0.0;
			for (int j = 1; j <= myDimension; j++) {
				boolean isZero = true;
				for (int k = 0; k < myTo.length; k++) {
					if (myTo[k] == i && myFrom[k] == j) {
						sum += myWeight[k] * aVector[j - 1];
						isZero = false;
						break;
					}
				}
				if (isZero)
					tempSum += aVector[j - 1];
			}
			System.out.println("line " + (i + 1) + " done");	
			ret[i - 1] = sum + myDampeningValue * tempSum;
		}
		
		return ret;
	}*/
	
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
			//System.out.println("line " + i + "done");
		}
		
		return ret;
	}
	
	public double[] getFirstVector() {
		double[] ret = new double[myDimension];
		double temp = (double)1 / myDimension;
		for (int i = 0; i < myDimension; i++)
			ret[i] = temp;
		return ret;
	}

	public int[] getFrom() { 
		return myFrom;
	}
	
	public int[] getTo() {
		return myTo;
	}
	
	public double[] getWeight() {
		return myWeight;
	}
	
	public int getDimension() {
		return myDimension;
	}
	
	@Override
	public PageRankMatrix clone() {
		PageRankMatrix ret = new PageRankMatrix();
		ret.myDimension = this.myDimension;
		ret.myFrom = this.myFrom.clone();
		ret.myTo = this.myTo.clone();
		ret.myWeight = this.myWeight.clone();
		return ret;
		
	}
	
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
	
	private void calculateGoogleMatrix() {
		myDampeningValue = (1 - DAMPENING) / myDimension;
		for (int i = 0; i < myWeight.length; i++)
			myWeight[i] = DAMPENING * myWeight[i] + myDampeningValue;
	}
}
