
public class RParen extends Token {
	public RParen() {
		super(Token.Type.RPAREN);
		Token token = CalcLang.getToken();
		check(token);
	}
}
