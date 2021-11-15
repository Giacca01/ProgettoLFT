public class MatricolaCognomeT {
    enum States {
        Q0, Q1, Q2, Q3, Q4, Q5, Q6, INVALID
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
                if (Character.isDigit(symbol))
                    stato = States.Q3;
                else
                    stato = States.INVALID;
                break;

            case Q2:
                if (Character.isDigit(symbol))
                    stato = States.Q4;
                else
                    stato = States.INVALID;
                break;

            case Q3:
                if (Character.isLetter(symbol) && (symbol >= 'A' && symbol <= 'K'))
                    stato = States.Q5;
                else if (Character.isDigit(symbol))
                    stato = States.Q3;
                else
                    stato = States.INVALID;
                break;

            case Q4:
                if (Character.isLetter(symbol) && (symbol >= 'L' && symbol <= 'Z'))
                    stato = States.Q5;
                else if (Character.isDigit(symbol))
                    stato = States.Q4;
                else
                    stato = States.INVALID;
                break;

            case Q5:
                if (Character.isLetter(symbol) && (Character.isLowerCase(symbol)))
                    stato = States.Q5;
                else if (symbol == ' ')
                    stato = States.Q6;
                else
                    stato = States.INVALID;
                break;

            case Q6:
                if (Character.isLetter(symbol) && (Character.isUpperCase(symbol)))
                    stato = States.Q5;
                break;

            default:
                stato = States.INVALID;
                break;
            }
            i++;
        }

        return stato == States.Q5;
    }

    public static void main(String[] args) {
        // Accepted strings
        System.out.println("***Stringhe da accettare***");
        System.out.println("Input: 654321Bianchi ==> " + scan("654321Bianchi"));
        System.out.println("Input: 123456Rossi ==> " + scan("123456Rossi"));
        System.out.println("Input: 221B ==> " + scan("221B"));

        // Rejected string
        System.out.println("\n***Stringhe da rifiutare***");
        System.out.println("Input: 123456Bianchi ==> " + scan("123456Bianchi"));
        System.out.println("Input: 654321Rossi ==> " + scan("654321Rossi"));
        System.out.println("Input: 5 ==> " + scan("5"));
        System.out.println("Input: 654322 ==> " + scan("654322"));
        System.out.println("Input: Rossi ==> " + scan("Rossi"));
        System.out.println("Input: 2Bianchi ==> " + scan("2Bianchi"));
    }
}
