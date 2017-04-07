
public class Term extends Token {
	public Term() {
		super(Token.Type.TERM);
		Factor factor = new Factor();
		this.line = factor.line;
		this.lineIndex = factor.lineIndex;
		this.children.add(factor);
		Token peek = CalcLang.peekToken();
		switch (peek.type) {
			case MULTIPLY:
			case DIVIDE:
				this.children.add(CalcLang.getToken());
				this.children.add(new Term());
				break;
			default:
		}
	}
}
