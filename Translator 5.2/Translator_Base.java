import java.io.*;
import lexer.*;

public class Translator_Base {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator_Base(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() { 
	// come in Esercizio 3.1
    }

    void error(String s) { 
	// come in Esercizio 3.1
    }

    void match(int t) {
	// come in Esercizio 3.1
    }

    public void prog() {        
	// ... completare ...
        int lnext_prog = code.newLabel();
       // statlist(lnext_prog);
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
        	code.toJasmin();
        }
        catch(java.io.IOException e) {
        	System.out.println("IO error\n");
        };
	// ... completare ...
    }

    public void stat( /* completare */ ) {
        switch(look.tag) {
	// ... completare ...
            case Tag.READ:
                match(Tag.READ);
                match('(');
	        idlist(/* completare */);
                match(')');
	// ... completare ...
        }
     }

    private void idlist(/* completare */) {
        switch(look.tag) {
	    case Tag.ID:
	    	// aggiunge un nuovo identificatore alla symbol table
        	int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
	// ... completare ...
    	}
    }

    private void expr( /* completare */ ) {
        switch(look.tag) {
	// ... completare ...
            case '-':
                match('-');
                expr();
                expr();
                // anche se la notazione è quella prefissa
                // il codice assembly va generato in modo
                // da rispettare l'utilizzo della RPN
                // di JVM, caricando quindi sullo stack
                // gli operandi prima dell'operatore
                code.emit(OpCode.isub);
                break;
	// ... completare ...
        }
    }

// ... completare ...
}

