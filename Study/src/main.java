import java.io.FileInputStream;

public class main {
	public static void main(String[] args) {
		Queue<String> q = new Queue<String>();
		System.out.println(q);
		q.moveToBack();
		System.out.println(q);
		q.enqueue("one");
		q.enqueue("two");
		q.enqueue("three");
		q.enqueue("four");
		q.enqueue("five");
		q.enqueue("six");
		System.out.println(q);
		q.moveToBack();
		System.out.println(q);
	}
}
