import java.io.*;
import lexer.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    public Translator(Lexer l, BufferedReader br) {
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
            if (look.tag != Tag.EOF) // Tag.EOF
                move();
        } else
            error("syntax error");
    }

    public void prog() {
        /*
         * We define the production associated to <prog> by considering that a program
         * in a certain programming language consists of a list of statements
         * (<statlist>)
         * followed by the end of file
         */
        int labelNext;

        switch (look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
            case '{':
                // <prog> -> <statlist>EOF
                labelNext = code.newLabel();
                statlist(labelNext);
                code.emitLabel(labelNext);
                match(Tag.EOF);
                try {
                    code.toJasmin();
                } catch (java.io.IOException e) {
                    System.out.println("IO error\n");
                }
                break;

            default:
                error("prog");
                break;
        }
    }

    private void statlist(int next) {
        int labelNext;

        switch (look.tag) {
            case '{':
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
                // <statlist> -> <stat><statlitsp>
                labelNext = code.newLabel();
                stat(labelNext);
                code.emitLabel(labelNext);
                statlistp(next);
                break;

            default:
                error("statlist");
                break;
        }
    }

    private void statlistp(int next) {
        /**
         * We further define a list of statements as a sequence of statements separated
         * by a semicolon, so we define the production <statlistp> -> ;<stat><statlistp>
         * To end the sequence we need to be able to replace <statlistp> with an empty
         * string and we therefore need to apply <statlistp> -> epsilon
         */
        int labelNext;

        switch (look.tag) {
            case ';':
                // <statlistp> -> ;<stat><statlistp>
                labelNext = code.newLabel();
                match(';');
                stat(labelNext);
                code.emitLabel(labelNext);
                statlistp(next);
                break;

            case Tag.EOF:
            case '}':
                code.emit(OpCode.GOto, next);
                break;

            default:
                error("statlistp");
                break;
        }
    }

    private void stat(int next) {
        /**
         * In the lft programming language, a statement can be composed of either
         * keywords (see Language.pdf), structured with a certain syntax (see the
         * grammar table)
         * defined by the first five productions.
         * A block of statements can be enclosed in a pair of brackets (e.g. if used as
         * body of while or if)
         */
        int bexprTrue, statNext, bexprFalse = -1;

        switch (look.tag) {
            case Tag.ASSIGN:
                // <stat> -> assign<expr>to<idlist>
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(next, 0);
                break;

            case Tag.PRINT:
                // <stat> -> print(<exprlist>)
                match(Tag.PRINT);
                match('(');
                // we need to invoke the print methods multiple times
                // to print multiple values, because JVM
                // doesn't support list printing natively
                exprlist(next, 2);
                match(')');
                code.emit(OpCode.GOto, next);
                break;

            case Tag.READ:
                // <stat> -> read(<idlist>)
                match(Tag.READ);
                code.emit(OpCode.invokestatic, 0);
                match('(');
                // we need to invoke the read methods multiple times
                // to print multiple values, because JVM
                // doesn't support list read natively
                idlist(next, 1);
                match(')');

                break;

            case Tag.WHILE:
                // <stat> -> while(<bexpr>)<stat>
                bexprTrue = code.newLabel();

                statNext = code.newLabel();

                match(Tag.WHILE);
                match('(');
                code.emitLabel(statNext);
                bexpr(bexprTrue, next);
                match(')');
                code.emitLabel(bexprTrue);
                stat(statNext);
                break;

            case Tag.IF:
                // <stat> -> if(<bexpr>)<stat><statp>
                bexprTrue = code.newLabel();
                bexprFalse = code.newLabel();
                match(Tag.IF);
                match('(');
                bexpr(bexprTrue, bexprFalse);
                match(')');
                code.emitLabel(bexprTrue);
                stat(next);
                code.emitLabel(bexprFalse);
                statp(next);
                break;

            case '{':
                // <start> -> {<statlist>}
                match('{');
                statlist(next);
                match('}');
                break;

            default:
                error("stat");
                break;
        }
    }

    private void statp(int next) {
        /**
         * This variable was introduced to factorize the productions that dictate
         * the structure of the if statement.
         * In our language, it can be followed by a block of conde, enclosed in
         * brackets, by
         * the ELSE keyword or by an empty body (first production).
         * The latter is also used to mark the end of a non-empty list of statements
         * that follows if.
         */
        switch (look.tag) {
            case Tag.END:
                // <statp> -> end
                match(Tag.END);
                code.emit(OpCode.GOto, next);
                break;

            case Tag.ELSE:
                // <statp> -> {<statlist>}
                match(Tag.ELSE);
                stat(next);
                match(Tag.END);
                break;

            default:
                error("statp");
                break;
        }
    }

    private void idlist(int next, int type) {
        /**
         * A list of variables, as used in print or read, can e broken
         * down into a single ID followed by a list of identifiers
         */
        int id_addr = -1;
        Token aus = null;

        switch (look.tag) {
            case Tag.ID:
                // <idlist> -> ID<idlistp>
                aus = look;
                match(Tag.ID);
                id_addr = st.lookupAddress(((Word) aus).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) aus).lexeme, count++);
                }
                // we need to do this here because
                // we need to push operand onto the stack
                // in the order they were written in
                if (type == 0)
                    code.emit(OpCode.dup);
                code.emit(OpCode.istore, id_addr);
                idlistp(next, type);
                break;

            default:
                error("idlist");
                break;
        }
    }

    private void idlistp(int next, int type) {
        /**
         * A list of identifiers is further defined as
         * a sequence of single statements, separated by a comma.
         * The second production defines the end of a list of identifiers (??)
         */
        int id_addr = -1;
        Token aus = null;

        switch (look.tag) {
            case ',':
                // <idlistp> -> ,ID<idlistp>
                match(',');
                aus = look;
                match(Tag.ID);
                id_addr = st.lookupAddress(((Word) aus).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) aus).lexeme, count++);
                }
                if (type == 0) {
                    // assign
                    code.emit(OpCode.dup);
                } else {
                    code.emit(OpCode.invokestatic, 0);
                }
                code.emit(OpCode.istore, id_addr);
                idlistp(next, type);
                break;

            case Tag.EOF:
            case ';':
            case ')':
            case Tag.END:
            case Tag.ELSE:
            case '}':
                // <idlistp> -> epsilon
                if (type == 0)
                    code.emit(OpCode.pop);
                code.emit(OpCode.GOto, next);
                break;

            default:
                error("idlistp");
                break;
        }
    }

    private void expr() {
        /**
         * These productions define the structure of valid arithmetic expression
         * written in prefixed form, that can be a
         * sum/subtrction/multiplication/division
         * between variables and constants.
         */
        int id_addr = -1;
        int exprlistNext = -1;
        Token aus = null;

        switch (look.tag) {
            case '+':
                // <expr> ->+(<exprlist>)
                exprlistNext = code.newLabel();

                match('+');
                match('(');
                code.emitLabel(exprlistNext);
                exprlist(exprlistNext, 0);
                match(')');
                break;

            case '-':
                // <expr> -> - <expr><expr>
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;

            case '*':
                // <expr> ->*(<exprlist>)
                exprlistNext = code.newLabel();

                match('*');
                match('(');
                code.emitLabel(exprlistNext);
                exprlist(exprlistNext, 1);
                match(')');
                break;

            case '/':
                // <expr> ->/<expr><expr>
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;

            case Tag.NUM:
                // <expr> -> NUM
                aus = look;
                match(Tag.NUM);
                code.emit(OpCode.ldc, ((NumberTok) aus).lexem);
                break;

            case Tag.ID:
                // <expr> -> ID
                aus = look;
                match(Tag.ID);
                id_addr = st.lookupAddress(((Word) aus).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) aus).lexeme, count++);
                }
                code.emit(OpCode.iload, id_addr);
                break;

            default:
                error("expr");
                break;
        }
    }

    private void exprlist(int next, int typeOp) {
        /**
         * A list of arithmetic expression
         * is composed of single expression, separated by a comma.
         * This list can be terminated by applying the last production
         */
        switch (look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                if (typeOp == 2)
                    code.emit(OpCode.invokestatic, 1);
                exprlistp(next, typeOp);
                break;

            default:
                error("exprlist");
                break;
        }
    }

    private void exprlistp(int next, int typeOp) {
        switch (look.tag) {
            case ',':
                // <exprlistp> -> ,<expr><exprlistp>
                match(',');
                expr();
                if (typeOp == 2)
                    code.emit(OpCode.invokestatic, 1);
                else if (typeOp == 0)
                    code.emit(OpCode.iadd);
                else
                    code.emit(OpCode.imul);
                exprlistp(next, typeOp);

                break;

            case ')':
                break;

            default:
                error("exprlistp");
                break;
        }
    }

    /*
     * Sezione modificata per implementare l'esercizio 5.3
     * 
     * Per implementarlo togliamo la generazione del goto da bexpr e poi dobiamo
     * fare in modo che, quando la condizione che compare nel sorgente è vera, NON
     * venga effettuato alcun salto, proseguendo semplicemente con l'esecuzione del
     * codice successivo alla if.
     * Per ottenere tale effetto traduciamo la condizione della if che compare nel
     * sorgente con il suo complementare.
     */
    private void bexpr(int bexprTrue, int bexprFalse) {
        /**
         * A boolean expression is written in prefixed form, with the structure
         * Relational operator expression expression
         * Arithmetic expressions can be nested into boolean ones.
         * In order to be valid, a source code cannot feature a list of
         * boolean expressions, and those can only be used as conditions of the
         * while and if construct.
         */
        Token aus = null;
        switch (look.tag) {
            case Tag.RELOP:
                aus = look;
                match(Tag.RELOP);
                expr(); // E1.code
                expr(); // E2.code
                // no need to check if look
                // can be casted to Word
                // since reach this line if and
                // only if we have successfully executed match
                switch (((Word) aus).lexeme) {
                    case "<":
                        code.emit(OpCode.if_icmpge, bexprFalse);
                        break;

                    case ">":
                        code.emit(OpCode.if_icmple, bexprFalse);
                        break;

                    case "==":
                        code.emit(OpCode.if_icmpne, bexprFalse);
                        break;

                    case "<=":
                        code.emit(OpCode.if_icmpgt, bexprFalse);
                        break;

                    case "<>":
                        code.emit(OpCode.if_icmpeq, bexprFalse);
                        break;

                    case ">=":
                        code.emit(OpCode.if_icmplt, bexprFalse);
                        break;

                    default:
                        error("bexpr");
                        break;
                }
                break;

            default:
                error("bexpr");
                break;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Test.lft"; // il percorso del file da leggere

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog(); // procedura associata al simbol iniziale
                               // della grammatica
            System.out.println("Intermediate language generated successfully!!!");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
