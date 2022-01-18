public class Token {
	/*
		This class implements the tag field of the
		language's tokens.
		It also defines all the tokens
		that do not have a lexem (or rather, the ASCII code
		of the symbol is used to identify them)
	*/
	public final int tag;

	public Token(int t) {
		tag = t;
	}

	public String toString() {
		return "<" + tag + ">";
	}

	// we defined tokens without attributes
	public static final Token not = new Token('!'), 
								lpt = new Token('('), 
								rpt = new Token(')'), 
								lpg = new Token('{'),		
								rpg = new Token('}'), 
								plus = new Token('+'), // tag == symbol's ASCII code
								minus = new Token('-'), 
								mult = new Token('*'),
								div = new Token('/'), 
								semicolon = new Token(';'),
								comma = new Token(',');
}
