import java.io.*;

public class Parser
{
	// Private variables for the parser
	private Lexer lex;
	private BufferedReader pbr;
	private Token look;
	
	/**
	 * Constructor of Parser class, initializes private Lexer and BufferedReader
	 * with the ones given as parameters.
	 * 
	 * @param l  pointer to the Lexer instance
	 * @param br pointer to the BufferedReader instance
	 */
	public Parser(Lexer l, BufferedReader br) {
		lex = l;
		pbr = br;
		// Read the first token so that variable look contains a value
		move();
	}
	
	/**
	 * Function that reads and prints the next token recognized in the file opened
	 * by the BufferedReader.
	 */
	void move() {
		look = lex.lexical_scan(pbr); // Reads the next token from file
		System.out.println("token = " + look);
	}
	
	/**
	 * Function that throws an Error with line where the error occured in the file
	 * and a message, given as parameter.
	 * 
	 * @param s the error message to print
	 */
	void error (String s) {
		throw new Error("near line " + lex.line + ": " + s);
	}
	
	/**
	 * Function that checks if the tag recognized from file is the expected one
	 * (passed as parameter); if it's true and the tag is not the End Of File, reads
	 * a new token from file, otherwise raises a Syntax error.
	 * 
	 * @param t the tag of the token that look is expected to be
	 */
	void match(int t) {
		// Check if the token recognized from file is the expected one
		if(look.tag == t) {
			if(look.tag != Tag.EOF) // if we haven't reached the end of file yet, read the next token
				move();
		}
		else // Tag was not expected, raise and error
			error("syntax error (expected token with name " + t + ")");
	}
	
