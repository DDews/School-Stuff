/**
 * Created by DJ on 1/24/2017.
 */

import java.io.*;
import java.util.*;

public class VPL_Clean{
    static int debug = 2; //1 or 2
    static final int max = 10000;
    static int[] mem = new int[max];
    static int ip, bp, sp, rv, hp, numPassed, gp, codeEnd;

    static String fileName;

    public static void main(String[] args) throws Exception
    {
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
            System.out.println("parsing line [" + line + "]");
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

        System.out.println("after replacing labels:");
        showMem(0, k - 1);

        // initialize registers:
        bp = k;
        sp = k + 2;
        ip = 0;
        rv = -1;
        hp = max;
        numPassed = 0;

        codeEnd = bp - 1;

        System.out.println("Code is ");
        showMem(0, codeEnd);

        gp = codeEnd + 1;

        // your code goes here------------------------------------------------------------------------------------------

        int a, b, c, n, L; //L = label
        int numPast = 2;
        //int numPast = 0;


        while( ip < k ) {  //k is the variable used to keep track
            int op = mem[ip];
            if (debug == 1){
                System.out.println(op);
            }
            a = mem[ip + 1];
            b = mem[ip + 2];
            c = mem[ip + 3];
            //n = mem[ip + 4];
            //L = mem[ip + 5];
            Scanner getInput = new Scanner(System.in);
            switch (op) {


                case 0: //Given. Do nothing
                    ip++;
                    break;

                case 1:
                    //this is already done in opcode when tokenizing
                    ip += 2;
                    break;

                case 2: //Jump to subroutine at label
                    mem[sp] = bp; //this moves the index of bp into the sp cell
                    mem[sp + 1] = ip + 2;
                    bp = sp; //
                    sp += numPast; //numPast is two to skip first two cells
                    ip = mem[ip + 1];
                    //ip++;
                    numPast = 2;
                    break;

                case 3:
                    mem[sp + numPast] = mem[bp + 2 + mem[ip + 1]];
                    numPast++;
                    ip +=2;
                    break;

                case 4: //Given. Increaes SP by n to make space for local variables in the current stack frame
                    sp += mem[ip + 1]; //increments by 1 then uses it
                    ip += 2; //since ip is incremented in live above this works as ip += 2;
                    break;

                case 5: //Return from subroutine, store a in rv
                    rv = mem[bp + 2 + a];
                    ip = mem[bp + 1];
                    sp = bp;
                    bp = mem[bp];
                    break;

                case 6:
                    mem[bp + 2 + mem[ip + 1]] = rv;
                    ip += 2;
                    break;

                case 7: //Change ip to literal L  eg. 7 L (method call)
                    ip = mem[ip + 1];
                    break;

                case 8:
                    if(mem[bp + 2 + mem[ip + 2]] != 0) { //a if the location of the inded on bp
                        ip = mem[ip + 1];
                    }
                    else  ip += 3;
                    break;

                case 9: // a = b + c
                    mem[bp + 2 + a] = mem[bp + 2 + b] + mem[bp + 2 + c];  // |ip (9)| |_| |_| |a| |b| |c|
                    ip += 4;
                    break;

                case 10: // a = b - c
                    mem[bp + 2 + a] = mem[bp + 2 + b] - mem[bp + 2 + c];  // |ip (9)| |_| |_| |a| |b| |c|
                    ip += 4;
                    break;

                case 11: // a = b * c
                    mem[bp + 2 + a] = mem[bp + 2 + b] * mem[bp + 2 + c];  // |ip (9)| |_| |_| |a| |b| |c|
                    ip += 4;
                    break;

                case 12: // a = b / c
                    mem[bp + 2 + a] = mem[bp + 2 + b] / mem[bp + 2 + c];  // |ip (9)| |_| |_| |a| |b| |c|
                    ip += 4;
                    break;

                case 13: // a = b % c
                    mem[bp + 2 + a] = mem[bp + 2 + b] % mem[bp + 2 + c];  // |ip (9)| |_| |_| |a| |b| |c|
                    ip += 4;
                    break;

                case 14: // a = (b == c)
                    if (mem[bp + 2 + b] == mem[bp + 2 + c]){
                        mem[bp + 2 + a] = 1;
                    }
                    else{ mem[bp + 2 + a] = 0; }
                    ip += 4;
                    break;

                case 15: // a = (b != c)
                    if (mem[bp + 2 + b] != mem[bp + 2 + c]){
                        mem[bp + 2 + a] = 1;
                    }
                    else{ mem[bp + 2 + a] = 0; }
                    ip += 4;
                    break;

                case 16: // a = (b < c)
                    if (mem[bp + 2 + b] < mem[bp + 2 + c]){
                        mem[bp + 2 + a] = 1;
                    }
                    else{ mem[bp + 2 + a] = 0; }
                    ip += 4;
                    break;

                case 17: // a = (b <= c)
                    if (mem[bp + 2 + b] <= mem[bp + 2 + c]){
                        mem[bp + 2 + a] = 1;
                    }
                    else{ mem[bp + 2 + a] = 0; }
                    ip += 4;
                    break;

                case 18: // a = (b && c)
                    //TODO is b && > 0???
                    if (mem[bp + 2 + b] != 0 && mem[bp + 2 + c] != 0){
                        mem[bp + 2 + a] = 1;
                    }
                    else{ mem[bp + 2 + a] = 0; }
                    ip += 4;
                    break;

                case 19: // a = (b || c)
                    //TODO is b || c > 0?
                    if (mem[bp + 2 + b] != 0 || mem[bp + 2 + c] != 0){
                        mem[bp + 2 + a] = 1;
                    }
                    else{ mem[bp + 2 + a] = 0; }
                    ip += 4;
                    break;

                case 20: // if (b == 0){  a = 1} else a = 0
                    if (mem[bp + 2 + b] == 0) {
                        mem[bp + 2 + a] = 1;
                    }
                    else { mem[bp + 2 + a] = 0;
                    }
                    ip += 3;
                    break;

                case 21: // a = b * -1  (opposite of b)
                    mem[bp + 2 + a] = mem[bp + 2 + b] * -1;
                    ip += 3;
                    break;

                case 22: // a = n (n is a literal in cell b?)
                    mem[bp + 2 + a] = b;
                    ip += 3;
                    break;

                case 23: // a = b (copy b into a) (retain b?)
                    mem[bp + 2 + a] = mem[bp + 2 + b];
                    ip += 3;
                    break;

                case 24: //Heap stuff
                    //TODO
                    mem[bp + 2 + a] = mem[hp + (mem[bp + 2 + c] + mem[bp + 2 + b])];
                    //mem[a] = mem[(mem[c] + mem[b])];
                    ip += 4;
                    break;

                case 25: //Heap stuff
                    mem[hp + (mem[bp + 2 + a] + mem[bp + 2 + b])] = mem[bp + 2 + c];
                    //mem[(mem[a] + mem[b])] = mem[c];
                    ip += 4;
                    break;

                case 26: // Halt execution
                    System.exit(0);
                    break;

                case 27: // a - integer value from user input
                    System.out.print("? ");
                    mem[bp + 2 + a] = getInput.nextInt();
                    ip+= 2;
                    break;

                case 28: //Display value stored in cell a in the console
                    System.out.print(mem[bp + 2 + a]);
                    ip += 2;
                    break;

                case 29: //Move cursor to the beginning of the next line
                    System.out.println();
                    ip++;
                    break;

                case 30: //If a == 32 -> 126 display ascii in console
                    if (mem[bp + 2 + a] >= 32 && mem[bp + 2 + a] <= 126){
                        System.out.print(Character.toString((char)mem[bp + 2 + a]));
                    }
                    ip += 2;
                    break;

                case 31: //Decrease hp by m and store hp value in a
                    //moves heep space from max so the spaces in front are the heap space
                    int m = mem[bp + 2 + b];
                    mem[bp + 2 + a] = (max - hp);
                    hp -= m;
                    ip += 3;
                    break;

                case 32:
                    gp += mem[ip + 1];
                    bp += mem[ip + 1]; // Added this to make similar to daniel code when he was helping me
                    sp += mem[ip + 1];
                    ip += 2;
                    break;

                case 33:  //spec calls for gp + n, in class we did eg 33 3 7 = mem[bp + 2 + 7]
                    mem[gp + 2 + mem[ip + 2 + 1]] = mem[ip + 2 + mem[ip + 2 + 2]];
                    ip += 3;
                    break;

                case 34:  //copy global memory cell index gp+n into cell a (opposite of case 33)
                    mem[ip + 2 + mem[ip + 2 + 2]] = mem[gp + 2 + mem[ip + 2 + 1]];
                    ip += 3;
                    break;



            } //End switch statement
            if (debug == 2) {
                printMem();
            }
        }


/*        while(  true  ){ //you have to figure out when to end the while

            int op = mem[ ip ];

            if( op==0 ){
                ip++;
            }
            //...
            else if( op== 4){
                //
                n = mem[ ip+1 ];
                sp += n;
                ip += 2; //ip needs to hop two cells
            }
            //....

            //(side note)  mem[ bp + 2 + a ] == access local variable 'a'

        }// end of the fetch-execute loop (while)
*/
        //--------------------------------------------------------------------------------------------------------------

    }// main

    // use symbolic names for all opcodes:

    // op to produce comment on a line by itself
    private static final int noopCode = 0;

    // ops involved with registers
    private static final int labelCode = 1;
    private static final int callCode = 2;
    private static final int passCode = 3;
    private static final int allocCode = 4;
    private static final int returnCode = 5;  // return a means "return and put
    // copy of value stored in cell a in register rv
    private static final int getRetvalCode = 6;//op a means "copy rv into cell a"
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
    private static final int litCode = 22;  // litCode a b means "cell a gets b"
    private static final int copyCode = 23;// copy a b means "cell a gets cell b"
    private static final int getCode = 24; // op a b means "cell a gets
    // contents of cell whose
    // index is stored in b"
    private static final int putCode = 25;  // op a b means "put contents
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

    private static void showMem( int a, int b )
    {
        for( int k=a; k<=b; ++k )
        {
            System.out.println( k + ": " + mem[k] );
        }
    }// showMem

    private static void printMem() {
        System.out.println();
        System.out.print("Current ip: " + ip + " rv: " + rv + "[");
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
