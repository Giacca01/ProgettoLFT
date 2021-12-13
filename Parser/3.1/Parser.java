import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("Syntax error");
    }

    public void start() {
        switch (look.tag) {
            // <start> ::= <expr> EOF
            case '(':
            case Tag.NUM:
                expr();
                match(Tag.EOF);
                break;

            default:
                error("Error in grammar (expr) with <" + look.tag + ">");
        }
    }

    private void expr() {
        switch (look.tag) {
            // <expr> ::= <term> <exprp>
            case '(':
            case Tag.NUM:
                term();
                exprp();
                break;

            default:
                error("Error in grammar (expr) with <" + look.tag + ">");
        }
    }

    private void exprp() {
        switch (look.tag) {
            // <exprp> ::= + <term> <exprp>
            case '+':
                match(Token.plus.tag);
                term();
                exprp();
                break;

            // <exprp> ::= - <term> <exprp>
            case '-':
                match(Token.minus.tag);
                term();
                exprp();
                break;

            // <exprp> ::= eps
            case Tag.EOF:
            case ')':
                break;

            default:
                error("Error in grammar (expr) with <" + look.tag + ">");
        }
    }

    private void term() {
        switch (look.tag) {
            // <term> ::= <fact> <termp>
            case '(':
            case Tag.NUM:
                fact();
                termp();
                break;

            default:
                error("Error in grammar (expr) with <" + look.tag + ">");
        }
    }

    private void termp() {
        switch (look.tag) {
            // <termp> ::= * <fact> <termp>
            case '*':
                match('*');
                fact();
                termp();
                break;

            // <termp> ::= / <fact> <termp>
            case '/':
                match('/');
                fact();
                termp();
                break;

            // <termp> ::= eps
            case '+':
            case Tag.EOF:
            case ')':
            case '-':
                break;

            default:
                error("Error in grammar (expr) with <" + look.tag + ">");
        }
    }

    private void fact() {
        switch (look.tag) {
            // <fact> ::= (<expr>)
            case '(':
                match('(');
                expr();
                match(')');
                break;

            // <fact> ::= NUM
            case Tag.NUM:
                match(Tag.NUM);
                break;

            default:
                error("Error in grammar (expr) with <" + look.tag + ">");
                break;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}