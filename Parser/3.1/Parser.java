import java.io.*;

public class Parser {
    
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
    void error(String s) {
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
        if (look.tag == t) {
            if (look.tag != Tag.EOF) // if we haven't reached the end of file yet, read the next token
                move();
        } 
        else // Tag was not expected, raise and error
            error("syntax error (expected token with name " + t + ")");
    }

    /**
     * Function that rappresents variable start in the grammar. Depending on the
     * token recognized from file, applies the corresponding production of the
     * grammar. In case the token is unexpected, raises an error.
     */
    public void start() {
        switch (look.tag) {
            /*
             * Production: <start> ::= <expr> EOF
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '(':
            case Tag.NUM:
                expr();
                match(Tag.EOF);
                break;

            default: // The token read wasn't expected, raise an error
                error("Error in grammar (start) with <" + look.tag + ">");
        }
    }

    /**
     * Function that rappresents variable expr in the grammar. Depending on the
     * token recognized from file, applies the corresponding production of the
     * grammar. In case the token is unexpected, raises an error.
     */
    private void expr() {
        switch (look.tag) {
            /*
             * Production: <expr> ::= <term> <exprp>
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '(':
            case Tag.NUM:
                term();
                exprp();
                break;

            default: // The token read wasn't expected, raise an error
                error("Error in grammar (expr) with <" + look.tag + ">");
        }
    }

    /**
     * Function that rappresents variable exprp in the grammar. Depending on the
     * token recognized from file, applies the corresponding production of the
     * grammar. In case the token is unexpected, raises an error.
     */
    private void exprp() {
        switch (look.tag) {
            /*
             * Production: <exprp> ::= + <term> <exprp>
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '+':
                match(Token.plus.tag);
                term();
                exprp();
                break;

            /*
             * Production: <exprp> ::= - <term> <exprp>
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '-':
                match(Token.minus.tag);
                term();
                exprp();
                break;

            /*
             * Production: <exprp> ::= epsilon
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case Tag.EOF:
            case ')':
                break;

            default: // The token read wasn't expected, raise an error
                error("Error in grammar (exprp) with <" + look.tag + ">");
        }
    }

    /**
     * Function that rappresents variable term in the grammar. Depending on the
     * token recognized from file, applies the corresponding production of the
     * grammar. In case the token is unexpected, raises an error.
     */
    private void term() {
        switch (look.tag) {
            /*
             * Production: <term> ::= <fact> <termp>
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '(':
            case Tag.NUM:
                fact();
                termp();
                break;

            default: // The token read wasn't expected, raise an error
                error("Error in grammar (term) with <" + look.tag + ">");
        }
    }

    /**
     * Function that rappresents variable termp in the grammar. Depending on the
     * token recognized from file, applies the corresponding production of the
     * grammar. In case the token is unexpected, raises an error.
     */
    private void termp() {
        switch (look.tag) {
            /*
             * Production: <termp> ::= * <fact> <termp>
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '*':
                match('*');
                fact();
                termp();
                break;

            /*
             * Production: <termp> ::= / <fact> <termp>
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '/':
                match('/');
                fact();
                termp();
                break;

            /*
             * Production: <termp> ::= epsilon
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '+':
            case Tag.EOF:
            case ')':
            case '-':
                break;

            default: // The token read wasn't expected, raise an error
                error("Error in grammar (termp) with <" + look.tag + ">");
        }
    }

    /**
     * Function that rappresents variable fact in the grammar. Depending on the
     * token recognized from file, applies the corresponding production of the
     * grammar. In case the token is unexpected, raises an error.
     */
    private void fact() {
        switch (look.tag) {
            /*
             * Production: <fact> ::= (<expr>)
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case '(':
                match('(');
                expr();
                match(')');
                break;

            /*
             * Production: <fact> ::= NUM
             * if the token is in the guide set of the production, then continue
             * recognizing the elements in the body of the production.
             */
            case Tag.NUM:
                match(Tag.NUM);
                break;

            default: // The token read wasn't expected, raise an error
                error("Error in grammar (fact) with <" + look.tag + ">");
                break;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer(); // Initializes the lexer to recognize tokens from file
        String path = "Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path)); // Open an input stream
            Parser parser = new Parser(lex, br); // Initializes the parser
            parser.start(); // Starts the parsing process
            System.out.println("Input OK"); // If the execution arrives here, the parsing process terminated successfully
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}