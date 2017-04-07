package codefights;

import java.util.ArrayList;
import java.util.BitSet;

public class codefights4 {
	static ArrayList<Integer> a;
	static ArrayList<Integer> b;
	public static void main(String args[]) {
		System.out.println(Hof(1));
		System.out.println(Hof(2));
		System.out.println(Hof(5));
	}
	static String Hof(int n) {
	    a = new ArrayList<Integer>();
	    b = new ArrayList<Integer>();
	    a.add(1);
	    a.add(3);
	    int i = 1;
	    int diff = 1;
	    while (a.size() < n)
	    {
	        while (a.contains(new Integer(i))  || b.contains(new Integer(i))) {
	            i++;
	            //System.out.println(i);
	        }
	        if (a.contains(new Integer(i))) b.add(new Integer(i));
	        else a.add(new Integer(i));
	    }
	    return a.get(n - 1).toString();
	}

}
