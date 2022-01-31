import java.util.HashSet;

/**
 * MatricolaNome
 */

public class Main {
    enum States {
        Q0, Q1, Q2, Q3, Q4, Q5, 
        Q6, Q7, Q8, Q10, INVALID
    }

    static boolean scan(String s) {
        int i = 0;
        char symbol;
        int lenS = s.length();
        States stato = States.Q0;
        // Since this automaton has many final states
        // we use an Hash Set to store them
        // this solution has a worse memory consumption
        // than a simple inline or
        // but allows us to have a cleaner code
        HashSet<States> statiFinali = new HashSet<>(); 

        statiFinali.add(States.Q2);
        statiFinali.add(States.Q4);
        statiFinali.add(States.Q6);
        statiFinali.add(States.Q10);

        while (i < lenS && stato != States.INVALID) {
            symbol = s.charAt(i);

            switch (stato) {
                case Q0:
                    if (symbol == '+' || symbol == '-')
                        stato = States.Q1;
                    else if (Character.isDigit(symbol))
                        stato = States.Q2;
                    else if (symbol == '.')
                        stato = States.Q3;
                    else
                        stato = States.INVALID;
                    break;

                case Q1:
                    if (symbol == '.')
                        stato = States.Q3;
                    else if (Character.isDigit(symbol))
                        stato = States.Q2;
                    else
                        stato = States.INVALID;
                    break;

                case Q2:
                    if (Character.isDigit(symbol))
                        stato = States.Q2;
                    else if (symbol == '.')
                        stato = States.Q3;
                    else if (symbol == 'e')
                        stato = States.Q5;
                    else
                        stato = States.INVALID;
                    break;

                case Q3:
                    if (Character.isDigit(symbol))
                        stato = States.Q4;
                    else
                        stato = States.INVALID;
                    break;

                case Q4:
                    if (Character.isDigit(symbol))
                        stato = States.Q4;
                    else if (symbol == 'e')
                        stato = States.Q5;
                    else
                        stato = States.INVALID;
                    break;

                case Q5:
                    if (Character.isDigit(symbol))
                        stato = States.Q6;
                    else if (symbol == '.')
                        stato = States.Q8;
                    else if (symbol == '+' || symbol == '-')
                        stato = States.Q7;
                    else
                        stato = States.INVALID;
                    break;

                case Q6:
                    if (symbol == '.')
                        stato = States.Q8;
                    else if (Character.isDigit(symbol))
                        stato = States.Q6;
                    else
                        stato = States.INVALID;
                    break;

                case Q7:
                    if (Character.isDigit(symbol))
                        stato = States.Q6;
                    else if (symbol == '.')
                        stato = States.Q8;
                    else
                        stato = States.INVALID;
                    break;

                case Q8:
                    if (Character.isDigit(symbol))
                        stato = States.Q10;
                    else
                        stato = States.INVALID;
                    break;
                
                case Q10:
                    if (Character.isDigit(symbol)) {
                        stato = States.Q10;
                    } else {
                        stato = States.INVALID;
                    }

                default:
                    stato = States.INVALID;
                    break;
            }

            i++;
        }

        return statiFinali.contains(stato);
    }

    public static void main(String[] args) {
        // Accepted strings
        System.out.println("***Stringhe da rifiutare***");
        System.out.println("Input: 123 ==> " + scan("123"));
        System.out.println("Input: 123.5 ==> " + scan("123.5"));
        System.out.println("Input: .567 ==> " + scan(".567"));
        System.out.println("Input: +7.5 ==> " + scan("+7.5"));
        System.out.println("Input: -.7 ==> " + scan("-.7"));
        System.out.println("Input: 67e10 ==> " + scan("67e10"));
        System.out.println("Input: 1e-2 ==> " + scan("1e-2"));
        System.out.println("Input: -.7e2 ==> " + scan("-.7e2"));
        System.out.println("Input: 1e2.3 ==> " + scan("1e2.3"));
        System.out.println("Input: -1.5e+2.3 ==> " + scan("1e2.3"));
        System.out.println("Input: -44.88e.7 ==> " + scan("-44.88e.7"));

        // Rejected strings
        System.out.println("\n***Stringhe da rifiutare***");
        System.out.println("Input: . ==> " + scan("."));
        System.out.println("Input: e3 ==> " + scan("e3"));
        System.out.println("Input: 123. ==> " + scan("123."));
        System.out.println("Input: 123e ==> " + scan("123."));
        System.out.println("Input: +e6 ==> " + scan("+e6"));
        System.out.println("Input: 1.2.3 ==> " + scan("1.2.3"));
        System.out.println("Input: 4e5e6 ==> " + scan("4e5e6"));
        System.out.println("Input: 4e5.5.6 ==> " + scan("4e5.5.6"));
        System.out.println("Input: ++3 ==> " + scan("++3"));
    }
}