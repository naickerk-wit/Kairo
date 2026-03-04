package exceptions;

public class InvalidNameException extends RuntimeException {
	public InvalidNameException() { super(); }
	public InvalidNameException(String msg) { super(msg); }
}
