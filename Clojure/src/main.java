import java.io.File;
import java.util.Scanner;

public class main {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		String line;
		System.out.print("Path to clj script (Enter to exit): ");
		while (!(line = in.nextLine()).equals("")) {
			try {
				if (new File(line).exists()) {
					clojure.main.main(new String[] {"-i",line});
				} else {
					System.err.println("File not found.");
				}
				System.out.print("Path to clj script (Enter to exit): ");
			} catch (Exception e) {
				System.err.println(e.toString());
				e.printStackTrace();
			}
		}
	}
}
