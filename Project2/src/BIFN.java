
public class BIFN extends Token {
	public BIFN() {
		super(Token.Type.BIFN);
		Token token = CalcLang.getToken();
		check(token);
	}
}
