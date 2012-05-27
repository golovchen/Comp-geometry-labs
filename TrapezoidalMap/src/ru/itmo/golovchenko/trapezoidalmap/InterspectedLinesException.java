package ru.itmo.golovchenko.trapezoidalmap;

@SuppressWarnings("serial")
public class InterspectedLinesException extends IncorrectLineException {
	public final Line existingLine;
	
	public InterspectedLinesException(Line newLine, Line existingLine) {
		super(newLine);
		this.existingLine = existingLine;
	}
}
