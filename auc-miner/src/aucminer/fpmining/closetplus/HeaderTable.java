package aucminer.fpmining.closetplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import aucminer.core.Item;

public class HeaderTable {
	
	//Maintain items order according to descending support-counting.
	private List<HeaderTableItem> tableItemList;
	
	//For performance purpose.
	private Map<Item, HeaderTableItem> tableItemMap;
	
	public HeaderTable() {
		tableItemList = new ArrayList<>();
	}
	
	public void insertTableItem(HeaderTableItem tItem) {
		if (tItem != null) {
			int insertIndex = 0;
			for (int i = 0; i < tableItemList.size(); i++) {
				HeaderTableItem hti = tableItemList.get(i);
				if (tItem.equals(hti.getItem())) {
					return;
				}
				if (hti.getSupportCount() < tItem.getSupportCount()) {
					insertIndex = i;
					break;
				}
			}
			tableItemList.add(insertIndex, tItem);
			tableItemMap.put(tItem.getItem(), tItem);
		}
	}
	
	public List<HeaderTableItem> getTableItemList() {
		return tableItemList;
	}
	
	public List<HeaderTableItem> getReverseTableItemList() {
		List<HeaderTableItem> reversedTableItemList = new ArrayList<>(tableItemList);
		Collections.reverse(reversedTableItemList);
		return reversedTableItemList;
	}
	
	public FPTreeNode getSideLink(Item itemID) {
		if (tableItemMap.containsKey(itemID)) {
			return tableItemMap.get(itemID).getSideLinkPointer();
		}
		return null;
	}
}
