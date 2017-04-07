package codefights;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class codefights {
	static int[] a;
	static int[] b;
	static int[][] ab;
	static int[] c;
	public static int champion = Integer.MAX_VALUE;
	public static void main(String args[]) {
		a = new int[]{3, 2, 1};
		b = new int[]{1000, 2, 3, 1000, 1000, 1};
		c = new int[a.length];
		int out = closestSequence2(a,b);
		System.out.println(champion - 1);
	}
	static int closestSequence2(int[] a, int[] b) {
	    if (a.length == b.length) { return getDiff(a,b); }
	    ab = new int[a.length][b.length - a.length + 1];
	    /*for (int i = 0; i < a.length; i++)
	        for (int j = i; j < i + 1 + b.length - a.length; j++)
	            ab[i][j - i] = Math.abs(a[i] - b[j]);*/
	    getDunked(0,0,0,0);
	    return champion - 1;
	    /*
	    int v;
	    for (int i = 0; i < a.length; i++) {
	        for (int n = i; n < i + 1 + b.length - a.length; n++) {
	            if ((v = Math.abs(a[i] - b[n])) < ab[i][n - i]) ab[i][n - i]= v;
	        }
	    }
	    int k = 0, p = 0, u = 0;
	    getDunked(0,0,0);
	   for (int i = 0; i < a.length; i++) {
	        p = Integer.MAX_VALUE;
	        for (int n = i + u; n < i + 1 + b.length - a.length; n++) {
	            if (ab[i][n-i] < p) { p = ab[i][n-i]; if (n - i > u) u = n - i; }
	            System.out.print(ab[i][n - i] + " ");
	        }
	        k += p;
	        System.out.println("\n");
	    }
	    return champion;*/
	}
	static void getDunked(int i, int n, int passed, int k) {
		int cr = passed;
		if (ab[i][n] == 0) ab[i][n] = Math.abs(a[i] - b[i + n]) + 1;
	    passed += ab[i][n] - 1;
	    System.out.println(i + "," + n + " ... " + passed + " " + k);
	    if (k == 0 || passed + 1 < k) k = passed + 1;
	    if (i == a.length - 1 && k < champion) champion = k;
	    if (k != 0 && passed + 1 > k) { System.out.println("saved"); return;}
	    if (n + 1 < 1 + b.length - a.length) getDunked(i,n + 1,cr,k);
	    if (i + 1 < a.length) getDunked(i + 1,n,passed,0);
	    return;
	}
	void getDunked2(int i, int n, int passed, int[] c) {
	    passed += ab[i][n];
	    //System.out.println(i + "," + n + " ... " + passed + " " + Arrays.toString(c));
	    if (c[i] == 0 || passed + 1 < c[i]) c[i] = passed + 1;
	    if (i == a.length - 1 && c[i] < champion) champion = c[i];
	    if (c[i] != 0 && passed + 1 > c[i]) { return;}
	    int[] d = new int[c.length];
	    System.arraycopy(c, 0, d, 0, c.length);
	    if (n + 1 < 1 + b.length - a.length) getDunked2(i,n + 1,passed - ab[i][n],d);
	    if (i + 1 < a.length) getDunked2(i + 1,n,passed,d);
	    return;
	}
	static void getNum(int indexA, int indexB, int currentNum) {
	    if (ab[indexA][indexB - indexA] == -1) ab[indexA][indexB - indexA] = Math.abs(a[indexA] - b[indexB]);
	    int newNum = ab[indexA][indexB - indexA] + currentNum;
	    if (indexB < indexA + (b.length - a.length)) getNum(indexA,indexB + 1,currentNum);
	    if (newNum >= champion) {
	        return;
	    }
	    if (indexA == a.length - 1) {
	        if (newNum < champion) champion = newNum;
	    }
	    else if (indexB + 1 < b.length) getNum(indexA + 1,indexB + 1,newNum);
	    return;
	}	
	static int getDiff(int[] a, int[] b) {
	    int out = 0;
	    for (int i = 0; i < a.length; i++) {
	        out += Math.abs(a[i] - b[i]);
	    }
	    return out;
	}
}
