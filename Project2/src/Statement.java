
public class Statement extends Token {
	public Statement() {
		super(Token.Type.STATEMENT);
		Token peek = CalcLang.peekToken();
		switch (peek.type) {
			case VARIABLE:
				this.children.add(new Variable());
				this.children.add(new Equals());
				this.children.add(new Expression());
				break;
			case SHOW:
				this.children.add(CalcLang.getToken());
				this.children.add(new Expression());
				break;
			case MESSAGE:
				this.children.add(CalcLang.getToken());
				this.children.add(new StringToken());
				break;
			case INPUT:
				this.children.add(CalcLang.getToken());
				this.children.add(new StringToken());
				this.children.add(new Variable());
				break;
			case NEWLINE:
				this.children.add(CalcLang.getToken());
				break;
			default:
				check(peek);
		}
		line = children.get(0).line;
		lineIndex = children.get(0).lineIndex;
	}
}
