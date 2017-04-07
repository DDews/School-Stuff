import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;


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
		CalcLang lang = new CalcLang();
		lang.showTree();
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
	public void showTree() {
		DefaultTreeForTreeLayout<Token> tree = new DefaultTreeForTreeLayout<Token>(head);
        addChildren(tree,head);
        DefaultConfiguration<Token> configuration = new DefaultConfiguration<Token>(
                20,
                20);
        NodeExtentProvider<Token> nodeExtentProvider = new TokenExtentProvider();
        TreeLayout<Token> treeLayout = new TreeLayout<Token>(
                tree,
                nodeExtentProvider,
                configuration);
        ParseTreePanel treePanel = new ParseTreePanel(treeLayout);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane jscroll = new JScrollPane(treePanel);
        frame.getContentPane().add(jscroll);
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
        frame.setAlwaysOnTop(true);
        frame.setAlwaysOnTop(false);
	}
	public static void addChildren(DefaultTreeForTreeLayout tree, Token token) {
		for (Token child : token.children) {
			tree.addChild(token, child);
			addChildren(tree,child);
		}
	}
	class TokenExtentProvider implements NodeExtentProvider<Token> {

        public double getWidth(Token treeNode) {
            return 100;
        }

        public double getHeight(Token treeNode) {
            return 20;
        }
    };
	class ParseTreePanel extends JPanel {
        private TreeLayout<Token> treeLayout;

        private final static int ARC_SIZE = 10;
        private final Color ROOT_COLOR = Color.orange;
        private final Color AGREE_COLOR = Color.green;
        private final Color DISAGREE_COLOR = Color.red;
        private final Color BORDER_COLOR = Color.darkGray;
        private final Color TEXT_COLOR = Color.black;

        public ParseTreePanel(TreeLayout<Token> treeLayout) {
            setTreeLayout(treeLayout);
        }
        
        public void updateTreeLayout() {
            TreeLayout<Token> newTreeLayout = new TreeLayout<Token>(
                    treeLayout.getTree(),
                    treeLayout.getNodeExtentProvider(),
                    treeLayout.getConfiguration());
            setTreeLayout(newTreeLayout);
        }

        private void setTreeLayout(TreeLayout<Token> treeLayout) {
            this.treeLayout = treeLayout;

            Dimension size = treeLayout.getBounds().getBounds().getSize();
            setPreferredSize(size);
        }
        private TreeForTreeLayout<Token> getTree() {
            return treeLayout.getTree();
        }

        private Iterable<Token> getChildren(Token parent) {
            return getTree().getChildren(parent);
        }

        private Rectangle2D.Double getBoundsOfNode(Token node) {
            return treeLayout.getNodeBounds().get(node);
        }

        private void paintEdges(Graphics g, Token parent) {
            if (!getTree().isLeaf(parent)) {
                Rectangle2D.Double b1 = getBoundsOfNode(parent);
                double x1 = b1.getCenterX();
                double y1 = b1.getCenterY();
                for (Token child : getChildren(parent)) {
                    Rectangle2D.Double b2 = getBoundsOfNode(child);
                    g.drawLine(
                            (int) x1,
                            (int) y1,
                            (int) b2.getCenterX(),
                            (int) b2.getCenterY());

                    paintEdges(g, child);
                }
            }
        }

        private void paintBox(Graphics g, Token argNode) {
            // draw the box in the background

            if (argNode instanceof Statements) {
                g.setColor(Color.orange);
            } else if (argNode instanceof Statement) {
                g.setColor(Color.green);
            } else if (argNode instanceof Expression) {
                g.setColor(Color.red);
            } else g.setColor(Color.white);

            Rectangle2D.Double box = getBoundsOfNode(argNode);
            g.fillRoundRect(
                    (int) box.x,
                    (int) box.y,
                    (int) box.width - 1,
                    (int) box.height - 1,
                    ARC_SIZE,
                    ARC_SIZE);
            g.setColor(BORDER_COLOR);
            g.drawRoundRect(
                    (int) box.x,
                    (int) box.y,
                    (int) box.width - 1,
                    (int) box.height - 1,
                    ARC_SIZE,
                    ARC_SIZE);

            // draw the text on top of the box (possibly multiple lines)
            g.setColor(TEXT_COLOR);
            String line = argNode.type.toString() + ": " + (argNode.string.equals("") ? (argNode.value == 0.0 ? "" : Double.toString(argNode.value)) : argNode.string);
            FontMetrics m = getFontMetrics(getFont());
            int x = (int) box.x + ARC_SIZE / 2;
            int y = (int) box.y + m.getAscent() + m.getLeading() + 1;
            g.drawString(line, x, y);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            paintEdges(g, getTree().getRoot());

            // paint the boxes
            for (Token argNode : treeLayout.getNodeBounds().keySet()) {
                paintBox(g, argNode);
            }
        }
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
