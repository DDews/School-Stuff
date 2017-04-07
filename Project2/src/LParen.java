
public class LParen extends Token {
	public LParen() {
		super(Token.Type.LPAREN);
		Token token = CalcLang.getToken();
		check(token);
	}
}
