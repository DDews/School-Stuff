import java.util.ArrayList;

public class Token {
	public enum Type {
		STATEMENTS, STATEMENT,
		SHOW, MESSAGE, INPUT, NEWLINE, EQUALS,
		EXPRESSION, PLUS, MINUS,
		TERM, MULTIPLY, DIVIDE,
		FACTOR, LPAREN, RPAREN,
		BIFN, VARIABLE, NUMBER, STRING,
		EOF
	}
	public int line;
	public int lineIndex;
	public Type type;
	public String string;
	public double value;
	ArrayList<Token> children;
	public Token(Type type, int lineNumber, int lineIndex, double value, String string) {
		children = new ArrayList<Token>();
		this.line = lineNumber;
		this.lineIndex = lineIndex;
		this.type = type;
		this.value = value;
		this.string = string;
	}
	public Token(Type type) {
		this(type,-1,-1,0.0,"");
	}
	public Token(Type type, int lineNumber, int lineIndex) {
		this(type, lineNumber, lineIndex, 0.0, "");
	}
	public Token(Type type, int lineNumber, int lineIndex, double value) {
		this(type, lineNumber, lineIndex, value,"");
	}
	public Token(Type type, int lineNumber, int lineIndex, String string) {
		this(type, lineNumber, lineIndex, 0.0,string);
	}
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("[");
		out.append(type);
		out.append(" <" + line + ":" + lineIndex + ">: ");
		out.append(string);
		out.append(", ");
		out.append(value);
		out.append("{");
		out.append(children);
		out.append("}]");
		return out.toString();
	}
	public void check(Token token) {
		this.line = token.line;
		this.lineIndex = token.lineIndex;
		if (token.type != this.type) throw new IllegalStateException("At line " + token.line + ":" + token.lineIndex + ", expected " + this.type + ", but encountered: " + token);
		this.value = token.value;
		this.string = token.string;
	}
}
