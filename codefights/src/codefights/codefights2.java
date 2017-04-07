package codefights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class codefights2 {
	ArrayList<HashMap<Integer,ArrayList<Integer>>> table = new ArrayList<HashMap<Integer,ArrayList<Integer>>>();
	int closestSequence2(int[] a, int[] b) {
	    if (a.length == b.length) return getDiff(a,b);
	    for (int i = 0; i < a.length; i++) {
	        table.add(getDiffs(i,a[i],b,a.length));
	    }

	    return getLowestDiff(0,-1,a.length,0);
	}
	int getLowestDiff(int indexA, int indexB, int length, int currentLowest) {
	    int lowest = Integer.MAX_VALUE;
	    HashMap<Integer,ArrayList<Integer>> map = table.get(indexA);
	    for (Map.Entry<Integer,ArrayList<Integer>> entry : map.entrySet()) {

	        int diff = entry.getKey();
	        ArrayList<Integer> indexes = entry.getValue();

	        for (Integer index : indexes) {

	            if (index >= indexB) {

	                int calculated = currentLowest + diff;
	                if (indexA+1 < length) {
	                    calculated = getLowestDiff(indexA + 1, index+1, length, calculated);
	                }
	                if (calculated < lowest) {
	                    lowest = calculated;
	                }

	            }
	        } 
	    }
	    return lowest;
	}
	HashMap<Integer,ArrayList<Integer>> getDiffs(int index, int k, int[] b, int length) {
	    HashMap<Integer,ArrayList<Integer>> output = null;
	    if (table.size() > index) output = table.get(index);
	    if (output == null) output = new HashMap<Integer,ArrayList<Integer>>();
	    for (int i = index; i <= b.length - length + index; i++) {
	        int diff = Math.abs(k - b[i]);
	        ArrayList<Integer> out = null;
	        out = output.get(diff);
	        if (out == null) out = new ArrayList<Integer>();
	        out.add(i);
	        output.put(diff,out);
	    }
	    return output;
	}
	int getDiff(int[] a, int[] b) {
	    int out = 0;
	    for (int i = 0; i < a.length; i++) {
	        out += Math.abs(a[i] - b[i]);
	    }
	    return out;
	}
}
