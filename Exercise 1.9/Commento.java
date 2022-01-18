public class Commento
{
	public static boolean scan (String s) {
		int state = 0;
		int i = 0;
		
		while(state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			
			switch(state) {
				case 0:
					if(ch == '/')
						state = 1;
					else if(ch == '*' || ch == 'a')
						state = 5;
					else
						state = -1;
					break;
					
				case 1:
					if(ch == '*')
						state = 2;
					else if(ch == '/' || ch == 'a')
						state = 5;
					else
						state = -1;
					break;
					
				case 2:
					if(ch == '*')
						state = 3;
					else if(ch == '/' || ch == 'a')
						state = 2;
					else
						state = -1;
					break;
					
				case 3:
					if(ch == '/')
						state = 4;
					else if(ch == '*')
						state = 3;
					else if(ch == 'a')
						state = 2;
					else
						state = -1;
					break;
					
				case 4:
					if(ch == '/' || ch == '*' || ch == 'a')
						state = 5;
					else
						state = -1;
					break;
					
				case 5:
					if(ch == '/' || ch == '*' || ch == 'a')
						state = 5;
					else
						state = -1;
					break;
			}
		}
		
		return state == 4;
	}
	
	public static void main (String[] args) {
		System.out.println("***Stringhe da accettare***");
		System.out.println("Input: /****/ ==> " + scan("/****/"));
		System.out.println("Input: /*a*a*/ ==> " + scan("/*a*a*/"));
		System.out.println("Input: /*a/**/ ==> " + scan("/*a/**/"));
		System.out.println("Input: /**a///a/a**/ ==> " + scan("/**a///a/a**/"));
		System.out.println("Input: /**/ ==> " + scan("/**/"));
		System.out.println("Input: /*/*/ ==> " + scan("/*/*/"));
		System.out.println("Input: /**a*a*a*a*a*a*a*a***a*/ ==> " + scan("/**a*a*a*a*a*a*a*a***a*/"));
		
		
		System.out.println();
		
		System.out.println("***Stringhe da rifiutare***");
		System.out.println("Input: /* ==> " + scan("/*"));
		System.out.println("Input: */ ==> " + scan("*/"));
		System.out.println("Input: /*/ ==> " + scan("/*/"));
		System.out.println("Input: /**/***/ ==> " + scan("/**/***/"));
		System.out.println("Input: *** ==> " + scan("***"));
	}
}