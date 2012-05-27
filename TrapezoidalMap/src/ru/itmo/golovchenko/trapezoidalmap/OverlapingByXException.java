package ru.itmo.golovchenko.trapezoidalmap;

@SuppressWarnings("serial")
public class OverlapingByXException extends IncorrectLineException {
	public final Line overlapingLine;

	public OverlapingByXException(Line newLine, Line overpapedLine) {
		super(newLine);
		this.overlapingLine = overpapedLine;
	}

}
