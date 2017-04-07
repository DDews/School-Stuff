import java.io.*;
import java.util.*;

public class VPL {
	static final int max = 10000;
	static int[] mem = new int[max];
	static int ip, bp, sp, rv, hp, numPassed, gp, codeEnd, numInstructions;

	static String fileName;

	public static void main(String[] args) throws Exception {
		BufferedReader keys = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("enter name of file containing VPLstart program: ");
		fileName = keys.readLine();

		// load the program into the front part of
		// memory
		BufferedReader input = new BufferedReader(new FileReader(fileName));
		String line;
		StringTokenizer st;
		int opcode = 0;

		ArrayList<IntPair> labels, holes;
		labels = new ArrayList<IntPair>();
		holes = new ArrayList<IntPair>();
		int label = 0;

		int k = 0;
		do {
			line = input.readLine();
			//System.out.println("parsing line [" + line + "]");
			if (line != null) {// extract any tokens
				st = new StringTokenizer(line);
				if (st.countTokens() > 0) {// have a token, so must be an
											// instruction (as opposed to empty
											// line)

					try {
						opcode = Integer.parseInt(st.nextToken());
					} catch (Exception e) {
						System.err.println("There was an error: " + e);
						e.printStackTrace();
					}
					// load the instruction into memory:

					if (opcode == labelCode) {// note index that comes where
												// label would go
						try {
							label = Integer.parseInt(st.nextToken());
						} catch (Exception e) {
							System.err.println("There was an error: " + e);
							e.printStackTrace();
						}
						labels.add(new IntPair(label, k));
					} else {// opcode actually gets stored
						mem[k] = opcode;
						++k;

						if (opcode == callCode || opcode == jumpCode || opcode == condJumpCode) {// note
																									// the
																									// hole
																									// immediately
																									// after
																									// the
																									// opcode
																									// to
																									// be
																									// filled
																									// in
																									// later
							try {
								label = Integer.parseInt(st.nextToken());
							} catch (Exception e) {
								System.err.println("There was an error: " + e);
								e.printStackTrace();
							}
							mem[k] = label;
							holes.add(new IntPair(k, label));
							++k;
						}

						// load correct number of arguments (following label, if
						// any):
						for (int j = 0; j < numArgs(opcode); ++j) {
							try {
								mem[k] = Integer.parseInt(st.nextToken());
							} catch (Exception e) {
								System.err.println("There was an error: " + e);
								e.printStackTrace();
							}
							++k;
						}

					} // not a label

				} // have a token, so must be an instruction
			} // have a line
		} while (line != null);

		// System.out.println("after first scan:");
		// showMem( 0, k-1 );

		// fill in all the holes:
		int index;
		for (int m = 0; m < holes.size(); ++m) {
			label = holes.get(m).second;
			index = -1;
			for (int n = 0; n < labels.size(); ++n)
				if (labels.get(n).first == label)
					index = labels.get(n).second;
			mem[holes.get(m).first] = index;
		}

		//System.out.println("after replacing labels:");
		//showMem(0, k - 1);

		// initialize registers:
		bp = k;
		sp = k + 2;
		ip = 0;
		rv = -1;
		hp = max;
		numPassed = 0;

		codeEnd = bp - 1;
		
		numInstructions = 1;

		//System.out.println("Code is ");
		//showMem(0, codeEnd);

		gp = codeEnd + 1;

		// your code goes here!

		int L = 0;
		Scanner in = new Scanner(System.in);
		boolean cont = true;
		//printMem();
		do {

			int op = mem[ip];
			int a = mem[ip + 1];
			int b = mem[ip + 2];
			int c = mem[ip + 3];
			switch (op) {
			case 2: // call function at label a
				mem[sp] = bp; // set cell 0 to previous bp for returning from
								// call
				mem[sp + 1] = ip + 2; // set cell 1 to current ip + 1 for
										// returning
				bp = sp; // set new bp
				sp += 2 + L; // set new sp. add number of parameters if they
								// exist
				ip = a;
				L = 0; // we are not passing any more variables to a function
						// call as parameters
				break;
			case 3: // pass a onto the stack
				mem[sp + 2 + L] = getVar(a); // set the new parameter to what is
				ip += 2;
				L++;
				break;
			case 4: // initialize local a variables
				sp += a;
				ip += 2;
				break;
			case 5: // return
				rv = getVar(a); // set return value
				sp = bp; // return to previous sp
				ip = mem[bp + 1]; // set ip to where it was after it called the
									// function we're in
				bp = mem[bp]; // set bp to old bp
				break;
			case 6:
				mem[getCell(a)] = rv;
				ip += 2;
				break;
			case 7:
				ip = a;
				break;
			case 8:
				if (getVar(b) != 0) {
					ip = a;
				} else
					ip += 3;
				break;
			case 9:
				mem[getCell(a)] = getVar(b) + getVar(c);
				ip += 4;
				break;
			case 10:
				mem[getCell(a)] = getVar(b) - getVar(c);
				ip += 4;
				break;
			case 11:
				mem[getCell(a)] = getVar(b) * getVar(c);
				ip += 4;
				break;
			case 12:
				mem[getCell(a)] = getVar(b) / getVar(c);
				ip += 4;
				break;
			case 13:
				mem[getCell(a)] = getVar(b) % getVar(c);
				ip += 4;
				break;
			case 14:
				mem[getCell(a)] = getVar(b) == getVar(c) ? 1 : 0;
				ip += 4;
				break;
			case 15:
				mem[getCell(a)] = getVar(b) != getVar(c) ? 1 : 0;
				ip += 4;
				break;
			case 16:
				mem[getCell(a)] = getVar(b) < getVar(c) ? 1 : 0;
				ip += 4;
				break;
			case 17:
				mem[getCell(a)] = getVar(b) <= getVar(c) ? 1 : 0;
				ip += 4;
				break;
			case 18:
				mem[getCell(a)] = getVar(b) != 0 && getVar(c) != 0 ? 1 : 0;
				ip += 4;
				break;
			case 19:
				mem[getCell(a)] = getVar(b) != 0 || getVar(c) != 0 ? 1 : 0;
				ip += 4;
				break;
			case 20:
				mem[getCell(a)] = getVar(b) == 0 ? 1 : 0;
				ip += 3;
				break;
			case 21:
				mem[getCell(a)] = getVar(b) * -1;
				ip += 3;
				break;
			case 22:
				mem[getCell(a)] = b;
				ip += 3;
				break;
			case 23:
				mem[getCell(a)] = getVar(b);
				ip += 3;
				break;
			case 24:
				mem[getCell(a)] = getHeap(getVar(b) + getVar(c));
				ip += 4;
				break;
			case 25:
				mem[getHeapCell(getVar(a) + getVar(b))] = getVar(c);
				ip += 4;
				break;
			case 26:
				System.exit(0);
			case 27:
				System.out.print("?");
				try {
					mem[getCell(a)] = in.nextInt();
				} catch (Exception e) {
					System.err.println("There was an error: " + e.toString());
					e.printStackTrace();
				}
				ip += 2;
				break;
			case 28:
				System.out.print(getVar(a));
				ip += 2;
				break;
			case 29:
				System.out.println();
				ip += 1;
				break;
			case 30:
				System.out.print(getChar(getVar(a)));
				ip += 2;
				break;
			case 31:
				hp -= getVar(b);
				mem[getCell(a)] = hp;
				ip += 3;
				break;
			case 32:
				sp += a;
				bp += a;
				ip += 2;
				break;
			case 33:
				mem[gp + a] = getVar(b);
				ip += 3;
				break;
			case 34:
				mem[getCell(a)] = mem[gp + b];
				ip += 3;
				break;
			default:
				ip += 1;
			}
			numInstructions++;
			//printMem();
		} while (cont);// end of the fetch-execute loop

	}// main

