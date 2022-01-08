import java.io.*;
import java.util.*;

public class Lexer {

    enum IdentifiersStates {
        Q0, Q1, Q2, INVALID
    };

    public static int line = 1;
    private char peek = ' ';
    // HashMap that allows to retrive key words' token
    // in constant time ( O(1) )
    private static HashMap<String, Integer> keyWords = new HashMap<>();

    /**
     * Function that reads and returns a character from the given stream
     * 
     * @param br: stream to read characters from
     * @return character read
     */
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    /**
     * Scans the input stream to return the next token
     * 
     * @param br: stream whose characters are to be tokenized
     * @return The next valid token or a null pointer
     */
    public Token lexical_scan(BufferedReader br) {
        Token returnValue = null;
        String aus = "";
        IdentifiersStates idState = IdentifiersStates.Q0;
        int i = 0;
        Character symbol = ' ';

        // formatting, meaningless symbols are discarded
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n') {
                System.out.println("***************************");
                line++;
            }
            // reads next character from input buffer
            readch(br);
        }

        // analyzes the symbol to create corresponding token
        switch (peek) {
        case '!':
            // we use ' ' as a way to request
            // the reading of the next character
            // we can't invoke readch directly because
            // we must first return the current symbol's token
            // (or a null pointer to signal an error)
            // to the caller
            peek = ' ';
            returnValue = Token.not;
            break;

        case '(':
            // Since we're not performing a syntactical
            // analysis we do not need to know the following
            // chacter to recognize the '(' symbol
            peek = ' ';
            returnValue = Token.lpt;
            break;

        case ')':
            peek = ' ';
            returnValue = Token.rpt;
            break;

        case '+':
            peek = ' ';
            returnValue = Token.plus;
            break;

        case '-':
            peek = ' ';
            returnValue = Token.minus;
            break;

        case '*':
            peek = ' ';
            returnValue = Token.mult;
            break;

        case '/':
            readch(br);

            if (peek == '/') {
                // single line comment
                do {
                    readch(br);
                } while ((peek != (char) -1) && (peek != '\n'));

                // Predicato: peek == (char)-1 || peek == '\n'
                // non serve il reset perchè il primo carattere
                // successivo al commento è già contenuto
                // in peek e verrà trattato dalla seguente
                // invocazione di lexical_scan
                // quini niente lettura al ciclo successivo
                // (in pratica, date le condizioni di arresto del ciclo, abbiamo già letto il
                // carattere successivo al commento)
                return lexical_scan(br);

            } else if (peek == '*') {

                // multiline comment
                // non serve l'1.10 perchè il riconoscimento
                // dei token prima e dopo /**/ è fatto da altre
                // parti del lexer
                symbol = ' ';
                do {
                    symbol = peek;
                    readch(br);
                    // Invariante: peek == ultimo carattere letto && symbolo == penultimo carattere
                    // letto
                    if (peek == (char) -1) {
                        System.err.println("Unclosed comment detected");
                        return null;
                    }
                } while (((peek != '/') || (symbol != '*')));

                // serve una lettura ad inizio ciclo successivo
                // perchè l'ultimo carattere letto è il delimitatore
                // di commento
                return lexical_scan(br);
            } else {
                returnValue = Token.div;
            }
            break;

        case ';':
            peek = ' ';
            returnValue = Token.semicolon;
            break;

        case ',':
            peek = ' ';
            returnValue = Token.comma;
            break;

        case '{':
            peek = ' ';
            returnValue = Token.lpg;
            break;

        case '}':
            peek = ' ';
            returnValue = Token.rpg;
            break;

        case '&':
            // we read the next character
            readch(br);
            // and check if it's another &
            if (peek == '&') {
                // if it's the case, we have found
                // an and token and we output it
                peek = ' ';
                returnValue = Word.and;
            } else
                System.err.println("Erroneous character" + " after & : " + peek);

            break;

        case '|':
            readch(br);

            if (peek == '|') {
                peek = ' ';
                returnValue = Word.or;
            } else
                System.err.println("Erroneous character" + " after | : " + peek);

            break;

        case '<':
            readch(br);

            if (peek == '=')
                returnValue = Word.le;
            else if (peek == '>')
                returnValue = Word.ne;
            else {
                returnValue = Word.lt;
            }

            break;

        case '>':
            readch(br);

            if (peek == '=')
                returnValue = Word.ge;
            else {
                returnValue = Word.gt;
            }

            break;

        case '=':
            readch(br);

            if (peek == '=') {
                peek = ' ';
                returnValue = Word.eq;
            } else {
                System.err.println("Erroneous character" + " after = : " + peek);
                returnValue = null;
            }

            break;

        case (char) -1:
            returnValue = new Token(Tag.EOF);
            break;

        default:
            // modificare secondo esercizio 2.2
            // controllare se tutte le keyword rispettano il pattern degli indetificatori
            if (Character.isLetter(peek) || peek == '_') { // neither keyword nor identifiers can start with numers

                aus = Character.toString(peek);
                readch(br);

                while (Character.isLetterOrDigit(peek) || peek == '_') {
                    aus += Character.toString(peek);
                    readch(br);
                }

                // le parole chiave vengono accettate solo se scritte in minuscolo
                if (keyWords.containsKey(aus)) { // keyword
                    returnValue = new Word(keyWords.get(aus), aus);
                } else { // identifier
                    if (isID(aus))
                        returnValue = new Word(Tag.ID, aus);
                    else
                        System.err.println("Invalid identifier. Erroneous character" + " after = : " + aus);
                }

            } else if (Character.isDigit(peek)) {
                // si suppone che 0 non possa essere seguito
                // da altri numeri
                if (peek == '0') {
                    readch(br);
                    if (Character.isDigit(peek))
                        System.err.println("Erroneous character" + " after 0 : " + peek);
                    else
                        returnValue = new NumberTok(Tag.NUM, 0);
                } else {
                    aus = "";
                    do { // same logic used for words
                        aus += peek;
                        readch(br);
                    } while (Character.isDigit(peek)); // we read all the number's digits to form the lexem

                    returnValue = new NumberTok(Tag.NUM, Integer.parseInt(aus));
                }

            } else
                System.err.println("Erroneous character: " + peek);

            break;
        }

        return returnValue;
    }

    public static boolean isID(String aus) { // check if s is ID
        IdentifiersStates idState = IdentifiersStates.Q0;
        int i = 0;
        Character symbol = ' ';
        // check identifier's validity
        while (i < aus.length() && idState != IdentifiersStates.INVALID) {
            symbol = aus.charAt(i);
            switch (idState) {
            case Q0:
                if (Character.isLetter(symbol))
                    idState = IdentifiersStates.Q2;
                else if (symbol == '_')
                    idState = IdentifiersStates.Q1;
                else
                    idState = IdentifiersStates.INVALID;
                break;

            case Q1:
                if (Character.isLetterOrDigit(symbol)) {
                    idState = IdentifiersStates.Q2;
                } else if (symbol == '_')
                    idState = IdentifiersStates.Q1;
                else
                    idState = IdentifiersStates.INVALID;
                break;

            case Q2:
                if (!Character.isLetterOrDigit(symbol) && symbol != '_')
                    idState = IdentifiersStates.INVALID;
                break;

            default:
                idState = IdentifiersStates.INVALID;
                break;
            }
            i++;
        }

        return idState == IdentifiersStates.Q2;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.txt"; // il percorso del file da leggere

        // Open an input stream and prints the lexer's output
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;

            // Initializing the keyword's map
            keyWords.put("assign", Tag.ASSIGN);
            keyWords.put("to", Tag.TO);
            keyWords.put("if", Tag.IF);
            keyWords.put("else", Tag.ELSE);
            keyWords.put("while", Tag.WHILE);
            keyWords.put("begin", Tag.BEGIN);
            keyWords.put("end", Tag.END);
            keyWords.put("print", Tag.PRINT);
            keyWords.put("read", Tag.READ);

            do {
                tok = lex.lexical_scan(br);
                if (tok != null)
                    System.out.println("Scan: " + tok);

            } while (tok != null && tok.tag != Tag.EOF);

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}