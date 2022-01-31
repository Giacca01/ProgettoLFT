
/**
 * MatricolaNome
 */
import java.util.HashSet;

public class Main {
    enum States {
        Q0, Q1, Q2, Q3, Q4, Q5,
        Q6, Q7, Q9, Q10, Q11,
        Q12, Q13, Q14, Q15, Q16,
        INVALID
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
                    if (symbol == 'F')
                        stato = States.Q1;
                    else if (symbol >= 'A' && symbol <= 'Z')
                        stato = States.Q9;
                    else
                        stato = States.INVALID;
                    break;

                case Q1:
                    if (symbol == 'e')
                        stato = States.Q2;
                    else
                        stato = States.Q10;
                    break;

                case Q2:
                    if (symbol == 'd')
                        stato = States.Q3;
                    else
                        stato = States.Q11;
                    break;

                case Q3:
                    if (symbol == 'e')
                        stato = States.Q4;
                    else
                        stato = States.Q12;
                    break;

                case Q4:
                    if (symbol == 'r')
                        stato = States.Q5;
                    else
                        stato = States.Q13;
                    break;

                case Q5:
                    if (symbol == 'i')
                        stato = States.Q6;
                    else
                        stato = States.Q14;
                    break;

                case Q6:
                    if (symbol == 'c')
                        stato = States.Q7;
                    else
                        stato = States.Q15;
                    break;

                case Q7:
                    stato = States.Q16;
                    break;

                case Q9:
                    if (symbol == 'e')
                        stato = States.Q10;
                    else
                        stato = States.INVALID;
                    break;

                case Q10:
                    if (symbol == 'd')
                        stato = States.Q11;
                    else
                        stato = States.INVALID;
                    break;

                case Q11:
                    if (symbol == 'e')
                        stato = States.Q12;
                    else
                        stato = States.INVALID;
                    break;

                case Q12:
                    if (symbol == 'r')
                        stato = States.Q13;
                    else
                        stato = States.INVALID;
                    break;

                case Q13:
                    if (symbol == 'i')
                        stato = States.Q14;
                    else
                        stato = States.INVALID;
                    break;

                case Q14:
                    if (symbol == 'c')
                        stato = States.Q15;
                    else
                        stato = States.INVALID;
                    break;

                case Q15:
                    if (symbol == 'o')
                        stato = States.Q16;
                    else
                        stato = States.INVALID;
                    break;

                default:
                    stato = States.INVALID;
                    break;
            }
            i++;
        }

        return stato == States.Q16;
    }

    public static void main(String[] args) {
        // true
        System.out.println("Input: Federico ==> " + scan("Federico"));
        System.out.println("Input: Aederico ==> " + scan("Aederico"));
        System.out.println("Input: FederKco ==> " + scan("FederKco"));
        System.out.println("Input: Federica ==> " + scan("Federica"));
        System.out.println("Input: Federrco ==> " + scan("Federrco"));
        System.out.println("Input: Fede%ico ==> " + scan("Fede%ico"));
        System.out.println("Input: Feder.co ==> " + scan("Feder.co"));
        System.out.println("Input: Fe*erico ==> " + scan("Fe*erico"));
        // false
        System.out.println("***************************************");
        System.out.println("Input: Antonio ==> " + scan("Antonio"));
        System.out.println("Input: Rtderico ==> " + scan("Rtderico"));
        System.out.println("Input: F*ed+erico ==> " + scan("F*ed+erico"));
        System.out.println("Input: MedeKico ==> " + scan("MedeKico"));
        System.out.println("Input: FEDERICO ==> " + scan("FEDERICO"));
        System.out.println("Input: Fekerjco ==> " + scan("Fekerjco"));
    }
}