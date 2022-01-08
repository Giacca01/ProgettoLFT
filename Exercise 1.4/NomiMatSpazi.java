import java.util.HashSet;

public class NomiMatSpazi {
    enum States {
        Q0, Q1, Q2, Q3, Q4, Q5, Q7, INVALID // Invalid = Q6
    }

    public static boolean scan(String s) {
        int i = 0;
        char symbol;
        HashSet<Character> pari = new HashSet<>(); // lookup time O(1)
        HashSet<Character> dispari = new HashSet<>();
        int lenS = s.length();
        States stato = States.Q0;
        HashSet<States> statiAccettanti = new HashSet<>();

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

        statiAccettanti.add(States.Q4);
        statiAccettanti.add(States.Q5);

        while (i < lenS && stato != States.INVALID) {
            symbol = s.charAt(i); // qui distinguiamo tra maiuscole e minuscole
            switch (stato) {
                case Q0:
                    if (symbol == ' ') {
                        stato = States.Q0;
                    } else if (pari.contains(symbol))
                        stato = States.Q1;
                    else if (dispari.contains(symbol))
                        stato = States.Q2;
                    else
                        stato = States.INVALID;
                    break;

                case Q1:
                    if (dispari.contains(symbol))
                        stato = States.Q2;
                    else if (pari.contains(symbol))
                        stato = States.Q1;
                    else if (symbol >= 'A' && symbol <= 'K')
                        stato = States.Q4;
                    else if (symbol == ' ')
                        stato = States.Q3;
                    else
                        stato = States.INVALID;
                    break;

                case Q2:
                    if (dispari.contains(symbol))
                        stato = States.Q2;
                    else if (pari.contains(symbol))
                        stato = States.Q1;
                    else if (symbol >= 'L' && symbol <= 'Z')
                        stato = States.Q4;
                    else if (symbol == ' ')
                        stato = States.Q7;
                    else
                        stato = States.INVALID;
                    break;

                case Q3:
                    if (symbol == ' ')
                        stato = States.Q3;
                    else if (symbol >= 'A' && symbol <= 'K')
                        stato = States.Q4;
                    else
                        stato = States.INVALID;
                    break;

                case Q4:
                    if (symbol == ' ')
                        stato = States.Q5;
                    else if (symbol >= 'a' && symbol <= 'z')
                        stato = States.Q4;
                    else
                        stato = States.INVALID;
                    break;

                case Q7:
                    if (symbol == ' ')
                        stato = States.Q7;
                    else if (symbol >= 'L' && symbol <= 'Z')
                        stato = States.Q4;
                    else
                        stato = States.INVALID;
                    break;

                case Q5:
                    if (symbol >= 'A' && symbol <= 'Z')
                        stato = States.Q4;
                    else if (symbol == ' ')
                        stato = States.Q5;
                    else
                        stato = States.INVALID;
                    break;

                default:
                    stato = States.INVALID;
                    break;
            }
            i++;
        }

        return statiAccettanti.contains(stato);
    }

    public static void main(String[] args) {
        // true
        System.out.println("***Stringhe accettate***");
        System.out.println("Input: 123456 Bianchi ==> " + scan("123456 Bianchi"));
        System.out.println("Input: 654321Rossi    ==> " + scan("654321Rossi   "));
        System.out.println("Input: 654321Michael Super Super Urru ==> " + scan("654321Michael Super Super Urru"));
        /*
            L'automa riconosce qualsiasi numero di blocci del cognome
            (Urru può essere tanto super quanto desiderato :) 
        */
        System.out.println("Input: 654321Michael Super Super Super Super Urru ==> " + scan("654321Michael Super Super Super Super Urru"));
        System.out.println("Input: 2Bianchi ==> " + scan("2Bianchi"));
        System.out.println("Input:   4 Arancione ==> " + scan("  4 Arancione"));
        System.out.println("Input: 123456De Gasperi ==> " + scan("123456De Gasperi"));

        // false
        System.out.println("***Stringhe rifiutate***");
        System.out.println("Input: 654321Bianchi ==> " + scan("654321Bianchi"));
        System.out.println("Input: 123456Rossi ==> " + scan("123456Rossi"));
        System.out.println("Input: 654321Ro  ssi ==> " + scan("654321Ro  ssi"));
        System.out.println("Input: Rossi ==> " + scan("Rossi"));
        System.out.println("Input: 1 B ==> " + scan("1 B"));
        /*
         * Rifiutata perchè si suppone che i vari blocci del cognome debbano
         * iniziare con la maiuscola
         */
        System.out.println("Input: 123456De gasperi ==> " + scan("123456De gasperi"));
        System.out.println("Input: 654321Michael Super Super URRU ==> " + scan("654321Michael Super Super URRU"));
    }
}