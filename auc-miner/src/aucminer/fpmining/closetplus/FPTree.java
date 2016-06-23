package aucminer.fpmining.closetplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import aucminer.core.ITransactionProvider;
import aucminer.core.Item;
import aucminer.core.Transaction;

public class FPTree {
	
	//To assist the physical FP-tree projection
	private HeaderTable headerTable;
	
	private FPTreeNode root;
	
	public FPTree(ITransactionProvider provider, int min_sup) {
		if (provider == null) {
			throw new IllegalArgumentException("Argument 'provider' cannot be null.");
		}
		buildFPTree(provider, min_sup);
	}
	
	public HeaderTable getHeaderTable() {
		return headerTable;
	}

	public FPTreeNode getRoot() {
		return root;
	}

	private void buildFPTree(ITransactionProvider provider, int min_sup) {
		Map<Item, ItemWithSupport> f_map = getGobalFrequentItems(provider, min_sup);
		
		root = new FPTreeNode();
		
		provider.resetDataSource();
		while (provider.hasNext()) {
			
			Transaction transaction = provider.getTransaction();

			List<ItemWithSupport> f_in_trans_list = new ArrayList<>();
			for (Item item : transaction.getItemset().getItems()) {
				if (f_map.containsKey(item)) {
					f_in_trans_list.add(f_map.get(item));
				}
			}
			Collections.sort(f_in_trans_list);
			
			if (f_in_trans_list.size() > 0) {
				insert_tree(root, f_in_trans_list);
			}
		}
	}
	
	private void insert_tree(FPTreeNode parent, List<ItemWithSupport> remainingItemsInPath) {
		if (remainingItemsInPath.size() > 0) {
			ItemWithSupport firstItem = remainingItemsInPath.get(0);
			
			FPTreeNode node = null;
			
			List<FPTreeNode> children = parent.getChildren();
			if (children.contains(firstItem.getItem())) {
				node = children.get(children.indexOf(firstItem.getItem()));
			}
			else {
				node = new FPTreeNode();
				node.setItem(firstItem.getItem());
				node.setParent(parent);
				children.add(node);
				
				FPTreeNode sideLink = null;
				if ((sideLink = headerTable.getSideLink(firstItem.getItem())) == null) {
					HeaderTableItem tItem = new HeaderTableItem(firstItem.getItem(), firstItem.getSupport());
					tItem.setSideLinkPointer(node);
					headerTable.insertTableItem(tItem);
				}
				else {
					while(sideLink.getSideLinkNext() != null) {
						sideLink = sideLink.getSideLinkNext();
					}
					sideLink.setSideLinkNext(node);
				}
			}
			
			node.increaseCount();
			
			remainingItemsInPath.remove(0);
			insert_tree(node, remainingItemsInPath);
		}
	}
	
	private Map<Item, ItemWithSupport> getGobalFrequentItems(ITransactionProvider provider, int min_sup) {
		Map<Item, ItemWithSupport> itemMap = new HashMap<Item, ItemWithSupport>();
		
		provider.resetDataSource();
		while (provider.hasNext()) {
			Transaction transaction = provider.getTransaction();
			for (Item item : transaction.getItemset().getItems()) {
				if (itemMap.containsKey(item)) {
					itemMap.get(item).increaseSupport();
				}
				else {
					itemMap.put(item, new ItemWithSupport(item));
				}
			}
		}
		
		Iterator<Map.Entry<Item, ItemWithSupport>> it = itemMap.entrySet().iterator(); 
		while(it.hasNext()){ 
			Map.Entry<Item, ItemWithSupport> entry = it.next(); 
		    if (entry.getValue().getSupport() < min_sup) 
		    	it.remove(); 
		}
		
	    return itemMap;
	}

	
	private class ItemWithSupport implements Comparable<ItemWithSupport> {
		
		private Item item;
		private int support;
		
		public ItemWithSupport(Item item) {
			if (item == null) {
				throw new IllegalArgumentException("Argument 'item' cannot be null.");
			}
			this.item = item;
			this.support = 1;
		}
		
		public void increaseSupport() {
			this.support++;
		}
		
		public Item getItem() {
			return item;
		}

		public int getSupport() {
			return support;
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof ItemWithSupport) {
				ItemWithSupport is = (ItemWithSupport)object;
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

		@Override
		public int compareTo(ItemWithSupport arg0) {
			return arg0.getSupport() - support;
		}
	}
}
