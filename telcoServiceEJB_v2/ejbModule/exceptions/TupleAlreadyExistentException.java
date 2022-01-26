package exceptions;

public class TupleAlreadyExistentException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TupleAlreadyExistentException() {}

	public TupleAlreadyExistentException(String message) {
		super(message); 
	}

}
