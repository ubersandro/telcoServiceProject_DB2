package exceptions;

public class WrongCredentialsException extends Exception {
	private static final long serialVersionUID = -8497609760648061569L;

	public WrongCredentialsException() {}

	public WrongCredentialsException(String message) {
		super(message);
	}

	
}
