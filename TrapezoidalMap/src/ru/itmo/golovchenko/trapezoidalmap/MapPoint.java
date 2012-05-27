package ru.itmo.golovchenko.trapezoidalmap;

import java.awt.Point;

@SuppressWarnings("serial")
public class MapPoint extends Point implements MapNode {
	public TreeNode upOrRight, down;
	
	public MapPoint(Point point) {
		super(point);
	}

	public MapPoint(int x, int y) {
		super(x, y);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}