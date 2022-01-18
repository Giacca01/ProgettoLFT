package lexer;

public class Token {
	/*
		This class implements the tag field of the
		language's tokens.
		It also defines all the tokens
		that do not have a lexem (or rather, the ASCII code
		of the symbol is used to identify them)
	*/
	public final int tag; // questa classe implementa il nome del token
						  // ovvero il codice numerico assegnato alla coppia
						  // in questa classe vengono implementati anche i
						  // token senza attributo

	public Token(int t) {
		tag = t;
	}

	public String toString() {
		return "<" + tag + ">";
	}

	// dichiarazione token senza attributo
	public static final Token not = new Token('!'), 
								lpt = new Token('('), 
								rpt = new Token(')'), 
								lpg = new Token('{'),		
								rpg = new Token('}'), 
								plus = new Token('+'), // tag == codice ASCII del simbolo
								minus = new Token('-'), 
								mult = new Token('*'),
								div = new Token('/'), 
								semicolon = new Token(';'),
								comma = new Token(',');
}
