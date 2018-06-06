package nz.riff.builder.parsers;

public class ChordParsingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ChordParsingException(String message) {
		super(message);
	}

	public ChordParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
