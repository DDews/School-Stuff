package codefights;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class codefights5 {

	public static void main (String args[]) {
		System.out.println(Arrays.toString(GetDuplicates(new int[]{1, 2, 1, 1, 3, 2, 4, 6, 5})));
	}
	static Object[] GetDuplicates(int[] n) {
	    HashSet<Integer> o = new HashSet<Integer>();
	    HashSet<Integer> r = new HashSet<Integer>();
	    ArrayList<Integer> k = new ArrayList<Integer>();
	    for (int a : n) {
	        r.add(a);
	        if (!o.add(a)) r.remove(a);
	    }
	    for (int a : n) {
	        if (!r.contains(a) && !k.contains(a)) k.add(a);
	    }
	    return k.toArray();
	}
}
