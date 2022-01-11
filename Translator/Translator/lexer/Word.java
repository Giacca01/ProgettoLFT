package lexer;

public class Word extends Token {
	/*
	 * This class implements the lexeme fiels of words' tokens. It also declares
	 * language keywords' tokens.
	 */
	public String lexeme = ""; // questa classe implementa la parte del lessema
								// per i token corrispondenti a parole, ovvero
								// identificatori, costrutti, operatori relazionali

	public Word(int tag, String s) { // tag ed s sono i due componenti delle coppia
										// che costituisce il token
		super(tag);
		lexeme = s;
	}

	public String toString() {
		return "<" + tag + ", " + lexeme + ">";
	}

	// dichiaro il token final perchè tanto dopo averlo
	// costruito non dovrò modificarlo in alcun modo
	// possiamo immaginare questi oggetti come token precostituiti
	// che possiamo definire subito senza aspettare la scansione
	// della stringa perchè conosciamo già tag e lessemi che formano
	// le coppi
	// Durante la scansione ci limiteremo ad utilizzare questi token
	// senza bisogno di ridefinirli
	public static final Word assign = new Word(Tag.ASSIGN, "assign"), to = new Word(Tag.TO, "to"),
			iftok = new Word(Tag.IF, "if"), elsetok = new Word(Tag.ELSE, "else"),
			whiletok = new Word(Tag.WHILE, "while"), begin = new Word(Tag.BEGIN, "begin"),
			end = new Word(Tag.END, "end"), print = new Word(Tag.PRINT, "print"), read = new Word(Tag.READ, "read"),
			or = new Word(Tag.OR, "||"), and = new Word(Tag.AND, "&&"), lt = new Word(Tag.RELOP, "<"),
			gt = new Word(Tag.RELOP, ">"), eq = new Word(Tag.RELOP, "=="), le = new Word(Tag.RELOP, "<="),
			ne = new Word(Tag.RELOP, "<>"), ge = new Word(Tag.RELOP, ">=");
}