	// use symbolic names for all opcodes:

	// op to produce comment on a line by itself
	private static final int noopCode = 0;

	// ops involved with registers
	private static final int labelCode = 1;
	private static final int callCode = 2;
	private static final int passCode = 3;
	private static final int allocCode = 4;
	private static final int returnCode = 5; // return a means "return and put
	// copy of value stored in cell a in register rv
	private static final int getRetvalCode = 6;// op a means "copy rv into cell
												// a"
	private static final int jumpCode = 7;
	private static final int condJumpCode = 8;

	// arithmetic ops
	private static final int addCode = 9;
	private static final int subCode = 10;
	private static final int multCode = 11;
	private static final int divCode = 12;
	private static final int remCode = 13;
	private static final int equalCode = 14;
	private static final int notEqualCode = 15;
	private static final int lessCode = 16;
	private static final int lessEqualCode = 17;
	private static final int andCode = 18;
	private static final int orCode = 19;
	private static final int notCode = 20;
	private static final int oppCode = 21;

	// ops involving transfer of data
	private static final int litCode = 22; // litCode a b means "cell a gets b"
	private static final int copyCode = 23;// copy a b means "cell a gets cell
											// b"
	private static final int getCode = 24; // op a b means "cell a gets
											// contents of cell whose
											// index is stored in b"
	private static final int putCode = 25; // op a b means "put contents
	// of cell b in cell whose offset is stored in cell a"

