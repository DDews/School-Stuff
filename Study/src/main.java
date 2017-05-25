import java.io.FileInputStream;
import java.util.Arrays;

public class main {
	public static void main(String[] args) {

		/*
		 * CHANGE THIS TO MATCH THE PROBLEM:
		 */
		int nums[][] = new int[9][6];

		/*
		 * IGNORE THIS
		 */
		try {

			/*
			 * Your answer below
			 */
			for (int i = 0; i < 9; i++) {
				for (int k = 0; k < 6; k++) {
					nums[i][k] = 1;
				}
			}

			// DO NOT EDIT ANYTHING BELOW

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
					"One or more of your for loops are wrong, they are going past their maximum length.");
			e.printStackTrace();
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		for (int i = 0; i < nums.length; i++) {
			System.out.println(Arrays.toString(nums[i]));
		}
	}
}
