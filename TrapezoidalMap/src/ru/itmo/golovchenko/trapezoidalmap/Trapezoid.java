package ru.itmo.golovchenko.trapezoidalmap;

import java.awt.Point;
import java.util.Objects;

public class Trapezoid implements MapNode {
	Line upLine, downLine;
	MapPoint leftPoint, leftPoint2, rightPoint, rightPoint2;
	
	Trapezoid(Line upLine, Line downLine, MapPoint leftPoint, MapPoint leftPoint2, MapPoint rightPoint, MapPoint rightPoint2) {
		this.upLine = upLine;
		this.downLine = downLine;
		this.leftPoint = leftPoint;
		this.leftPoint2 = leftPoint2;
		this.rightPoint = rightPoint;
		this.rightPoint2 = rightPoint2;
	}
	
	public Trapezoid(Line upLine, Line downLine, MapPoint leftPoint, MapPoint rightPoint) {
		this(upLine, downLine, leftPoint, null, rightPoint, null);
	}
	
	public Trapezoid() { }
	
	public MapPoint getLeftPoint() {
		return leftPoint;
	}
	
	public MapPoint getRightPoint() {
		return rightPoint;
	}
	
	public Line getUpLine() {
		return upLine;
	}
	
	public Line getDownLine() {
		return downLine;
	}
	
	public boolean isCrossInside(int x, Line line) {
		Point a = line.left;
		Point b = line.right;
		
		if (getLeftPoint() == null && getRightPoint() == null) {
			return true;
		}
		
		if (getLeftPoint() == null) {
			return x <= getRightPoint().x;
		}
		if (getRightPoint() == null) {
			return x >= getLeftPoint().x;
		}
		
		if (x < getLeftPoint().x || (x > getRightPoint().x))
			return false;
		
		int multipler = b.x - a.x;
		Point intP = new Point(x * multipler, (x - a.x) * b.y + (b.x - x) * b.y);
		
		long upRotate = -1;
		long downRotate = 1;
		if (getDownLine() != null) {
			Point intDownA = new Point(getDownLine().left.x * multipler, getDownLine().left.y * multipler);
			Point intDownB = new Point(getDownLine().right.x * multipler, getDownLine().right.y * multipler);
			downRotate = Line.rotate(intDownA, intDownB, intP);
		}
		if (getUpLine() != null) {
			Point intUpA = new Point(getUpLine().left.x * multipler, getUpLine().left.y * multipler);
			Point intUpB = new Point(getUpLine().right.x * multipler, getUpLine().right.y * multipler);
			upRotate = Line.rotate(intUpA, intUpB, intP);
		}
		
		return  (upRotate <= 0 && downRotate >= 0) || (upRotate >= 0 && downRotate <= 0);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Trapezoid) {
			Trapezoid trapezoid = (Trapezoid) obj;
			return Objects.equals(getLeftPoint(), trapezoid.getLeftPoint()) && Objects.equals(getRightPoint(), trapezoid.getRightPoint())
			           && Objects.equals(upLine, trapezoid.upLine) && Objects.equals(downLine, trapezoid.downLine);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (leftPoint != null ? leftPoint.hashCode() : 0)
				+ (upLine != null ? 359 * upLine.hashCode() : 0)
				+ (downLine != null ? 1597 * downLine.hashCode() : 0)
				+ (rightPoint != null ? 125383 * rightPoint.hashCode() : 0);
	}
	
	@Override
	public String toString() {
		return "trapezoid {" + (getUpLine() == null ? "" : "up " + getUpLine())
				+ (getDownLine() == null ? "" : " down " + getDownLine())
				+ (getLeftPoint() == null ? "" : " left: " + getLeftPoint())
				+ (getRightPoint() == null ? "" : " right: " + getRightPoint());
	}
	
	MapPoint getLeftLineSecondary() {
		return leftPoint2;
	}
	
	MapPoint getRightLineSecondary() {
		return rightPoint2;
	}
}