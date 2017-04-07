import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import bsh.EvalError;

public class JavaMath {
	public static Random rand = new Random();
	public static String generateMathQuestion() {
		int numVars = rand.nextInt(3) + 3;
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < numVars; i++) {
			int type = rand.nextInt(2);
			switch (type) {
				case 0:
					out.append(Math.round(rand.nextDouble() * 100.0));
					break;
				case 1:
				default:
					out.append(rand.nextInt(100));
					break;	
			}
			out.append(" ");
			type = rand.nextInt(5);
			switch(type) {
				case 0:
					out.append("*");
					break;
				case 1:
					out.append("/");
					break;
				case 2:
					out.append("%");
					break;
				case 3:
					out.append("-");
					break;
				case 4:
				default:
					out.append("+");
			}
			out.append(" ");
		}
		int type = rand.nextInt(2);
		switch (type) {
			case 0:
				out.append(Math.round(rand.nextDouble() * 100.0) + ".0");
				break;
			case 1:
			default:
				out.append(rand.nextInt(100));
				break;	
		}
		return out.toString();
	}
	public static void main(String[] args) throws EvalError {
		System.out.println(new File("").getAbsolutePath());
		ScriptEngineManager manager = new ScriptEngineManager();
		//System.out.println("Engine: " + engine.getFactory().getEngineName());
		//System.out.println((85 % 10 / 2.0 + 4));
		double c = 3.0;
		boolean C = true, D = true, E = false, F = false;
		System.out.println((! (E || D) && C));
		System.out.println((85 % 10 / 2.0 + 4));
		Scanner in = new Scanner(System.in);
		boolean cont = true;
		do {
			String test = generateMathQuestion();
			String answer = JOptionPane.showInputDialog(new JFrame("Answer java evaluation of this math expression:"), test);
			String correctOrNot = "Correct!";
			try {
				Object a = new bsh.Interpreter().eval(test);
				double ans;
				if (a.getClass().getSimpleName().equals("Integer")) ans = ((Integer)a).doubleValue();
				else ans = (Double)a;
				if (Math.abs(ans - Double.parseDouble(answer)) < 0.1) {
					// bla
				} else {
					correctOrNot = "Incorrect.";
				}
			} catch (NumberFormatException e) {
				System.out.println("Unable to parse number from " + answer);
			}
			int choice = JOptionPane.showOptionDialog(null, 
					test + "\n" + correctOrNot + " Answer: " + new bsh.Interpreter().eval(test) + "\nContinue?", 
				      "Continue?", 
				      JOptionPane.YES_NO_OPTION, 
				      JOptionPane.QUESTION_MESSAGE, 
				      null, null, null);
			cont = choice == JOptionPane.YES_OPTION;
		} while(cont);
	}
}
