import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class CalcLang {
	private static Scanner in;
	public static Lexer lexer;
	public static Token head;
	private static ArrayList<String> variables;
	private static ArrayList<Double> variable;
	private static ArrayList<String> bifs;
	public static void main(String[] args) throws FileNotFoundException {
		variables = new ArrayList<String>();
		variable = new ArrayList<Double>();
		bifs = new ArrayList<String>();
		for (String bif : new String[] {"sin","cos","sqrt","abs","radians","degrees"}) {
			bifs.add(bif);
		}
		in = new Scanner(System.in);
		System.out.print("Enter filename: ");
		String filename = in.nextLine();
		lexer = new Lexer(filename);
		head = new Statements();
		execute(head);
	}
	public static double execute(Token token) {
		switch(token.type) {
			case STATEMENTS:
				if (token.children.size() > 1) {
					execute(token.children.get(0));
					return execute(token.children.get(1));
				}
				return execute(token.children.get(0));
			case STATEMENT:
				Token first = token.children.get(0);
				switch (first.type) {
					case VARIABLE:
						return setVar(first.string,execute(token.children.get(2)));
					case MESSAGE:
						System.out.print(token.children.get(1).string);
						return 0.0;
					case SHOW:
						System.out.print(execute(token.children.get(1)));
						return 0.0;
					case INPUT:
						System.out.print(token.children.get(1).string);
						return setVar(token.children.get(2).string,Double.parseDouble(in.nextLine()));
					case NEWLINE:
						System.out.println();
						return 0.0;
					default:
				}
				break;
			case EXPRESSION:
				if (token.children.size() > 1) {
					switch(token.children.get(1).type) {
						case PLUS:
							return execute(token.children.get(0)) + execute(token.children.get(2));
						case MINUS:
							return execute(token.children.get(0)) - execute(token.children.get(2));
						default:		
					}
				}
				return execute(token.children.get(0));
			case TERM:
				if (token.children.size() > 1) {
					switch(token.children.get(1).type) {
						case MULTIPLY:
							return execute(token.children.get(0)) * execute(token.children.get(2));
						case DIVIDE:
							return execute(token.children.get(0)) / execute(token.children.get(2));
						default:
					}
				}
				return execute(token.children.get(0));
			case FACTOR:
				switch(token.children.get(0).type) {
					case NUMBER:
						return token.children.get(0).value;
					case VARIABLE:			
						return getVar(token.children.get(0).string);
					case LPAREN:
						return execute(token.children.get(1));
					case MINUS:
						return -1 * execute(token.children.get(1));
					case BIFN:
						switch(token.children.get(0).string) {
						case "cos":
							return Math.cos(execute(token.children.get(2)));
						case "sin":
							return Math.sin(execute(token.children.get(2)));
						case "abs":
							return Math.abs(execute(token.children.get(2)));
						case "sqrt":
							return Math.sqrt(execute(token.children.get(2)));
						case "radians":
							return Math.toRadians(execute(token.children.get(2)));
						case "degrees":
							return Math.toDegrees(execute(token.children.get(2)));
						default:
							return execute(token.children.get(2));
						}
					default:
						break;
				}
				break;
			case VARIABLE:
				return getVar(token.string);
			case NUMBER:
				return token.value;
			default:
		}
		return 0.0;
	}
	public static double setVar(String var, double value) {
		if (variables.contains(var)) {
			variable.set(variables.indexOf(var),value);
		} else {
			variables.add(var);
			variable.add(value);
		}
		return value;
	}
	public static double getVar(String var) {
		if (variables.contains(var)) return variable.get(variables.indexOf(var));
		variables.add(var);
		variable.add(0.0);
		return 0.0;
	}
	public static Token getToken() {
		return lexer.getToken();
	}
	public static Token peekToken() {
		return lexer.peekToken();
	}
	public static void putBackToken(Token token) {
		lexer.putBackToken(token);
	}
}
