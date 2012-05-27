package ru.itmo.golovchenko.trapezoidalmap;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class TrapezoidIterator implements Iterator<Trapezoid> {
	private long version;
	private TrapezoidalMap map;
	private Stack<TreeNode> stack = new Stack<>();
	
	public TrapezoidIterator(TrapezoidalMap map) {
		this.map = map;
		version = map.version;

	}
	
	public boolean hasNext() {
		checkVersion();
		if (stack.isEmpty()) {
			return true;
		}
		
		@SuppressWarnings("unchecked")
		Stack<TreeNode> clone = (Stack<TreeNode>)stack.clone();
		while (!clone.isEmpty()) {
			TreeNode current = clone.pop();
			if (clone.isEmpty()) {
				return false;
			} else if (clone.peek().right != null && clone.peek().right != current) {
				return true;
			}
		}
		return false;
	}

	public Trapezoid next() throws ConcurrentModificationException, NoSuchElementException {
		checkVersion();
		boolean goUp = true;
		TreeNode last = null;
		if (stack.isEmpty()) {
			stack.push(map.root);
			goUp = false;
		}
		for (;;) {
			if (goUp) {
				if (stack.isEmpty()) {
					throw new NoSuchElementException();
				}
				if (stack.peek().right != null && stack.peek().right != last) {
					stack.push(stack.peek().right);
					goUp = false;
				} else {
					last = stack.pop();
				}
			} else {
				if (stack.peek().mapNode instanceof Trapezoid) {
					return (Trapezoid)stack.peek().mapNode;
				} else {
					stack.push(stack.peek().left);
				}
			}
		}
	}
	
	private void checkVersion() throws ConcurrentModificationException {
		if (map.version != version)
			throw new ConcurrentModificationException();
	}

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
