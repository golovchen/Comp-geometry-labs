package ru.itmo.golovchenko.trapezoidalmap;

import java.awt.Point;

public class Line implements MapNode, Comparable<Point> {
	public final MapPoint left, right;
	
	public Line(MapPoint a, MapPoint b) {
		left = a.x < b.x ? a : b;
		right = a.x < b.x ? b : a;
	}
	
	public Line(Point a, Point b) {
		this(a instanceof MapPoint ? (MapPoint)a : new MapPoint(a), b instanceof MapPoint ? (MapPoint)b : new MapPoint(b));
	}

	public boolean isIntersect(Line line) {
		//TODO: make it correct for unbounded int-values.
		
		if ((line.right.x <= left.x) || (right.x <= line.left.x)) {
			return false;
		}
		
		if (Math.max(line.right.y, line.left.y) <= Math.min(right.y, left.y) || Math.max(right.y, left.y) <= Math.min(line.right.y, line.left.y)) {
			return false;
		}
		
		return differentRotates(this, line) && differentRotates(line, this);
	}
	
	private static boolean differentRotates(Line line1, Line line2) {
		long rotate1 = rotate(line1.left, line1.right, line2.left);
		long rotate2 = rotate(line1.left, line1.right, line2.right);
		return Math.max(rotate1, rotate2) > 0 && Math.min(rotate1, rotate2) < 0
				|| (rotate1 == 0 && rotate2 == 0 && (isBetween(line1.left.x, line2.left.x, line1.right.x) || isBetween(line1.left.x, line2.right.x, line1.right.x)));
	}
	
	/**
	 * @return the rotate of a-b line relative a-c line.
	 */
	static long rotate(Point a, Point b, Point c) {
		long aX = b.x - a.x;
		long aY = b.y - a.y;
		long bX = c.x - a.x;
		long bY = c.y - a.y;
		return aX * bY - aY * bX;
	}
	
	private static boolean isBetween(int left, int value, int right) {
		return left < value && value < right; 
	}
	
	/**
	 * @return 1 if <code>point</code> is upper than line, -1 if below, and 0 elsewhere.
	 */
	public int compareTo(Point point) {
		
		//TODO: make it long
		Point a = new Point(right.x - left.x, right.y - left.y);
		Point b = new Point(point.x - left.x, point.y - left.y);
		long rotate = (long)a.x * b.y - (long)a.y * b.x;
		return rotate > 0 ? -1 : (rotate == 0 ? 0 : 1);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Line) {
			Line line = (Line) obj;
			return line.left.equals(left) && line.right.equals(right);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return left.hashCode() * 61 + right.hashCode();
	}
	
	@Override
	public String toString() {
		return "line {"
				+ (left == null ? "" : left.toString()) + " - "
				+ (right == null ? "" : right.toString());
	}
}