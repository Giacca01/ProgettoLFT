public class MatricolaCognome {
    enum States {
        Q0, Q1, Q2, Q3, INVALID
    }

    static boolean scan(String s) {
        int i = 0;
        char symbol;
        int lenS = s.length();
        States stato = States.Q0;

        while (i < lenS && stato != States.INVALID) {
            symbol = s.charAt(i);

            switch (stato) {
            case Q0:
                if (Character.isDigit(symbol) && Character.getNumericValue(symbol) % 2 == 0)
                    stato = States.Q1;
                else if (Character.isDigit(symbol) && Character.getNumericValue(symbol) % 2 != 0)
                    stato = States.Q2;
                else
                    stato = States.INVALID;
                break;

            case Q1:
                if (Character.isDigit(symbol) && Character.getNumericValue(symbol) % 2 == 0)
                    stato = States.Q1;
                else if (Character.isDigit(symbol) && Character.getNumericValue(symbol) % 2 != 0)
                    stato = States.Q2;
                else if (Character.isLetter(symbol) && (symbol >= 'A' && symbol <= 'K'))
                    stato = States.Q3;
                else
                    stato = States.INVALID;
                break;

            case Q2:
                if (Character.isDigit(symbol) && Character.getNumericValue(symbol) % 2 == 0)
                    stato = States.Q1;
                else if (Character.isDigit(symbol) && Character.getNumericValue(symbol) % 2 != 0)
                    stato = States.Q2;
                else if (Character.isLetter(symbol) && (symbol >= 'L' && symbol <= 'Z'))
                    stato = States.Q3;
                else
                    stato = States.INVALID;
                break;

            case Q3:
                if (Character.isLetter(symbol) && (symbol >= 'a' && symbol <= 'z'))
                    stato = States.Q3;
                else
                    stato = States.INVALID;
                break;

            default:
                stato = States.INVALID;
                break;
            }
            i++;
        }

        return stato == States.Q3;
    }

    public static void main(String[] args) {
        // Accepted strings
        System.out.println("***Stringhe da accettare***");
        System.out.println("Input: 2Bianchi ==> " + scan("2Bianchi"));
        System.out.println("Input: 122B ==> " + scan("122B"));
        System.out.println("Input: 123456Bianchi ==> " + scan("123456Bianchi"));
        System.out.println("Input: 654321Rossi ==> " + scan("654321Rossi"));

        // Rejected string
        System.out.println("\n***Stringhe da rifiutare***");
        System.out.println("Input: 654321Bianchi ==> " + scan("654321Bianchi"));
        System.out.println("Input: 123456Rossi ==> " + scan("123456Rossi"));
        System.out.println("Input: 654322 ==> " + scan("654322"));
        System.out.println("Input: Rossi ==> " + scan("Rossi"));
    }
}