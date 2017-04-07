import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Sample DFA formal definitions in correct format from Sipser:
{q1,q2,q3},{0,1},q1,{q2},{{q1,0,q1},{q1,1,q2},{q2,0,q3},{q2,1,q2},{q3,0,q2},{q3,1,q2}}
{q0,q1,q2},{R,0,1,2},q0,{q0},{{q0,R,q0},{q0,0,q0},{q0,1,q1},{q0,2,q2},{q1,R,q0},{q1,0,q1},{q1,1,q2},{q1,2,q0},{q2,R,q0},{q2,0,q2},{q2,1,q0},{q2,2,q1}}
*/
public class DFAtoTM {
	public static String getUnary(int n) {
		StringBuilder a = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			a.append("0");
		}
		return a.toString();
	}

	public static String getAccepts(ArrayList<Integer> accepts, int numStates) {
		StringBuilder a = new StringBuilder(accepts.size());
		for (int i = 0; i < numStates; i++) {
			if (accepts.contains(i))
				a.append("1");
			else
				a.append("0");
		}
		return a.toString();
	}

	public static String getTransitions(HashMap<String, Integer> states,
			HashMap<String, ArrayList<Integer>> transitions) {
		StringBuilder a = new StringBuilder(transitions.size());
		for (int i = 0; i < states.size(); i++) {
			for (Map.Entry<String, Integer> entry : states.entrySet()) {
				String state = entry.getKey();
				Integer num = entry.getValue();
				if (num == i) {
					a.append("@");
					ArrayList<Integer> transition = transitions.get(state);
					for (int j = 0; j < transition.size(); j++) {
						a.append("%");
						a.append(getUnary(transition.get(j) + 1));
					}
				}
			}
		}
		return a.toString();
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		boolean cont = true;
		System.out.print("Enter a DFA to convert to tape: ");
		String dfa = in.nextLine();
		do {
			System.out.print("Enter test string:");
			String testString = in.nextLine();
			String exp = "^\\{(.*)\\},\\{(.*)\\},(.*),\\{(.*)\\},\\{(\\{.*\\})\\}$";
			Matcher m = Pattern.compile(exp).matcher(dfa);
			boolean flag = false;
			if (!m.find()) {
				flag = true;
			} else {
				for (int i = 0; i < m.groupCount(); i++) {
					if (m.group(i).equals("")) {
						flag = true;
						break;
					}
				}
			}
			if (flag) {
				System.out.println(
						"Error: not enough pieces for formal definition of DFA. Please give delimited by comma: set of states, set of alphabet, start state, set of accept states, set of transitions = {state in, symbol, state to}.");
			} else {
				String strStates[] = m.group(1).split(",");
				HashMap<String, Integer> states = new HashMap<String, Integer>();
				int i = 0;
				for (String state : strStates) {
					states.put(state, i++);
				}
				String strAlphabet[] = m.group(2).split(",");
				HashMap<String, Integer> alphabet = new HashMap<String, Integer>();
				i = 0;
				for (String symbol : strAlphabet) {
					alphabet.put(symbol, i++);
				}
				String start = m.group(3);
				String strAccepts[] = m.group(4).split(",");
				ArrayList<Integer> accepts = new ArrayList<Integer>();
				for (String accept : strAccepts) {
					accepts.add(states.get(accept));
				}
				String strTransitions = m.group(5);
				Matcher trans = Pattern.compile("\\{([a-zA-Z\\,0-9]*)*\\}").matcher(strTransitions);
				HashMap<String, ArrayList<Integer>> transitions = new HashMap<String, ArrayList<Integer>>();
				flag = false;
				for (i = 0; i < states.size() * alphabet.size(); i++) {
					if (!trans.find()) {
						System.err.println("Not enough state transitions.");
						flag = true;
						break;
					}
					String info[] = trans.group(0).substring(1, trans.group(0).length() - 1).split(",");
					if (info.length < 3) {
						flag = true;
						break;
					}
					ArrayList<Integer> temp = transitions.get(info[0]);
					if (temp == null) {
						temp = new ArrayList<Integer>();
						for (int k = 0; k < alphabet.size(); k++) {
							temp.add(k);
						}
					}
					temp.set(alphabet.get(info[1]), states.get(info[2]));
					transitions.put(info[0], temp);
				}
				String output = "";
				if (flag) {
					System.err.println("Not enough transtions! Should have a set of "
							+ (states.size() * alphabet.size()) + " sets.");
				} else {
					StringBuilder b = new StringBuilder();
					for (Character c : testString.toCharArray()) {
						b.append("@");
						b.append(getUnary(alphabet.get("" + c) + 1));
					}
					output = String.format("$%s#%s#%s#%s#%s*%s", getUnary(states.size()), getUnary(alphabet.size()),
							getUnary(states.get(start) + 1), getAccepts(accepts, states.size()),
							getTransitions(states, transitions), b);
				}
				System.out.println(output);
				System.out.print("Continue? (y/n): ");
			}
			cont = !flag && in.nextLine().toLowerCase().equals("y");
		} while (cont);
		in.close();
	}
}
