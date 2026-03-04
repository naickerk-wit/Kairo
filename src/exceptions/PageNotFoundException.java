package exceptions;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException() { super(); }
    public PageNotFoundException(String msg) { super(msg); }
}