	/**
	 * Function that rappresents variable prog in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	public void prog() {
		switch(look.tag) {
			/*
			 * Production: <prog> ::= <statlist> EOF
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.ASSIGN:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.WHILE:
			case Tag.IF:
			case '{':
				statlist();
				match(Tag.EOF);
				break;
				
			default: // The token read wasn't expected, raise an error
				error("error in grammar (prog) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable statlist in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void statlist() {
		switch(look.tag) {
			/*
			 * Production: <statlist> ::= <stat> <statlistp>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.ASSIGN:
			case Tag.PRINT:
			case Tag.READ:
			case Tag.WHILE:
			case Tag.IF:
			case '{':
				stat();
				statlistp();
				break;
				
			default: // The token read wasn't expected, raise an error
				error("error in grammar (statlist) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable statlistp in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void statlistp() {
		switch(look.tag) {
			/*
			 * Production: <statlistp> ::= ; <stat> <statlistp>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case ';':
				match(';');
				stat();
				statlistp();
				break;
			
			/*
			 * Production: <statlistp> ::= epsilon
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.EOF:
			case '}':
				break;
				
			default: // The token read wasn't expected, raise an error
				error("error in grammar (statlistp) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable stat in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void stat() {
		switch(look.tag) {
			/*
			 * Production: <stat> ::= assign <expr> to <idlist>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.ASSIGN:
				match(Tag.ASSIGN);
				expr();
				match(Tag.TO);
				idlist();
				break;
			
			/*
			 * Production: <stat> ::= print ( <exprlist> )
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.PRINT:
				match(Tag.PRINT);
				match('(');
				exprlist();
				match(')');
				break;
			
			/*
			 * Production: <stat> ::= read ( <idlist> )
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.READ:
				match(Tag.READ);
				match('(');
				idlist();
				match(')');
				break;
			
			/*
			 * Production: <stat> ::= while ( <bexpr> ) <stat>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.WHILE:
				match(Tag.WHILE);
				match('(');
				bexpr();
				match(')');
				stat();
				break;
			
			/*
			 * Production: <stat> ::= if( <bexpr> ) <stat> <statp>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.IF:
				match(Tag.IF);
				match('(');
				bexpr();
				match(')');
				stat();
				statp();
				break;
			
			/*
			 * Production: <stat> ::= { <statlist> }
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case '{':
				match('{');
				statlist();
				match('}');
				break;
				
			default: // The token read wasn't expected, raise an error
				error("error in grammar (stat) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable statp in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void statp() {
		switch(look.tag) {
			// produzione statp ::= end
			/*
			 * Production: <statp> ::= end
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.END:
				// This procedure's invocation is used mainly as a way to ask the lexer for another token
				match(Tag.END);
				break;
			
			/*
			 * Production: <statp> ::= else <stat> end
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.ELSE:
				match(Tag.ELSE);
				stat();
				match(Tag.END);
				break;
				
			default: // The token read wasn't expected, raise an error
				error("error in grammar (statp) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable idlist in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void idlist() {
		switch(look.tag) {
			/*
			 * Production: <idlist> ::= ID <idlistp>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.ID:
				match(Tag.ID);
				idlistp();
				break;
				
			default: // The token read wasn't expected, raise an error
				error("error in grammar (idlist) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable idlistp in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void idlistp() {
		switch(look.tag) {
			/*
			 * Production: <idlistp> ::= , ID <idlistp>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case ',':
				match(',');
				match(Tag.ID);
				idlistp();
				break;
			
			/*
			 * Production: <idlistp> ::= epsilon
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case ';':
			case Tag.EOF:
			case '}':
			case Tag.ELSE:
			case Tag.END:
			case ')':
				break;
			
			default: // The token read wasn't expected, raise an error
				error("error in grammar (idlistp) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable bexpr in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void bexpr() {
		switch(look.tag) {
			/*
			 * Production: <bexpr> ::= RELOP <expr> <expr>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.RELOP:
				match(Tag.RELOP);
				expr();
				expr();
				break;
			
			default: // The token read wasn't expected, raise an error
				error("error in grammar (bexpr) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable expr in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void expr() {
		switch(look.tag) {
			/*
			 * Production: <expr> ::= + ( <exprlist> )
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case '+':
				match('+');
				match('(');
				exprlist();
				match(')');
				break;
			
			/*
			 * Production: <expr> ::= * ( <exprlist> )
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case '*':
				match('*');
				match('(');
				exprlist();
				match(')');
				break;
			
			/*
			 * Production: <expr> ::= - <expr> <expr>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case '-':
				match('-');
				expr();
				expr();
				break;
			
			/*
			 * Production: <expr> ::= / <expr> <expr>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case '/':
				match('/');
				expr();
				expr();
				break;
			
			/*
			 * Production: <expr> ::= NUM
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.NUM:
				match(Tag.NUM);
				break;
			
			/*
			 * Production: <expr> ::= ID
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case Tag.ID:
				match(Tag.ID);
				break;
			
			default: // The token read wasn't expected, raise an error
				error("error in grammar (expr) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable exprlist in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void exprlist() {
		switch(look.tag) {
			/*
			 * Production: <exprlist> ::= <expr> <exprlistp>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case '+':
			case '*':
			case '-':
			case '/':
			case Tag.NUM:
			case Tag.ID:
				expr();
				exprlistp();
				break;
			
			default: // The token read wasn't expected, raise an error
				error("error in grammar (exprlist) with <" + look.tag + ">");
		}
	}
	
	/**
	 * Function that rappresents variable exprlistp in the grammar. Depending on the
	 * token recognized from file, applies the corresponding production of the
	 * grammar. In case the token is unexpected, raises an error.
	 */
	private void exprlistp() {
		switch(look.tag) {
			/*
			 * Production: <exprlistp> ::= , <expr> <exprlistp>
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case ',':
				match(',');
				expr();
				exprlistp();
				break;
			
			/*
			 * Production: <exprlistp> ::= epsilon
			 * if the token is in the guide set of the production, then continue
			 * recognizing the elements in the body of the production.
			 */
			case ')':
				break;
			
			default: // The token read wasn't expected, raise an error
				error("error in grammar (exprlistp) with <" + look.tag + ">");
		}
	}
	
	public static void main(String[] args) {
		Lexer lex = new Lexer(); // Initializes the lexer to recognize tokens from file

		String path = args[0]; // il percorso del file da leggere
		try {
			BufferedReader br = new BufferedReader(new FileReader(path)); // Open an input stream
			Parser parser = new Parser(lex, br); // Initializes the parser
			parser.prog(); // Starts the parsing process
			System.out.println("Input OK"); // If the execution arrives here, the parsing process terminated successfully
			br.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}