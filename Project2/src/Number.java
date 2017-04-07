
public class Number extends Token {
	public Number() {
		super(Token.Type.NUMBER);
		Token token = CalcLang.getToken();
		check(token);
	}
}
