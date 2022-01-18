package lexer;

public class Tag {
	// List of possible tag values
	// for the language's tokens
	public final static int EOF = -1,
			NUM = 256,
			ID = 257,
			RELOP = 258,
			ASSIGN = 259,
			TO = 260,
			IF = 261,
			ELSE = 262,
			WHILE = 263,
			BEGIN = 264,
			END = 265,
			PRINT = 266,
			READ = 267,
			OR = 268,
			AND = 269,
			COMMENT = 270,
			NOT = 33;
}