	// system-level ops:
	private static final int haltCode = 26;
	private static final int inputCode = 27;
	private static final int outputCode = 28;
	private static final int newlineCode = 29;
	private static final int symbolCode = 30;
	private static final int newCode = 31;

	// global variable ops:
	private static final int allocGlobalCode = 32;
	private static final int toGlobalCode = 33;
	private static final int fromGlobalCode = 34;

	// debug ops:
	private static final int debugCode = 35;

	private static char getChar(int val) {
		return (char)val;
	}
	private static int getHeapCell(int index) {
		if (index <= sp) throw new IllegalArgumentException("Stack-heap collision at vpl instruction: " + numInstructions);
		else if (index >= max) throw new ArrayIndexOutOfBoundsException(index);
		return index;
	}

	// grab the heap var at index of mem
	private static int getHeap(int index) {
		if (index >= max || index <= sp)
			throw new IllegalArgumentException("Out of bounds index on heap (heap size " + hp + "): " + index + " at VPL instruction: " + numInstructions);
		return mem[index];
	}

	// return contents of variable at offset on stack
	private static int getVar(int offset) {
		if (getCell(offset) >= hp) throw new IllegalArgumentException("Stack-heap collision at vpl instruction: " + numInstructions);
		return mem[getCell(offset)];
	}

	// return mem index of var at offset
	private static int getCell(int offset) {
		if (bp + 2 + offset >= hp) throw new IllegalArgumentException("Stack-heap collision on VPL instruction: " + numInstructions);
		return bp + 2 + offset;
	}

	// return the number of arguments after the opcode,
	// except ops that have a label return number of arguments
	// after the label, which always comes immediately after
	// the opcode
	private static int numArgs( int opcode )
	  {
	    // highlight specially behaving operations
	    if( opcode == labelCode ) return 1;  // not used
	    else if( opcode == jumpCode ) return 0;  // jump label
	    else if( opcode == condJumpCode ) return 1;  // condJump label expr
	    else if( opcode == callCode ) return 0;  // call label

	    // for all other ops, lump by count:

	    else if( opcode==noopCode ||
	             opcode==haltCode ||
	             opcode==newlineCode ||
	             opcode==debugCode
	           ) 
	      return 0;  // op

	    else if( opcode==passCode || opcode==allocCode || 
	             opcode==returnCode || opcode==getRetvalCode || 
	             opcode==inputCode || 
	             opcode==outputCode || opcode==symbolCode ||
	             opcode==allocGlobalCode
	           )  
	      return 1;  // op arg1

	    else if( opcode==notCode || opcode==oppCode || 
	             opcode==litCode || opcode==copyCode || opcode==newCode ||
	             opcode==toGlobalCode || opcode==fromGlobalCode

	           ) 
	      return 2;  // op arg1 arg2

	    else if( opcode==addCode ||  opcode==subCode || opcode==multCode ||
	             opcode==divCode ||  opcode==remCode || opcode==equalCode ||
	             opcode==notEqualCode ||  opcode==lessCode || 
	             opcode==lessEqualCode || opcode==andCode ||
	             opcode==orCode || opcode==getCode || opcode==putCode
	           )
	      return 3;
	   
	    else
	    {
	      System.out.println("Fatal error: unknown opcode [" + opcode + "]" );
	      System.exit(1);
	      return -1;
	    }

	  }// numArgs
	private static void showMem(int a, int b) {
		for (int k = a; k <= b; ++k) {
			System.out.println(k + ": " + mem[k]);
		}
	}// showMem
	
	private static void printMem() {
		System.out.println();
		System.out.print("Current opcode: " + mem[ip] + " instruction #:" + numInstructions + " gp: " + gp + " ip: " + ip + " bp: " + bp + " sp: " + sp + " rv: " + rv + " hp: " + hp + "[");
		for (int i = 0; i <= codeEnd; i++) {
			System.out.print(mem[i] + ", ");
		}
		System.out.print("||");
		for (int i = codeEnd + 1; i < sp; i++) {
			System.out.print(mem[i] + ", ");
		}
		System.out.print("||");
		for (int i = hp; i < max; i++) {
			System.out.print(mem[i] + ", ");
		}
		System.out.println("]");
	}

}// VPLstart