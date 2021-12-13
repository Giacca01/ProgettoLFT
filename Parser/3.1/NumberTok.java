public class NumberTok extends Token {
	// questa classe implementa la parte del lessema
	// per i token corrispondenti a costanti numeriche
	/*
	 * This class implements the lexeme field of numeric constants' tokens.
	 */
	public int lexeme = 0;

	public NumberTok(int tag, int lexeme) {
		super(tag); // richiama il costruttore di Token

		if (lexeme > -1)
			this.lexeme = lexeme;
		else
			System.out.println("Invalid numeric lexem");
	}

	public String toString() {
		return "<" + tag + ", " + lexeme + ">";
	}

	// qui non conosciamo in anticipo i lessemi che formeranno
	// i vari token, quindi non possiamo definirli anticipatamente
	// Li definiremo durante la scansione, quando sarà noto il valore
	// della costante numerica rappresentata dal token
}
