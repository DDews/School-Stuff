
public class Statements extends Token {
	public Statements() {
		super(Token.Type.STATEMENTS);
		this.children.add(new Statement());
		line = children.get(0).line;
		lineIndex = children.get(0).lineIndex;
		Token peek = CalcLang.peekToken();
		switch(peek.type) {
			case VARIABLE:
			case SHOW:
			case MESSAGE:
			case INPUT:
			case NEWLINE:
				this.children.add(new Statements());
				break;
			default:	
		}
	}
}
