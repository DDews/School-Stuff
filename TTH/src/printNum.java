import java.util.Scanner;

public class printNum {
	public static void main(String[] args) {
		printNum obj = new printNum();
		obj.run();
	}
	public void run() {
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		String n;
		do {
			System.out.println("Enter a char: ");
			n = reader.nextLine();
			System.out.println((int)n.charAt(0) - (int)'a');
		} while (!n.equals("stop"));
	}
}
