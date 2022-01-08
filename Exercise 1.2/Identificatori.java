public class Identificatori {
    // Automaton's states
    enum States {
        Q0, Q1, Q2, INVALID
    };

    static boolean scan(String input){
        int i = 0;
        States state = States.Q0; // initial state
        char symbol; // currently scanned symbol

        // scans the input string
        while (i < input.length() && state != States.INVALID){
            symbol = input.charAt(i); // retrives current symbol
            switch (state) {
                // handles the symbol according to current state and automaton definition
                case Q0:
                    if (Character.isLetter(symbol))
                        state = States.Q2;
                    else if (symbol == '_')
                        state = States.Q1;
                    else
                        state = States.INVALID;
                    break;
            
                case Q1:
                    if (Character.isLetterOrDigit(symbol)) {
                        state = States.Q2;
                    } else if (symbol == '_')
                        state = States.Q1;
                    else
                        state = States.INVALID;
                    break;
                
                case Q2:
                    if (!Character.isLetterOrDigit(symbol) && symbol != '_')
                        state = States.INVALID;
                    break;

                default:
                    state = States.INVALID;
                    break;
            }
            i++;
        }

        return state == States.Q2;
    }

    public static void main(String[] args) {
        // Accepted strings
        System.out.println("***Stringhe da accettare***");
        System.out.println("Input: x ==> " + scan("x"));
        System.out.println("Input: ABC ==> " + scan("ABC"));
        System.out.println("Input: flag1 ==> " + scan("flag1"));
        System.out.println("Input: x2y2 ==> " + scan("x2y2"));
        System.out.println("Input: x_1 ==> " + scan("x_1"));
        System.out.println("Input: lft_lab ==> " + scan("lft_lab"));
        System.out.println("Input: _temp ==> " + scan("_temp"));
        System.out.println("Input: x_1_y_2 ==> " + scan("x_1_y_2"));
        System.out.println("Input: x___ ==> " + scan("x___"));
        System.out.println("Input: __5 ==> " + scan("__5"));
        
        // Rejected strings
        System.out.println("\n***Stringhe da rifiutare***");
        System.out.println("Input: 5 ==> " + scan("5"));
        System.out.println("Input: 221B ==> " + scan("221B"));
        System.out.println("Input: 123 ==> " + scan("123"));
        System.out.println("Input: 9_to_5 ==> " + scan("9_to_5"));
        System.out.println("Input: ___ ==> " + scan("___"));
    }
}
