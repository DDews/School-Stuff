package codefights;

public class codefights3 {
	public static void main(String args[]) {
		System.out.println(maxBits(5));
	}
	static int maxBits(int n) {
	    String o = Integer.toString(n,2);
	    StringBuilder x = new StringBuilder(o.length());
	    int ones = o.length() - o.replace("1","").length();
	    int i;
	    for (i = 0; i < ones; i++) 
	        x.append("1");
	    for (i = 0; i < o.length() - ones; i++)
	        x.append("0");
	    return Integer.parseInt(x.toString(),2);
	}
	static Byte[] intToByteArray(int value) {
	    return new Byte[] {
	            new Byte((byte)(value >>> 24)),
	            new Byte((byte)(value >>> 16)),
	            new Byte((byte)(value >>> 8)),
	            new Byte((byte)(value))};
	}
	static int byteArrayToInt(Byte[] b) 
	{
	    return   b[3].byteValue() & 0xFF |
	            (b[2].byteValue() & 0xFF) << 8 |
	            (b[1].byteValue() & 0xFF) << 16 |
	            (b[0].byteValue() & 0xFF) << 24;
	}
}
