package pageRank;

/**
 * This is a simple main of PageRankMatrix class
 * @author Kaiyuan Shi
 * @version 1.0
 */
public class SampleMain {

	/**
	 * Main method
	 * @param args command line argument
	 */
	public static void main(String[] args) {
		
		//a list of pages contain links
		int[] from = {1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5};
		
		//a list of pages were linked
		int[] to = {2, 3, 4, 5, 2, 4, 5, 2, 3, 5, 2, 3, 4, 6};
		
		//the page rank matrix
		PageRankMatrix matrix = new PageRankMatrix(from, to);
		
		//test attributes
		int[] testFrom = matrix.getFrom();
		int[] testTo = matrix.getTo();
		double[] testWeight = matrix.getWeight();
		int testDimension = matrix.getDimension();
		
		//get length
		System.out.println("length test:");
		System.out.println("From: " + testFrom.length
				+ ", To: " + testTo.length
				+ ", Weight: " + testWeight.length
				+ ", Dimension: " + testDimension);
		
		//get weights
		System.out.println("weight test:");
		for (int i = 0; i < testFrom.length; i++) {
			System.out.println(testFrom[i] + ", " + testTo[i] + ", " + testWeight[i]);
		}
		
		//get first
		double[] first = matrix.getFirstVector();
		System.out.println("first vector test:");
		for (int i = 0; i < first.length; i++)
			System.out.println(first[i]);
		
		//get new vector
		double[] current = matrix.multVector(first);
		System.out.println("multiplication test:");
		for (int i = 0; i < current.length; i++)
			System.out.println(current[i]);
		
	}

}
