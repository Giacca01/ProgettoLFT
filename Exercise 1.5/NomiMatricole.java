import java.util.HashSet;

public class NomiMatricole {
    enum States {
        Q0, Q1, Q2, Q3, Q4, INVALID
    }

    static boolean scan(String s) {
        int i = 0;
        char symbol;
        // HashSet is used for a cleaner code
        // this solution consumes more memory
        // but offers a constant lookup time (O(1))
        HashSet<Character> pari = new HashSet<>();
        HashSet<Character> dispari = new HashSet<>();
        int lenS = s.length();
        States stato = States.Q0;

        pari.add('0');
        pari.add('2');
        pari.add('4');
        pari.add('6');
        pari.add('8');

        dispari.add('1');
        dispari.add('3');
        dispari.add('5');
        dispari.add('7');
        dispari.add('9');

        while (i < lenS && stato != States.INVALID) {
            symbol = s.charAt(i);
            
            switch (stato) {
                case Q0:
                    if (symbol >= 'A' && symbol <= 'K')
                        stato = States.Q1;
                    else if (symbol >= 'L' && symbol <= 'Z')
                        stato = States.Q2;
                    else
                        stato = States.INVALID;
                    break;

                case Q1:
                    if (dispari.contains(symbol))
                        stato = States.Q4;
                    else if (Character.isLetter(symbol)) {
                        stato = States.Q1;
                    } else if (pari.contains(symbol))
                        stato = States.Q3;
                    else
                        stato = States.INVALID;
                    break;

                case Q2:
                    if (pari.contains(symbol))
                        stato = States.Q4;
                    else if (Character.isLetter(symbol)) {
                        stato = States.Q2;
                    } else if (dispari.contains(symbol)) {
                        stato = States.Q3;
                    } else
                        stato = States.INVALID;
                    break;

                case Q3:
                    if (pari.contains(symbol))
                        stato = States.Q3;
                    else if (dispari.contains(symbol))
                        stato = States.Q4;
                    else
                        stato = States.INVALID;
                    break;

                case Q4:
                    if (dispari.contains(symbol))
                        stato = States.Q4;
                    else if (pari.contains(symbol))
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

        return stato == States.Q3 || stato == States.Q4;
    }

    public static void main(String[] args) {
        // Accepted strings
        System.out.println("***Stringhe da accettare***");
        System.out.println("Input: Bianchi123456 ==> " + scan("Bianchi123456"));
        System.out.println("Input: Rossi654321 ==> " + scan("Rossi654321"));
        System.out.println("Input: Bianchi2 ==> " + scan("Bianchi2"));
        System.out.println("Input: B122 ==> " + scan("B122"));

        // Rejected string
        System.out.println("\n***Stringhe da rifiutare***");
        System.out.println("Input: 654321Bianchi ==> " + scan("654321Bianchi"));
        System.out.println("Input: Rossi 123456 ==> " + scan("Rossi 123456"));
        System.out.println("Input: 654322 ==> " + scan("654322"));
        System.out.println("Input: Rossi ==> " + scan("Rossi"));
        System.out.println("Input: rOssi654321 ==> " + scan("rOssi654321"));
    }
}