public class NumberTok extends Token {
	/*
		This class implements the lexeme field of numeric
		constants' tokens. 
	*/
	public int lexem = 0;

	public NumberTok(int tag, int lexem) {
		super(tag);

		if (lexem > -1)
			this.lexem = lexem;
		else
			System.out.println("Invalid numeric lexem");
	}

	public String toString(){
		return "<" + tag + ", " + lexem + ">";
	}
}
