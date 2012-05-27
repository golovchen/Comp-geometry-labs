package ru.itmo.golovchenko.trapezoidalmap;

@SuppressWarnings("serial")
public class EqualsXCoordinatesException extends IncorrectLineException {
	public EqualsXCoordinatesException(Line newLine) {
		super(newLine);
	}
}
