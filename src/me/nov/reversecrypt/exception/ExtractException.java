package me.nov.reversecrypt.exception;

public class ExtractException extends RuntimeException {

	public ExtractException(String string) {
		super(string);
	}

	public ExtractException(Throwable t) {
		super(t);
	}

}
