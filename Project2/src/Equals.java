
public class Equals extends Token {
	public Equals() {
		super(Token.Type.EQUALS);
		Token token = CalcLang.getToken();
		check(token);
	}
}
