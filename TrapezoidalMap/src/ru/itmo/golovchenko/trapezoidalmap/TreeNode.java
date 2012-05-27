package ru.itmo.golovchenko.trapezoidalmap;

class TreeNode {
	public TreeNode left, right;
	public MapNode mapNode;
	
	public TreeNode(MapNode mapNode) {
		this.mapNode = mapNode;
	}
	
	public TreeNode(MapNode mapNode, TreeNode left, TreeNode right) {
		this(mapNode);
		this.left = left;
		this.right = right;
	}
}
