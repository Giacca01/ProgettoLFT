public class StudentiT2T3 {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (ch >= '0' && ch <= '9') {
                        if (ch % 2 == 0)
                            state = 1;
                        else
                            state = 6;
                    } else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
                        state = 8;
                    else
                        state = -1;
                    break;

                case 1:
                    if (ch >= '0' && ch <= '9') {
                        if (ch % 2 == 0)
                            state = 2;
                        else
                            state = 3;
                    } else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
                        state = 8;
                    else
                        state = -1;
                    break;

                case 2:
                    if (ch >= '0' && ch <= '9') {
                        if (ch % 2 == 0)
                            state = 2;
                        else
                            state = 3;
                    } else if (ch >= 'A' && ch <= 'K')
                        state = 7;
                    else if ((ch >= 'L' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
                        state = 8;
                    else
                        state = -1;
                    break;

                case 3:
                    if (ch >= '0' && ch <= '9') {
                        if (ch % 2 == 0)
                            state = 4;
                        else
                            state = 5;
                    } else if (ch >= 'A' && ch <= 'K')
                        state = 7;
                    else if ((ch >= 'L' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
                        state = 8;
                    else
                        state = -1;
                    break;

                case 4:
                    if (ch >= '0' && ch <= '9') {
                        if (ch % 2 == 0)
                            state = 2;
                        else
                            state = 3;
                    } else if (ch >= 'L' && ch <= 'Z')
                        state = 7;
                    else if ((ch >= 'A' && ch <= 'K') || (ch >= 'a' && ch <= 'z'))
                        state = 8;
                    else
                        state = -1;
                    break;

                case 5:
                    if (ch >= '0' && ch <= '9') {
                        if (ch % 2 == 0)
                            state = 4;
                        else
                            state = 5;
                    } else if (ch >= 'L' && ch <= 'Z')
                        state = 7;
                    else if ((ch >= 'A' && ch <= 'K') || (ch >= 'a' && ch <= 'z'))
                        state = 9;
                    else
                        state = -1;
                    break;

                case 6:
                    if (ch >= '0' && ch <= '9') {
                        if (ch % 2 == 0)
                            state = 4;
                        else
                            state = 5;
                    } else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
                        state = 8;
                    else
                        state = -1;
                    break;

                case 7:
                    if (ch >= '0' && ch <= '9')
                        state = 8;
                    else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
                        state = 7;
                    else
                        state = -1;
                    break;

                case 8:
                    if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
                        state = 8;
                    else
                        state = -1;
                    break;
            }
        }

        return state == 7;
    }

    public static void main(String[] args) {
        System.out.println("***Stringhe da accettare***");
        System.out.println("Input: 654321Bianchi ==> " + scan("654321Bianchi"));
        System.out.println("Input: 123456Rossi ==> " + scan("123456Rossi"));
        System.out.println("Input: 221B ==> " + scan("221B"));
        System.out.println("Input: 944320Giacardi ==> " + scan("944320Giacardi"));
        System.out.println("Input: 944320Fontana ==> " + scan("944320Fontana"));
        System.out.println("Input: 11Verdi ==> " + scan("11Verdi"));

        System.out.println();

        System.out.println("***Stringhe da rifiutare***");
        System.out.println("Input: 123456Bianchi ==> " + scan("123456Bianchi"));
        System.out.println("Input: 654321Rossi ==> " + scan("654321Rossi"));
        System.out.println("Input: 5 ==> " + scan("5"));
        System.out.println("Input: 654322 ==> " + scan("654322"));
        System.out.println("Input: Rossi ==> " + scan("Rossi"));
        System.out.println("Input: 2Bianchi ==> " + scan("2Bianchi"));
        System.out.println("Input: 944310Giacardi ==> " + scan("944310Giacardi"));
        System.out.println("Input: 944320Urru ==> " + scan("944320Urru"));
        System.out.println("Input: 1Verdi ==> " + scan("1Verdi"));
    }
}