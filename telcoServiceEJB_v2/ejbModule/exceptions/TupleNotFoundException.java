package exceptions;

public class TupleNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;

	public TupleNotFoundException() {}

	public TupleNotFoundException(String message) {
		super(message);
	}
	
}
