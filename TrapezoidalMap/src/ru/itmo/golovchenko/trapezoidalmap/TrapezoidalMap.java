package ru.itmo.golovchenko.trapezoidalmap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TrapezoidalMap implements Iterable<Trapezoid> {
	long version = 0;
	TreeNode root = new TreeNode(new Trapezoid());
	
	private Map<Integer, Line> leftXs = new HashMap<>();
	private Map<Integer, Line> rightXs = new HashMap<>();
	private Map<Point, TreeNode> points = new HashMap<>();
	private Collection<Line> lines = new ArrayList<>();
	private TreeNode lastUpTrapezoid, lastDownTrapezoid;
	
	public boolean isAddable(Line line) {
		if (line.left.x == line.right.x) {
			return false;
		}
		
		if (leftXs.containsKey(line.left.x) || rightXs.containsKey(line.right.x)) {
			return false;
		}
		
		Iterator<Line> iter = lines.iterator();
		while (iter.hasNext()) {
			if (iter.next().isIntersect(line)) {
				return false;
			}
		}
		return true;
	}
	
	public void checkAndAdd(Line line) throws InterspectedLinesException, OverlapingByXException, EqualsXCoordinatesException {
		if (line.left.x == line.right.x) {
			throw new EqualsXCoordinatesException(line);
		} else if (leftXs.containsKey(line.left.x)) {
			throw new OverlapingByXException(line, leftXs.get(line.left.x));
		} else if (rightXs.containsKey(line.right.x)) {
			throw new OverlapingByXException(line, rightXs.get(line.right.x));			
		} else {
			Iterator<Line> iter = lines.iterator();
			while (iter.hasNext()) {
				Line next = iter.next();
				if (next.isIntersect(line)) {
					throw new InterspectedLinesException(line, next); 
				}
			}
			add(line);
		}
	}
	
	public void add(Line line) {
		lines.add(line);
		
		leftXs.put(line.left.x, line);
		rightXs.put(line.right.x, line);
		
		TreeNode left = get(line.left, line.right);
		TreeNode right = get(line.right, line.left);
		
		lastUpTrapezoid = null;
		lastDownTrapezoid  = null;
		boolean isStart = true;
		
		for (;;) {
			boolean isFinish = isNeighbors(left.mapNode, right.mapNode);
			
			boolean leftSplit = isStart && left.mapNode instanceof Trapezoid;
			if (leftSplit || (isFinish && right.mapNode instanceof Trapezoid)) {
				//need to split by point
				
				if (left.equals(right)) {
					if (leftSplit) {
						right = splitTrapezoidByPoint(leftSplit ? left : right, line, leftSplit);
					} else {
						left = splitTrapezoidByPoint(leftSplit ? left : right, line, leftSplit);
					}
				} else {
					splitTrapezoidByPoint(leftSplit ? left : right, line, leftSplit);
				}
			} else {
				//need to split by line
				
				TreeNode next = isFinish ? null : nextTrapezoid((Trapezoid)(left.mapNode instanceof MapPoint ? ((MapPoint)left.mapNode).upOrRight : left).mapNode, line);				
				splitTrapezoidByLine(left.mapNode instanceof MapPoint ? ((MapPoint)left.mapNode).upOrRight : left, line);
				if (isFinish) {
					break;
				} else {
					left = next;
				}
				isStart = false;
			}
		}
		version++;
	}
	
	public MapNode get(int x, int y) {
		return get(new Point(x, y));
	}
	
	public MapNode get(Point position) {
		return get(position, null).mapNode;
	}
	
	private static void addPointToMap(TreeNode potentialPoint, Map<Point, TreeNode> map) {
		if (potentialPoint.mapNode instanceof MapPoint && !map.containsKey(potentialPoint.mapNode)) {
			map.put((Point)potentialPoint.mapNode, potentialPoint);
		}
	}
	
	private static TreeNode nextTrapezoid(Trapezoid current, Line line) {
		MapPoint rightPoint = current.getRightPoint();
		return rightPoint.down == null || !isPointAboveLine(line, rightPoint, current.getRightLineSecondary())
				? rightPoint.upOrRight : rightPoint.down;
	}
		
	/**
	 * @param trapezoid will changed to tree node with the point inside
	 * @return new <code>TreeNode</code> with old trapezoid inside
	 */
	private TreeNode splitTrapezoidByPoint(TreeNode trapezoid, Line line, boolean isLeft) {
		Trapezoid oldTrapezoid = (Trapezoid)trapezoid.mapNode;
		
		//create new trapezoids
		TreeNode linedTrapezoid = new TreeNode(isLeft
				? new Trapezoid(oldTrapezoid.upLine, oldTrapezoid.downLine, line.left, line.right, oldTrapezoid.getRightPoint(), oldTrapezoid.getRightLineSecondary())
				: new Trapezoid(oldTrapezoid.upLine, oldTrapezoid.downLine, oldTrapezoid.getLeftPoint(), oldTrapezoid.getLeftLineSecondary(), line.right, line.left));
		TreeNode sideTrapezoid = new TreeNode(isLeft
				? new Trapezoid(oldTrapezoid.upLine, oldTrapezoid.downLine, oldTrapezoid.getLeftPoint(), oldTrapezoid.getLeftLineSecondary(), line.left, line.right)
				: new Trapezoid(oldTrapezoid.upLine, oldTrapezoid.downLine, line.right, line.left, oldTrapezoid.getRightPoint(), oldTrapezoid.getRightLineSecondary()));
		
		//update left point's link
		if (oldTrapezoid.getLeftPoint() != null) {
			if (oldTrapezoid.getLeftPoint().upOrRight.mapNode.equals(oldTrapezoid)) {
				oldTrapezoid.getLeftPoint().upOrRight = isLeft ? sideTrapezoid : linedTrapezoid;
			} else if (oldTrapezoid.getLeftPoint().down.mapNode.equals(oldTrapezoid)) {
				oldTrapezoid.getLeftPoint().upOrRight = isLeft ? sideTrapezoid : linedTrapezoid;
			}
		}
		
		//update middle point's link
		(isLeft ? line.left : line.right).upOrRight = (isLeft ? linedTrapezoid : sideTrapezoid);
		
		//add trapezoids in the tree
		trapezoid.mapNode = isLeft ? line.left : line.right;
		trapezoid.left = isLeft ? sideTrapezoid : linedTrapezoid;
		trapezoid.right = isLeft ? linedTrapezoid : sideTrapezoid;
		
		addPointToMap(trapezoid, points);
		
		return isLeft ? trapezoid.right : trapezoid.left;
	}
	
	/**
	 * @param trapezoid will changed to tree node with the line inside
	 */
	private void splitTrapezoidByLine(TreeNode trapezoid, Line line) {
		Trapezoid oldTrapezoid = (Trapezoid)trapezoid.mapNode;
		
		//create new trapezoids
		TreeNode upTrapezoidNode = lastUpTrapezoid != null ? lastUpTrapezoid : new TreeNode(new Trapezoid(oldTrapezoid.upLine, line, oldTrapezoid.getLeftPoint(), oldTrapezoid.getLeftLineSecondary(), null, null));
		TreeNode downTrapezoidNode = lastDownTrapezoid != null ? lastDownTrapezoid : new TreeNode(new Trapezoid(line, oldTrapezoid.downLine, oldTrapezoid.getLeftPoint(), oldTrapezoid.getLeftLineSecondary(), null, null));
		
		//correct right points.
		Trapezoid upTrapezoid = (Trapezoid)upTrapezoidNode.mapNode;
		upTrapezoid.rightPoint = oldTrapezoid.getRightPoint();
		upTrapezoid.rightPoint2 = oldTrapezoid.getRightLineSecondary();
		Trapezoid downTrapezoid = (Trapezoid)downTrapezoidNode.mapNode;
		downTrapezoid.rightPoint = oldTrapezoid.getRightPoint();
		downTrapezoid.rightPoint2 = oldTrapezoid.getRightLineSecondary();
		
		//add trapezoids in the tree
		trapezoid.mapNode = line;
		trapezoid.left = downTrapezoidNode;
		trapezoid.right = upTrapezoidNode;

		//update last trapezoids
		if (isPointAboveLine(line, oldTrapezoid.getRightPoint(), oldTrapezoid.getRightLineSecondary())) {
			lastUpTrapezoid = null;
			lastDownTrapezoid = downTrapezoidNode; 
		} else {
			lastUpTrapezoid = upTrapezoidNode;
			lastDownTrapezoid = null;
		}
		
		if (oldTrapezoid.rightPoint != null) {
			if (oldTrapezoid.upLine != null && oldTrapezoid.upLine.right.equals(oldTrapezoid.rightPoint) && line.compareTo(oldTrapezoid.upLine.right) == 0) {
				lastUpTrapezoid = null;
			}
			if (oldTrapezoid.downLine != null && oldTrapezoid.downLine.equals(oldTrapezoid.rightPoint) && line.compareTo(oldTrapezoid.downLine.right) == 0) {
				lastDownTrapezoid = null;
			}
		}
		
		//update left point's link
		updatePointLinks(oldTrapezoid.getLeftPoint(), oldTrapezoid.getLeftLineSecondary(), line, upTrapezoidNode, downTrapezoidNode);
	}
	
	private static void updatePointLinks(MapPoint point, MapPoint point2, Line line, TreeNode up, TreeNode down) {
		if (line.left.equals(point)) {
			point.upOrRight = up;
			point.down = down;
		} else {
			TreeNode value = isPointAboveLine(line, point, point2) ? up : down;
			if (point.down == null) {
				point.upOrRight = value;
			} else {
				switch (line.compareTo(point)) {
				case 1 :
					point.upOrRight = down;
					break;
				case -1 :
					point.down = up;
					break;
				case 0 :
					if (line.compareTo(point2) < 0) {
						point.down = down;
					} else {
						point.upOrRight = up;
					}
				}
			}
			
			
//			switch (line.compareTo(point)) {
//			case 1 :
//				point.down = up;
//				break;
//			case -1 :
//				point.upOrRight = down;
//				break;
//			case 0 :
//				if (((Trapezoid)down.mapNode).downLine.left.equals(point)) {
//					point.upOrRight = up;
//				} else {
//					point.down = down;
//				}
//			}
		}
	}
	
	private static boolean isPointAboveLine(Line line, Point point, Point secondaryPoint) {
		int compared = line.compareTo(point);
		return compared == 0 ? (line.compareTo(secondaryPoint) < 0) : (compared == 1);
	}
	
	private TreeNode get(Point position, Point secondaryPosition) {
		TreeNode potentialPoint = points.get(position);
		if (potentialPoint != null) {
			return potentialPoint;
		}
		
		TreeNode node = root;
		while (!(node.mapNode instanceof Trapezoid)) {
			if (node.mapNode instanceof MapPoint) {
				MapPoint point = (MapPoint) node.mapNode;
				if (position.x <= point.x) {
					node = node.left;
				} else {
					node = node.right;
				}
			} else if (node.mapNode instanceof Line) {
				Line line = (Line) node.mapNode;
				switch (line.compareTo(position)) {
					case 1 :
						node = node.right;
						break;
					case -1 :
						node = node.left;
						break;
					case 0 :
						if (secondaryPosition == null) {
							return node;
						} else {
							node = line.compareTo(secondaryPosition) == 1 ? node.right : node.left;
						}
				}
			}
		}
		return node;
	}
	
	private static boolean isNeighbors(MapNode left, MapNode right) {
		
		//TODO:shortly
		if (left instanceof Trapezoid && right instanceof Trapezoid) {
			return ((Trapezoid)left).equals(right);
		} else if (left instanceof Trapezoid && right instanceof MapPoint) {
			return ((Trapezoid)left).getRightPoint().equals(right);
		} else if (left instanceof MapPoint && right instanceof Trapezoid) {
			return ((Trapezoid)right).getLeftPoint().equals(left);
		} else if (left instanceof MapPoint && right instanceof MapPoint) {
			MapPoint point = (MapPoint)left;
			return (point.upOrRight == null ? false : ((Trapezoid)point.upOrRight.mapNode).getRightPoint().equals(right))
			         || (point.down == null ? false : ((Trapezoid) point.down.mapNode).getRightPoint().equals(right));
		} else {
			throw new IllegalArgumentException("Both of arguments must be Trapezoid or MapPoint.");
		}
	}

	public Iterator<Trapezoid> iterator() {
		return new TrapezoidIterator(this);
	}
}
