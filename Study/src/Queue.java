
public class Queue<T> {
	public Node start;
	public Queue() {
		start = null;
	}
	@Override
	public String toString() {
		StringBuilder r = new StringBuilder("[");
		Node m = start;
		while (m != null) {
			r.append(", ");
			r.append(m.getData());
			m = m.getNext();
		}
		r.append("]");
		if (r.length() > 2) r.delete(1, 3);
		return r.toString();
	}
	public void enqueue(T data) {
		if (start != null) {
			Node c = start;
			while (c.getNext() != null) {
				c = c.getNext();
			}
			c.setNext(new Node(data));
		}
		else start = new Node(data);
	}
	public T dequeue() {
		T temp = null;
		if (start != null) { temp = start.getData(); start = start.getNext(); }
		return temp;
	}
	public void moveToBack() {
		T temp = dequeue();
		if (temp != null) enqueue(temp);
	}
	private class Node {
		T data;
		Node next;
		public Node() {
			this(null);
		}
		public Node(T data) {
			this.data = data;
		}
		public Node getNext() {
			return next;
		}
		public void setNext(Node next) {
			this.next = next;
		}
		public void setData(T data) {
			this.data = data;
		}
		public T getData() {
			return data;
		}
	}
}
