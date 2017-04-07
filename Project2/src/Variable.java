
public class Variable extends Token {
	public Variable() {
		super(Token.Type.VARIABLE);
		Token token = CalcLang.getToken();
		check(token);
	}
}
