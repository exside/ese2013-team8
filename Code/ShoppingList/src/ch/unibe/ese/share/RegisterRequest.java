package ch.unibe.ese.share;

/**
 * Some trivial implementation of the Request class
 * It just forwards the Phonenumber, which is already implemented in Register
 * 
 * @author Stephan
 *
 */
public class RegisterRequest extends Request {
	
	private static final long serialVersionUID = 8085524766542717980L;

	public RegisterRequest(String phoneNumber) {
		super(phoneNumber);
	}

	@Override
	public int getType() {
		return Request.REGISTER_REQUEST;
	}

	@Override
	public String toString() {
		if(this.wasSuccessful()) 
			return "Registered on the Server";
		if(this.isHandled())
			return "You already have an account";
		else 
			return "Request wasn't handled";
	}

}
