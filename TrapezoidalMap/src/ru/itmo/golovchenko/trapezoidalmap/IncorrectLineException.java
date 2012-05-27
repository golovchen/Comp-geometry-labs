package ru.itmo.golovchenko.trapezoidalmap;

@SuppressWarnings("serial")
public abstract class IncorrectLineException extends Exception {
	public final Line newLine;
	
	protected IncorrectLineException(Line newLine) {
		this.newLine = newLine;
	}
}