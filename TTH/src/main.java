import java.util.Arrays;

public class main extends printNum {
	int[] row1;
	int[] row2;
	int[] row3;
	int[] row4;
	int[] cipher;
	public static void main(String[] args) {
		main obj = new main();
		System.out.println("toString: " + obj.toString());
		obj.run();
	}
	public String toString() {
		String output = "";
		run();
		for (int a = 0; a < 4; a++) {
			output += (char)(cipher[a] + (int)'a');
		}
		return output;
	}
	public int min(int a, int b) {
		if (a <= b) return a;
		return b;
	}
	public void run() {
		String text = "I leave twenty million dollars to my friendly cousin Bill";
		text = text.replaceAll("\\s",""); //Remove spaces
		row1 = new int[4];
		row2 = new int[4];
		row3 = new int[4];
		row4 = new int[4];
		cipher = new int[] {0,0,0,0}; //beginning cipher
		int a = 0;
		String output = "";
		do {
			loadArrays(text.substring(a,min(a+16,text.length()))); //read in first 16 characters
			getSum();
			//printSum();
			rotateArrays();
			getSum();
			//printSum();
			a += 16;
		} while (a < text.length());
		printSum();
	}
	public void loadArrays(String input) {
		while (input.length() < 16) {
			input += "a";
		}
		input = input.toLowerCase(); //convert to lowercase
		for (int a = 0; a < 4; a++) {
			row1[a] = (int)input.charAt(a) - (int)'a';
			row2[a] = (int)input.charAt(a + 4) - (int)'a';
			row3[a] = (int)input.charAt(a + 8) - (int)'a';
			row4[a] = (int)input.charAt(a + 12) - (int)'a';
		}
		/*System.out.println("----");
		System.out.println(input.substring(0, 4));
		System.out.println(input.substring(4,8));
		System.out.println(input.substring(8,12));
		System.out.println(input.substring(12,16));
		System.out.println("----");*/
	}
	public void getSum() {
		int[] result = new int[4];
		for (int a = 0; a < 4; a++) {
			result[a] = (row1[a] + row2[a] + row3[a] + row4[a]) % 26;
		}
		for (int a = 0; a < 4; a++) {
			cipher[a] = (cipher[a] + result[a]) % 26;
		}
	}
	public void rotateArrays() {
		row1 = rotate(row1,1);
		row2 = rotate(row2,2);
		row3 = rotate(row3,3);
		row4 = reverse(row4);
	}
	public int[] reverse(int[] nums) {
		int[] result = new int [nums.length];
		int j = 0;
		for (int a = nums.length - 1; a >= 0; a--) {
			result[j++] = nums[a];
		}
		return result;
	}
	public int[] rotate(int[] nums, int k) {
	    if(k > nums.length) 
	        k=k%nums.length;
	 
	    int[] result = new int[nums.length];
	    
	    for(int i=0; i < k; i++){
	        result[nums.length-k+i] = nums[i];
	    }
	    
	    int j = 0;
	    
	    for(int i=k; i<nums.length; i++){
	        result[j] = nums[i];
	        j++;
	    }
	 
	    return result;
	}
	public void printSum() {
		String output = "";
		for (int a = 0; a < 4; a++) {
			output += (char)(cipher[a] + (int)'a');
		}
		System.out.println(output);
	}
}
