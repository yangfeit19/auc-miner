package cn.edu.nju.raua.fpmining.closetplus.fptree;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.raua.core.transactions.Item;

public class FPTreeNode {
	
	private Item item;
	private int count;
	private FPTreeNode parent;
	private List<FPTreeNode> children;
	private FPTreeNode sideLinkNext;
	
	public FPTreeNode() {
		this.item = null;
		this.count = 0;
		this.parent = null;
		this.children = new ArrayList<>();
		this.sideLinkNext = null;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getCount() {
		return count;
	}

	public void increaseCount() {
		this.count++;
	}
	
	public FPTreeNode getParent() {
		return parent;
	}

	public void setParent(FPTreeNode parent) {
		this.parent = parent;
	}

	/**
	 * @return A list of child node or empty list.
	 */
	public List<FPTreeNode> getChildren() {
		return children;
	}

	public FPTreeNode getSideLinkNext() {
		return sideLinkNext;
	}

	public void setSideLinkNext(FPTreeNode sideLinkNext) {
		this.sideLinkNext = sideLinkNext;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof FPTreeNode) {
			FPTreeNode is = (FPTreeNode)object;
			return item.equals(is.getItem());
		}
		else if (object instanceof Item){
			return item.equals((Item)object);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return item.hashCode();
	}
}
