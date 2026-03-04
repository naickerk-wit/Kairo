package exceptions;

public class InvalidDateException extends RuntimeException{
	public InvalidDateException() { super(); }
	public InvalidDateException(String msg) { super(msg); }
}
