package ru.itmo.golovchenko.trapezoidalmaptest;

import java.awt.Point;

import org.testng.Assert;
import org.testng.annotations.Test;

import ru.itmo.golovchenko.trapezoidalmap.EqualsXCoordinatesException;
import ru.itmo.golovchenko.trapezoidalmap.InterspectedLinesException;
import ru.itmo.golovchenko.trapezoidalmap.Line;
import ru.itmo.golovchenko.trapezoidalmap.MapPoint;
import ru.itmo.golovchenko.trapezoidalmap.OverlapingByXException;
import ru.itmo.golovchenko.trapezoidalmap.Trapezoid;
import ru.itmo.golovchenko.trapezoidalmap.TrapezoidalMap;

public class TrapezoidalMapTest {

	@Test
	public void empty() {
		TrapezoidalMap map = new TrapezoidalMap();
		Assert.assertEquals(map.get(10, -90), new Trapezoid(null, null, null, null));
	}
	
	@Test
	public void singleLine() {
		TrapezoidalMap map = new TrapezoidalMap();
		MapPoint a = new MapPoint(0, 0);
		MapPoint b = new MapPoint(2, 0);
		Line line = new Line(a, b);
		map.add(line);
		
		Assert.assertEquals(map.get(0, 0), a);
		Assert.assertEquals(map.get(2, 0), b);
		Assert.assertEquals(map.get(1, 0), line);
		
		Trapezoid left = (Trapezoid)map.get(0, 1);		
		Assert.assertEquals(map.get(-1, 0), left);
		
		Assert.assertEquals(left, new Trapezoid(null, null, null, a));              //left
		Assert.assertEquals(map.get(3, 0), new Trapezoid(null, null, b, null));     //right
		Assert.assertEquals(map.get(1, 1), new Trapezoid(null, line, a, b));        //up
		Assert.assertEquals(map.get(1, -1), new Trapezoid(line, null, a, b));       //down
	}
	
	@Test
	public void lineContinueLine() {
		TrapezoidalMap map = new TrapezoidalMap();
		
		MapPoint a = new MapPoint(0, 0);
		MapPoint b = new MapPoint(2, 0);
		MapPoint c = new MapPoint(4, 0);
		
		Line ab = new Line(a, b);
		Line bc = new Line(b, c);
		
		map.add(ab);
		map.add(bc);
		
		Assert.assertEquals(map.get(3, 1), new Trapezoid(null, bc, b, c));
		Assert.assertEquals(map.get(5, 1), new Trapezoid(null, null, c, null));
		Assert.assertEquals(map.get(1, -1), new Trapezoid(ab, null, a, b));
		Assert.assertEquals(map.get(2, 0), b);
		Assert.assertEquals(map.get(4, 1), map.get(3, 1));
	}
	
	@Test
	public void twoHalfOverlapingLines() {
		TrapezoidalMap map = new TrapezoidalMap();
		
		MapPoint a = new MapPoint(0, 0);
		MapPoint b = new MapPoint(4, 0);
		MapPoint c = new MapPoint(2, 2);
		MapPoint d = new MapPoint(6, 2);
		
		Line ab = new Line(a, b);
		Line cd = new Line(c, d);
		
		map.add(ab);
		map.add(cd);
		
		Assert.assertEquals(map.get(3, 1), new Trapezoid(cd, ab, c, b));
		Assert.assertEquals(map.get(5, 1), new Trapezoid(cd, null, b, d));
		Assert.assertEquals(map.get(4, 3), new Trapezoid(null, cd, c, d));
	}
	
	@Test
	public void oneLineBetweenTouchingTwo() throws InterspectedLinesException, OverlapingByXException, EqualsXCoordinatesException {
		TrapezoidalMap map = new TrapezoidalMap();
		
		MapPoint a = new MapPoint(0, 0);
		MapPoint b = new MapPoint(100, 0);
		MapPoint c = new MapPoint(200, 0);
		MapPoint d = new MapPoint(50, -50);
		MapPoint e = new MapPoint(150, 50);
		
		map.checkAndAdd(new Line(a, b));
		map.checkAndAdd(new Line(b, c));
		map.checkAndAdd(new Line(d, e));
	}
	
	@Test
	public void oneLineAboveTouchingTwo() throws InterspectedLinesException, OverlapingByXException, EqualsXCoordinatesException {
		TrapezoidalMap map = new TrapezoidalMap();
		
		map.checkAndAdd(new Line(new Point(0, 0), new Point(100, 100)));
		
		Line line2 = new Line(new Point(50, 50), new Point(150, 20));
		map.checkAndAdd(line2);
		
		Line line3 = new Line(new Point(30, 120), new Point(300, 120));
		map.checkAndAdd(line3);
	}
		
	@Test
	public void twoCrossingLine() throws OverlapingByXException, EqualsXCoordinatesException {
		boolean exceptionWas = false;
		try {
			TrapezoidalMap map = new TrapezoidalMap();
			
			MapPoint a = new MapPoint(0, 0);
			MapPoint b = new MapPoint(100, 100);
			MapPoint c = new MapPoint(1, 50);
			MapPoint d = new MapPoint(99, 50);
			
			map.checkAndAdd(new Line(a, b));
			map.checkAndAdd(new Line(c, d));
		} catch (InterspectedLinesException exc) {
			exceptionWas = true;
		}
		Assert.assertTrue(exceptionWas);
	}
	
	@Test
	public void oneBetweenTwo() throws OverlapingByXException, EqualsXCoordinatesException, InterspectedLinesException {

		TrapezoidalMap map = new TrapezoidalMap();
		
		map.checkAndAdd(new Line(new Point(20, 20), new Point(30, 20)));
		
		Line line2 = new Line(new Point(10, 10), new Point(40, 10));
		map.checkAndAdd(line2);
		
		Line line3 = new Line(new Point(0, 30), new Point(50, 30));
		map.checkAndAdd(line3);
		Trapezoid t = (Trapezoid)map.get(25, 25);
		Assert.assertNotNull(t.getUpLine());
	}
	
	@Test
	public void oneAboveTwo() throws OverlapingByXException, EqualsXCoordinatesException, InterspectedLinesException {
		TrapezoidalMap map = new TrapezoidalMap();
		
		map.checkAndAdd(new Line(new Point(10, 10), new Point(30, 10)));
		
		Line line2 = new Line(new Point(20, 0), new Point(40, 0));
		map.checkAndAdd(line2);
		
		Line line3 = new Line(new Point(0, 20), new Point(50, 20));
		map.checkAndAdd(line3);
		
		Trapezoid t = (Trapezoid)map.get(15, 15);
		Assert.assertNotNull(t.getUpLine());
	}
}
