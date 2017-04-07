package codefights;

public class Kata {
	public static long currentN;
	public static String Factorial(int n) {
		if (n < 0) return null;
		return Long.toString(fact(n));
	}
	
	public static int log2nlz(int bits) {
		if (bits == 0)
			return 0; // or throw exception
		return 31 - Integer.numberOfLeadingZeros(bits);
	}

	public static long fact(int n) {
		if (n < 0) {
			return -1;
		}

		if (n < 2)
			return 1L;

		long p = 1L;
		long r = 1L;
		currentN = 1L;

		int h = 0, shift = 0, high = 1;
		int log2n = log2nlz(n);

		while (h != n) {
			shift += h;
			h = n >> log2n--;
			int len = high;
			high = (h - 1) | 1;
			len = (high - len) / 2;

			if (len > 0) {
				p *= Product(len);
				r *= p;
			}
		}

		return r << shift;
	}

	private static long Product(int n) {
		int m = n / 2;
		if (m == 0)
			return (long)(currentN += 2);
		if (n == 2)
			return (currentN += 2) * (currentN += 2);
		return Product(n - m) * Product(m);
	}

}
