import java.io.*;
/**
 * CONTROLLARE LA SEGNALAZIONE DEGLI ERRORI
 */
// importo solo gli elementi del lexer necessari
import lexer.*;

/*
    ATTENZIONE!!!
    Quella usata qui non è l'ultima versione del lexer
*/

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    // per ora non possiamo mettere nel file test
    // commenti o segni di punteggiatura
    // perchè non fanno parte della 
    // grammatica di riferimento
    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    // legge il prossimo token della sequenza
    // di input
    void move() {
        // look = prossimo simbolo della stringa di input
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        // Ricordare che il simbolo di input è un token
        // quindi i confronti si fanno in base al tag
        // perchè ci interessiamo della struttura
        // non del reale valore
        if (look.tag == t) {
            // avanziamo se non siamo a fine stringa
            if (look.tag != Tag.EOF) // Tag.EOF
                move();
        } else
            error("syntax error");
    }

    public void prog() {
        /*
         * We define the production associated to <prog> by considering that a program
         * in a certain programming language consists of a list of statements (<statlist>)
         * followed by the end of file
         */
        // procedura associata a <prog> -> <statlist>EOF
        int labelNext;

        switch (look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
            case '{':
                // qui le label sono espresse come offset rispetto
                // all'indirizzo virtuale 0 di inizio del programma
                // in sostanza non hanno un nome simbolico
                // quindi non serve aggiungere alla simbol table
                // perchè il loro valore è già l'offset
                labelNext = code.newLabel(); // crea la label
                // l'idea è quella di indicare al primo blocco di codice
                // l'etichetta del blocco successivo
                statlist(labelNext);
                // aggiunge la label al codice intermedio
                /**
                 * Ricordare che una label va emessa appena prima delle valutazione
                 * del terminale che produrrà il blocco a cui essa è riferita
                 */
                // etichetta che rappresenta l'inizio del blocco di codice (inteso come statements list successivo)
                code.emitLabel(labelNext); // in teoria servirebbe solo per produrre un unico codice intermedio
                                            // per più sorgenti (cioè richimando prog in un ciclo)
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
        /**
         * We than consider that a list of statements can be seen as a single statement
         * followed by another list of statements. Thus, we define the production
         * <statlist> -> <stat><statlitsp>
         */
        // procedura associata a <statlist> -> <stat><statlitsp>
        int labelNext;

        switch (look.tag) {
            case '{':
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.WHILE:
            case Tag.IF:
                labelNext = code.newLabel();
                stat(labelNext);
                // ricordare che questa label rappresenta il blocco di codice a cui saltiamo
                // dopo aver valutato stat, quindi va appesa dopo il codice necessario
                // per valutare stat
                // label next è riferita al blocco generato valutando statlistp
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
                // è riferita al blocco di codice rappresentatato da statlistp
                code.emitLabel(labelNext);
                statlistp(next);
                break;
            
            case Tag.EOF:
            case '}':
                // <statlistp> -> epsilon
                // non serve fare match(')')
                // e lo capiamo dal fatto che il corpo sia epsilon
                //Non serve perchè a controllare la presenza della tonda
                // saranno le produzioni il cui corpo
                // include statlistp
                // saltiamo allo statement successivo
                code.emit(OpCode.GOto, next); // si può togliere
                                                // perchè il blocco a cui si salta
                                                // è quello immediatamente successivo
                break;

            default:
                error("statlistp");
                break;
        }
    }

    private void stat(int next) {
        /**
         * In the lft programming language, a statement can be composed of either
         * keywords (see Language.pdf), structured with a certain syntax (see the grammar table)
         * defined by the first five productions.
         * A block of statements can be enclosed in a pair of brackets (e.g. if used as body of while or if)
         */
        int bexprTrue, statNext, bexprFalse = -1;

        switch (look.tag) {
            case Tag.ASSIGN:
                // <stat> -> assign<expr>to<idlist>
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                // we need to duplicate the word on top of the stack
                // to assign it to multiple variables, because JVM
                // doesn't support multiple assignment natively
                idlist(next, 0);
                break;
            
            case Tag.PRINT:
                // stampa tutti i valori delle espressioni in exprlist
                // <stat> -> print(<exprlist>)
                match(Tag.PRINT);
                match('(');
                // we need to invoke the print methods multiple times
                // to print multiple values, because JVM
                // doesn't support list printing natively
                exprlist(next, 2);
                match(')');
                // dobbiamo metterlo qui, perchè la sintassi JVM
                // prevede di caricare i parametri prima di effettuare 
                // la chiamata a metodo
                //code.emit(OpCode.invokestatic, 1);
                code.emit(OpCode.GOto, next); // si può togliere: punta al blocco successivo
                break;
            
            case Tag.READ:
                // <stat> -> read(<idlist>)
                match(Tag.READ);
                // attenzione!!!
                // read e ( sono terminali DIVERSI
                // nell'insieme guida consideriamo solo read
                // e poi verifichiamo che sia seguito da (
                // nel case associato
                // l'invocazione a read va messa prima dell'operando
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
                code.emitLabel(statNext); // non si può togliere: punta ad un blocco non consecutivo
                bexpr(bexprTrue, next);
                match(')');
                code.emitLabel(bexprTrue); // in teoria si può togliere
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
                code.emitLabel(bexprTrue); // non si può togliere: punta ad un blocco non consecutivo
                stat(next);
                code.emitLabel(bexprFalse);// in teoria si può togliere
                statp(next);
                break;  

            case '{':
                // es. While() {...}
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
         * In our language, it can be followed by a block of conde, enclosed in brackets, by
         * the ELSE keyword or by an empty body (first production).
         * The latter is also used to mark the end of a non-empty list of statements that follows if.
         */
        switch (look.tag) {
            case Tag.END:
                // <statp> -> end
                match(Tag.END);
                code.emit(OpCode.GOto, next); //punta al blocco successivo
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
                    // prima usa count e poi lo incremento
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

    private void idlistp(int next, int type){
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
                // with this code we emit all the identifiers
                // of a list that do follow the first one
                // lo mettiamo prima di idlistp
                // per evitare che la word in cima allo stack venga consumata
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
                code.emit(OpCode.GOto, next); //si può togliere: corrisponde al codice per assign e read che non alterano il 
                // flusso di esecuzione
                break;
        
            default:
                error("idlistp");
                break;
        }
    }

    private void expr(){
        /**
         * These productions define the structure of valid arithmetic expression
         * written in prefixed form, that can be a sum/subtrction/multiplication/division
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
                exprlist(exprlistNext, 0); // lascia sullo stack il risultato della somma
                                        // tra i risultati delle espressioni in exprlist
                match(')');
                // emettiamo (i.e. scriviamo l'opcode sulla
                // lista che costituisce il sorgente) solo
                // dopo aver verificato il corretto utilizzo
                // del simbolo di somma e SOPRATTUTTO DOPO AVER VALUTATO GLI OPERANDI
                // (ricordare che JVM usa l'RPN)
                //code.emit(OpCode.iadd);
                break;
            
            case '-':
                // <expr> -> - <expr><expr>
                match('-');
                expr();
                expr();
                // sottrazione tra il risultato delle due espressioni
                code.emit(OpCode.isub);
                break;
            
            case '*':
                // <expr> ->*(<exprlist>)
                exprlistNext = code.newLabel();
                
                match('*');
                match('(');
                code.emitLabel(exprlistNext);
                exprlist(exprlistNext, 1);// lascia sullo stack il risultato della moltiplicazione
                                       // tra i risultati delle espressioni in exprlist
                match(')');
                //code.emit(OpCode.imul);
                break;

            case '/':
                // <expr> ->/<expr><expr>
                match('/');
                expr();
                expr();
                // divisione tra il risultato delle due espressioni
                code.emit(OpCode.idiv);
                break;
            
            case Tag.NUM:
                // <expr> -> NUM
                // necessary because, in case of corrispondence, match
                // orders the lexer to produce another token
                // and we would therefore the needed lexeme
                aus = look;
                match(Tag.NUM);
                // We don't need to check the object's type
                // because if match returns with no error
                // then we are sure that the current token
                // represents a number
                code.emit(OpCode.ldc, ((NumberTok) aus).lexem);
                break;
            
            case Tag.ID:
                // <expr> -> ID
                aus = look;
                match(Tag.ID);
                // il simbolo va aggiunto prima o dopo il match???
                // In teoria dopo, così in caso di errore sintattico
                // il simbolo "errato" non viene aggiunto
                // e così non ci occorre un istance of, perchè
                // siamo sicuri che look sia di tipo word
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

    private void exprlist(int next, int typeOp){
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
                // <exprlist> -> <expr><exprlistp>
                // the assembly for the single expression
                // is generated by invoking expr
                // iadd ed imul vanno applicate su due operandi
                // qui valutiamo il primo
                expr(); // produce un intero, risultato della valutazione di expr
                // per stampare il valore di tutte le espressioni di exprlistp
                // ci occorre una print dopo la valutazione di ogni espressione
                if (typeOp == 2) 
                    code.emit(OpCode.invokestatic, 1); // stampa il risultato
                                                        // dell'espressione i-esima
                exprlistp(next, typeOp); // produce un intero, risultato della somma o della moltiplicazione
                                // dei risultati delle espressioni in exprlistp (ipotesi induttiva)
                /*
                if (typeOp == 0) // sum
                    code.emit(OpCode.iadd); // passo induttivo nel caso della somma
                else if (typeOp == 1) // multiplication
                    code.emit(OpCode.imul);// passo induttivo nel caso della moltiplicazione*/
                break;
        
            default:
                error("exprlist");
                break;
        }
    }

    private void exprlistp(int next, int typeOp){
        switch (look.tag) {
            case ',':
                // <exprlistp> -> ,<expr><exprlistp>
                match(',');
                // we don't need to generate assembly code
                // for the comma since it just marks the beginning
                // of a new expression which code will be generated by invoking expr
                expr();
                // a questo punto siamo sicuri di aver valutato
                // il secondo operando
                if (typeOp == 2)
                    code.emit(OpCode.invokestatic, 1);
                else if (typeOp == 0)
                    code.emit(OpCode.iadd);
                else
                    code.emit(OpCode.imul);
                exprlistp(next, typeOp);
                
                break;

            case ')':
                // match(')'); NO, c'è già in expr()
                // <exprlist> -> epsilon
                //code.emit(OpCode.GOto, next); //Goto irraggiungibile
                break;

            default:
                error("exprlistp");
                break;
        }
    }

    private void bexpr(int bexprTrue, int bexprFalse){
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
                switch (((Word)aus).lexeme) {
                    case "<": 
                        code.emit(OpCode.if_icmplt, bexprTrue);  
                        break;
                    
                    case ">":
                        code.emit(OpCode.if_icmpgt, bexprTrue);
                        break;
                
                    case "==":
                        code.emit(OpCode.if_icmpeq, bexprTrue);
                        break;

                    case "<=":
                        code.emit(OpCode.if_icmple, bexprTrue);
                        break;

                    case "<>":
                        code.emit(OpCode.if_icmpne, bexprTrue);
                        break;

                    case ">=":
                        code.emit(OpCode.if_icmpgt, bexprTrue);
                        break;
                        
                    default:
                        // it will (or at least, should) be executed
                        error("bexpr");
                        break;
                }
                // non si può togliere: if e while alterano il flusso
                code.emit(OpCode.GOto, bexprFalse);
                break;
        
            default:
                error("bexpr");
                break;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "esempio_semplice.lft"; // il percorso del file da leggere

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