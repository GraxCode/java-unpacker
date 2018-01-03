package me.nov.reversecrypt.exception;

public class ExtractException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ExtractException(String string) {
    super(string);
  }

  public ExtractException(Throwable t) {
    super(t);
  }

}
