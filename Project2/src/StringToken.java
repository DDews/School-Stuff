
public class StringToken extends Token {
	public StringToken() {
		super(Token.Type.STRING);
		Token token = CalcLang.getToken();
		check(token);
	}
}
