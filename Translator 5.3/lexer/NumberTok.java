package lexer;

public class NumberTok extends Token {
	// questa classe implementa la parte del lessema
	// per i token corrispondenti a costanti numeriche
	/*
	 * This class implements the lexeme field of numeric constants' tokens.
	 */
	public int lexem = 0;

	public NumberTok(int tag, int lexem) {
		super(tag); // richiama il costruttore di Token

		if (lexem > -1)
			this.lexem = lexem;
		else
			System.out.println("Invalid numeric lexem");
	}

	public String toString() {
		return "<" + tag + ", " + lexem + ">";
	}

	// qui non conosciamo in anticipo i lessemi che formeranno
	// i vari token, quindi non possiamo definirli anticipatamente
	// Li definiremo durante la scansione, quando sarà noto il valore
	// della costante numerica rappresentata dal token
}
