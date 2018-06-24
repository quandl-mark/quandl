package capital.one.stock.exceptions;

public class BadRequestException extends Exception {

	public BadRequestException(String msg) {
		super(msg);
	}

	public BadRequestException(Exception e) {
		super(e);
	}
}
