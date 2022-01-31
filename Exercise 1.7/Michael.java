import java.util.HashSet;

public class Automa {
    // Automaton's states
    enum States {
        Q0, Q1, Q2, Q3, Q4, Q5,
        Q6, Q7, Q8, Q9, Q10, Q11,
        Q12, Q13, Q14, Q15, Q16,
        Q17, Q18, Q19, Q20, INVALID
    }

    static boolean scan(String s) {
        int i = 0;
        char symbol; // currently scanned symbol
        int lenS = s.length();
        States stato = States.Q0; // initial state
        // HashSet is used for a cleaner code
        // this solution consumes more memory
        // but offers a constant lookup time (O(1))
        HashSet<States> statiFinali = new HashSet<>();

        // HashSet for final states
        statiFinali.add(States.Q7);
        statiFinali.add(States.Q14);
        statiFinali.add(States.Q20);

        while (i < lenS && stato != States.INVALID) {
            symbol = s.charAt(i); // retrives current symbol
            switch (stato) {
                case Q0:
                    if (symbol == 'M')
                        stato = States.Q1;
                    else if (symbol >= 'A' && symbol <= 'Z')
                        stato = States.Q8;
                    else
                        stato = States.INVALID;
                    break;

                case Q1:
                    if (symbol == 'i')
                        stato = States.Q2;
                    else
                        stato = States.Q9;
                    break;

                case Q2:
                    if (symbol == 'c')
                        stato = States.Q3;
                    else
                        stato = States.Q10;
                    break;

                case Q3:
                    if (symbol == 'h')
                        stato = States.Q4;
                    else
                        stato = States.Q11;
                    break;

                case Q4:
                    if (symbol == 'a')
                        stato = States.Q5;
                    else
                        stato = States.Q12;
                    break;

                case Q5:
                    if (symbol == 'e')
                        stato = States.Q6;
                    else
                        stato = States.Q13;
                    break;

                case Q6:
                    if (symbol == 'l')
                        stato = States.Q7;
                    else
                        stato = States.Q14;
                    break;

                case Q8:
                    if (symbol == 'i')
                        stato = States.Q15;
                    else
                        stato = States.INVALID;
                    break;

                case Q9:
                    if (symbol == 'c')
                        stato = States.Q16;
                    else
                        stato = States.INVALID;
                    break;

                case Q10:
                    if (symbol == 'h')
                        stato = States.Q17;
                    else
                        stato = States.INVALID;
                    break;

                case Q11:
                    if (symbol == 'a')
                        stato = States.Q18;
                    else
                        stato = States.INVALID;
                    break;

                case Q12:
                    if (symbol == 'e')
                        stato = States.Q19;
                    else
                        stato = States.INVALID;
                    break;

                case Q13:
                    if (symbol == 'l')
                        stato = States.Q20;
                    else
                        stato = States.INVALID;
                    break;

                case Q15:
                    if (symbol == 'c')
                        stato = States.Q16;
                    else
                        stato = States.INVALID;
                    break;

                case Q16:
                    if (symbol == 'h')
                        stato = States.Q17;
                    else
                        stato = States.INVALID;
                    break;

                case Q17:
                    if (symbol == 'a')
                        stato = States.Q18;
                    else
                        stato = States.INVALID;
                    break;

                case Q18:
                    if (symbol == 'e')
                        stato = States.Q19;
                    else
                        stato = States.INVALID;
                    break;

                case Q19:
                    if (symbol == 'l')
                        stato = States.Q20;
                    else
                        stato = States.INVALID;
                    break;

                default:
                    stato = States.INVALID;
                    break;
            }
            i++;
        }

        return statiFinali.contains(stato);
    }

    public static void main(String[] args) {
        // true
        System.out.println("Input: Michael ==> " + scan("Michael"));
        System.out.println("Input: Aichael ==> " + scan("Aichael"));
        System.out.println("Input: MichaKl ==> " + scan("MichaKl"));
        System.out.println("Input: Michaek ==> " + scan("Michaek"));
        System.out.println("Input: Michaal ==> " + scan("Michaal"));
        System.out.println("Input: Mic%ael ==> " + scan("Mic%ael"));
        System.out.println("Input: Mich.el ==> " + scan("Mich.el"));
        System.out.println("Input: M*chael ==> " + scan("M*chael"));
        // false
        System.out.println("***************************************");
        System.out.println("Input: Antonio ==> " + scan("Antonio"));
        System.out.println("Input: Aighael ==> " + scan("Aighael"));
        System.out.println("Input: M*ch+el ==> " + scan("M*ch+el"));
        System.out.println("Input: Licsael ==> " + scan("Licsael"));
        System.out.println("Input: MICHAEL ==> " + scan("MICHAEL"));
    }
}
