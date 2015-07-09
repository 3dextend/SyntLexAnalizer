/*
 * Класс синтаксического анализатора
 * Рябенький Сергей 2ПЗ-пс
 */

public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _number = 2;
	public static final int _if = 3;
	public static final int _then = 4;
	public static final int _else = 5;
	public static final int maxT = 19;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 1; // if minErrDist = 1 LL1 grammar

	public Token t; // last recognized token
	public Token la; // lookahead token
	int errDist = minErrDist;

	public Scanner scanner;
	public Errors errors;

	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr(int n) {
		if (errDist >= minErrDist)
			errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr(String msg) {
		if (errDist >= minErrDist)
			errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}

	void Get() {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}

	void Expect(int n) {
		if (la.kind == n)
			Get();
		else {
			SynErr(n);
		}
	}

	boolean StartOf(int s) {
		return set[s][la.kind];
	}

	void ExpectWeak(int n, int follow) {
		if (la.kind == n)
			Get();
		else {
			SynErr(n);
			while (!StartOf(follow))
				Get();
		}
	}

	boolean WeakSeparator(int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) {
			Get();
			return true;
		} else if (StartOf(repFol))
			return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}

	void RJ() {
		Expect(6);
		Expect(1);
		while (la.kind == 1 || la.kind == 3) {
			if (la.kind == 1) {
				Stat();
			} else {
				IfStatBlock();
			}
		}
	}

	void Stat() {
		Expect(1);
		Expect(11);
		Expr();
		Expect(12);
	}

	void IfStatBlock() {
		IfPar();
		ExpRel();
		ThElPar();
	}

	void IfPar() {
		Expect(3);
		Expect(7);
		while (la.kind == 3) {
			Get();
			Expect(7);
		}
	}

	void ExpRel() {
		Expect(1);
		RelOp();
		Expr();
	}

	void ThElPar() {
		Expect(8);
		StatThEl();
		while (la.kind == 8) {
			Get();
			StatThEl();
		}
	}

	void StatThEl() {
		Expect(9);
		Expect(4);
		Stat();
		if (la.kind == 5) {
			Get();
			Stat();
		}
		Expect(10);
	}

	void RelOp() {
		if (la.kind == 11) {
			Get();
		} else if (la.kind == 17) {
			Get();
		} else if (la.kind == 18) {
			Get();
		} else
			SynErr(20);
	}

	void Expr() {
		SimExpr();
		if (la.kind == 11 || la.kind == 17 || la.kind == 18) {
			RelOp();
			SimExpr();
		}
	}

	void SimExpr() {
		Term();
		while (la.kind == 13 || la.kind == 14) {
			AddOp();
			Term();
		}
	}

	void Term() {
		Factor();
		while (la.kind == 15 || la.kind == 16) {
			MulOp();
			Factor();
		}
	}

	void AddOp() {
		if (la.kind == 13) {
			Get();
		} else if (la.kind == 14) {
			Get();
		} else
			SynErr(21);
	}

	void Factor() {
		Expect(2);
	}

	void MulOp() {
		if (la.kind == 15) {
			Get();
		} else if (la.kind == 16) {
			Get();
		} else
			SynErr(22);
	}

	public void Parse() {
		la = new Token();
		la.val = "";
		Get();
		RJ();
		Expect(0);

	}

	private static final boolean[][] set = { { T, x, x, x, x, x, x, x, x, x, x,
			x, x, x, x, x, x, x, x, x, x }

	};

	public String ParseErrors() {
		java.io.PrintStream oldStream = System.out;

		java.io.OutputStream out = new java.io.ByteArrayOutputStream();
		java.io.PrintStream newStream = new java.io.PrintStream(out);

		errors.errorStream = newStream;

		Parse();

		String errorStream = out.toString();
		errors.errorStream = oldStream;

		return errorStream;

	}
} // end Parser

class Errors {
	public int count = 0; // number of errors detected
	public java.io.PrintStream errorStream = System.out; // error messages go to
															// this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line,
																// 1=column,
																// 2=text

	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) {
			b.delete(pos, pos + 3);
			b.insert(pos, line);
		}
		pos = b.indexOf("{1}");
		if (pos >= 0) {
			b.delete(pos, pos + 3);
			b.insert(pos, column);
		}
		pos = b.indexOf("{2}");
		if (pos >= 0)
			b.replace(pos, pos + 3, msg);
		errorStream.println(b.toString());
	}

	public void SynErr(int line, int col, int n) {
		String s;
		switch (n) {
		case 0:
			s = "EOF expected";
			break;
		case 1:
			s = "ident expected";
			break;
		case 2:
			s = "number expected";
			break;
		case 3:
			s = "if expected";
			break;
		case 4:
			s = "then expected";
			break;
		case 5:
			s = "else expected";
			break;
		case 6:
			s = "\"Program\" expected";
			break;
		case 7:
			s = "\"(\" expected";
			break;
		case 8:
			s = "\")\" expected";
			break;
		case 9:
			s = "\"{\" expected";
			break;
		case 10:
			s = "\"}\" expected";
			break;
		case 11:
			s = "\"=\" expected";
			break;
		case 12:
			s = "\";\" expected";
			break;
		case 13:
			s = "\"+\" expected";
			break;
		case 14:
			s = "\"-\" expected";
			break;
		case 15:
			s = "\"*\" expected";
			break;
		case 16:
			s = "\"/\" expected";
			break;
		case 17:
			s = "\"<\" expected";
			break;
		case 18:
			s = "\">\" expected";
			break;
		case 19:
			s = "??? expected";
			break;
		case 20:
			s = "invalid RelOp";
			break;
		case 21:
			s = "invalid AddOp";
			break;
		case 22:
			s = "invalid MulOp";
			break;
		default:
			s = "error " + n;
			break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr(int line, int col, String s) {
		printMsg(line, col, s);
		count++;
	}

	public void SemErr(String s) {
		errorStream.println(s);
		count++;
	}

	public void Warning(int line, int col, String s) {
		printMsg(line, col, s);
	}

	public void Warning(String s) {
		errorStream.println(s);
	}
} // Errors

class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;

	public FatalError(String s) {
		super(s);
	}
}
