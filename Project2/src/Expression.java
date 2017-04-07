
public class Expression extends Token {
	public Expression() {
		super(Token.Type.EXPRESSION);
		Term term = new Term();
		this.line = term.line;
		this.lineIndex = term.lineIndex;
		this.children.add(term);
		Token peek = CalcLang.peekToken();
		switch(peek.type) {
			case PLUS:
			case MINUS:
				this.children.add(CalcLang.getToken());
				this.children.add(new Term());
				break;
			default:	
		}
	}
}
