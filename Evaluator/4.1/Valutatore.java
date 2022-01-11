import java.io.*;
import lexer.*;

/*
    ATTENZIONE!!!!
    Il lexer usato non è l'ultima versione
    presente in questa repository
*/


public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) {
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
            error("syntax error");
    }

    public void start() {
        // production: S->EEOF
        int expr_val = -1;

        switch (look.tag) {
            case '(':
            case Tag.NUM:
                expr_val = expr();
                match(Tag.EOF);
                System.out.println(expr_val);
                break;

            default:
                error("start");
                break;
        }
    }

    private int expr() {
        // production: E->TE'
        int term_val = -1, expr_val = -1;

        switch (look.tag) {
            case '(':
            case Tag.NUM:
                term_val = term();
                expr_val = exprp(term_val);
                break;

            default:
                error("expr");
                break;
        }

        return expr_val;
    }

    private int exprp(int exprp_i) {
        int term_val = -1, exprp_val = -1;

        switch (look.tag) {
            case '+':
                // production: E' -> +TE'
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;

            case '-':
                // production: E' -> -TE'
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;

            case ')':
            case Tag.EOF:
                // production: E' -> epsilon
                exprp_val = exprp_i;
                break;

            default:
                error("exprp");
                break;
        }

        return exprp_val;
    }

    private int term() {
        // production: T->FT'
        int fact_val = -1, termp_val = -1;
        fact_val = fact();
        termp_val = termp(fact_val);

        return termp_val;
    }

    private int termp(int termp_i) {
        int fact_val = -1, termp_val = -1;

        switch (look.tag) {
            case '*':
                // production: T'->*FT'
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;
            
            case '/':
                // production: T'->/FT'
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;

            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                // production: T'->epsilon
                termp_val = termp_i;
                break;
        
            default:
                error("termp");
                break;
        }

        return termp_val;
    }

    private int fact() {
        int fact_val = -1;

        switch (look.tag) {
            case '(':
                // production: F->(E)
                match('(');
                fact_val = expr();
                match(')');
                break;

            case Tag.NUM:
                // production: F->NUM
                // we are duplicating a bit of the match(int) function's code
                // in order to be able to save the lexem (i.e. the lexem's value)
                // in case look is really a numeric tag
                if (look.tag == Tag.NUM) {
                    fact_val = ((NumberTok) look).lexem;
                    // this is actually redundant, but it's necessary
                    // to consume the current token and move on to the next one
                    match(Tag.NUM);
                } else {
                    error("syntax error");
                }

                break;

            default:
                error("fact");
                break;
        }

        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Test.txt"; // il percorso del file da leggere

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            // Avvio analisi lessicale
            Valutatore valutatore = new Valutatore(lex, br);

            // Inizio parsificazione(analisi sintattica) e valutazione della sequennza di token
            // in questo caso due componenti logicamente distinti della pipeline 
            // di traduzione, ovvero parser e valutatore, sono implementati con un solo software
            // questa è una pratica comune: normalmente tutti i componenti della pipeline
            // sono fusi in un solo software oppure raccolti in una collezione "automatizzata"
            // (es. gcc)
            valutatore.start();

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}