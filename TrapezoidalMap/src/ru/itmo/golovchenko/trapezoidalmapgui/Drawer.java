package ru.itmo.golovchenko.trapezoidalmapgui;

import ru.itmo.golovchenko.trapezoidalmap.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class Drawer extends JPanel {
	private static final Color ACTIVE_COLOR = Color.GREEN;
	private static final Color HORISONTAL_COLOR = Color.RED;
	private static final Color VERTICAL_COLOR = Color.BLUE;
	
	private Point point = new Point(0, 0);
	private List<ChangeListener> pointChangedListeners = new ArrayList<>();
	private List<ChangeListener> mapNodeChangedListeners = new ArrayList<>();
	private int x = -10;
	private int y = 200;
	private TrapezoidalMap map = null;
	private Point holdPoint  = null;
	private Point newLineStart = null;
	
	public Drawer() {
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				mouseMoved(e);
			}
			
			public void mouseMoved(MouseEvent event) {
				if (holdPoint != null) {
					x -= event.getPoint().x - holdPoint.x;
					y += event.getPoint().y - holdPoint.y;
					holdPoint = event.getPoint();
				}
				setPoint(event.getPoint());
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent eventArg) {
				if (eventArg.getButton() == MouseEvent.BUTTON1) {
					if (newLineStart != null) {
						Line newLine = new Line(newLineStart, virtualCoordinate(eventArg.getPoint()));
						if (map.isAddable(newLine)) {
							map.add(newLine);
						}
					}
					newLineStart = null;
				} else {
					holdPoint = null;
				}
			}
			
			public void mousePressed(MouseEvent eventArg) {
				if (eventArg.getButton() == MouseEvent.BUTTON1) {
					newLineStart = virtualCoordinate(eventArg.getPoint());
				} else {
					holdPoint = eventArg.getPoint();
				}
			}
		});
	}
	
	public void setMap(TrapezoidalMap map) {
		this.map = map;
		for (ChangeListener listener : mapNodeChangedListeners) {
			listener.stateChanged(new ChangeEvent(this));
		}
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (map == null) {
			return;
		}
		MapPoint leftUp = new MapPoint(x, y);
		Point rightUp = virtualCoordinate(new Point(getWidth(), 0));
		Point leftDown = virtualCoordinate(new Point(0, getHeight()));
		MapPoint rightDown = new MapPoint(virtualCoordinate(new Point(getWidth(), getHeight())));
		Trapezoid screen = new Trapezoid(new Line(leftUp, rightUp), new Line(leftDown, rightDown), leftUp, rightDown);
		
		Iterator<Trapezoid> iter = map.iterator();
		while (iter.hasNext()) {
			drawTrapezoid(iter.next(), g, screen, false);
		}

		if (getMousePosition() != null && newLineStart != null) {
			Line newLine = new Line(newLineStart, virtualCoordinate(getMousePosition()));
			if (map.isAddable(newLine)) {
				drawLine(newLine, g, false);
			}
			return;
		}
		
		MapNode node = map.get(point);
		if (node instanceof Trapezoid) {
			drawTrapezoid((Trapezoid)node, g, screen, true);
		} else if (node instanceof Line) {
			drawLine((Line)node, g, true);
		} else {
			drawPoint((Point)node, g);
		}
	}
	
	private void drawLine(Line line, Graphics graphics, boolean active) {
		graphics.setColor(active ? ACTIVE_COLOR : HORISONTAL_COLOR);
		Line realLine = new Line(realCoordinate(line.left), realCoordinate(line.right));
		graphics.drawLine(realLine.left.x, realLine.left.y, realLine.right.x, realLine.right.y);
		drawPoint(line.left, graphics);
		drawPoint(line.right, graphics);
	}
	
	private void drawPoint(Point point, Graphics graphics) {
		graphics.setColor(ACTIVE_COLOR);
		point = realCoordinate(point);
		graphics.fillOval(point.x - 3, point.y - 3, 6, 6);
	}
	
	private void drawTrapezoid(Trapezoid trapezoid, Graphics graphics, Trapezoid screen, boolean active) {
		Point[] points = realPoints(trapezoid, screen);
		if (active) {
			int[] xs = new int[points.length];
			int[] ys = new int[points.length];
			for (int i = 0; i < points.length; i++) {
				xs[i] = points[i].x;
				ys[i] = points[i].y;
			}
			graphics.setColor(ACTIVE_COLOR);
			graphics.fillPolygon(xs, ys, points.length);
		} else {
			graphics.setColor(VERTICAL_COLOR);
			graphics.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
			graphics.drawLine(points[2].x, points[2].y, points[3].x, points[3].y);
			graphics.setColor(HORISONTAL_COLOR);
			graphics.drawLine(points[1].x, points[1].y, points[2].x, points[2].y);
			graphics.drawLine(points[0].x, points[0].y, points[3].x, points[3].y);
		}
	}
	
	private Point[] realPoints(Trapezoid t, Trapezoid screen) {
		Point[] result = new Point[4];
		result[0] = realCoordinate(brutIntersect(t.getUpLine() == null ? screen.getUpLine() : t.getUpLine(), t.getLeftPoint() == null ? screen.getLeftPoint().x : t.getLeftPoint().x));
		result[1] = realCoordinate(brutIntersect(t.getDownLine() == null ? screen.getDownLine() : t.getDownLine(), t.getLeftPoint() == null ? screen.getLeftPoint().x  : t.getLeftPoint().x));
		result[2] = realCoordinate(brutIntersect(t.getDownLine() == null ? screen.getDownLine() : t.getDownLine(), t.getRightPoint() == null ? screen.getRightPoint().x : t.getRightPoint().x));
		result[3] = realCoordinate(brutIntersect(t.getUpLine() == null ? screen.getUpLine(): t.getUpLine(), t.getRightPoint() == null ? screen.getRightPoint().x : t.getRightPoint().x));
		return result;
	}
	
	private static Point brutIntersect(Line line, int x) {
		return new Point(x, ((x - line.left.x) * line.right.y + (line.right.x - x) * line.left.y) / (line.right.x - line.left.x));
	}
	
	private Point realCoordinate(Point p) {
		return new Point(p.x - x, y - p.y);
	}
	
	private Point virtualCoordinate(Point p) {
		return new Point(p.x + x, y - p.y);
	}
	
	private static boolean isIntersect(Trapezoid a, Trapezoid b) {
		//TODO: fix intersection check.
		return isOneInAnother(a, b) || isOneInAnother(b, a);
	}
	
	private static boolean isOneInAnother(Trapezoid one, Trapezoid another) {
		return ((one.getLeftPoint() == null || one.getUpLine() == null) ? false : another.isCrossInside(one.getLeftPoint().x, one.getUpLine()))
				|| ((one.getLeftPoint() == null || one.getDownLine() == null) ? false : another.isCrossInside(one.getLeftPoint().x, one.getDownLine()))
				|| ((one.getRightPoint() == null || one.getUpLine() == null) ? false : another.isCrossInside(one.getRightPoint().x, one.getUpLine()))
				|| ((one.getRightPoint() == null || one.getDownLine() == null) ? false : another.isCrossInside(one.getRightPoint().x, one.getDownLine()));
	}
	
	public void addPointChangedListener(ChangeListener listener) {
		pointChangedListeners.add(listener);
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setPoint(Point newPoint) {
		point = virtualCoordinate(newPoint);
		for (ChangeListener listener : pointChangedListeners) {
			listener.stateChanged(new ChangeEvent(this));
		}
		for (ChangeListener listener : mapNodeChangedListeners) {
			listener.stateChanged(new ChangeEvent(this));
		}
		repaint();
	}
	
	public void addMapNodeChangedListener(ChangeListener listener) {
		mapNodeChangedListeners.add(listener);
	}
	
	public String getMapNode() {
		return map == null ? "" : map.get(point).toString();
	}
}
