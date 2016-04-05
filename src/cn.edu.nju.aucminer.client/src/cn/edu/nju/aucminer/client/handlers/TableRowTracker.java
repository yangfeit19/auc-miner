package cn.edu.nju.aucminer.client.handlers;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

public class TableRowTracker implements Listener {

	public static final int NO_COLUMN = -1;

	private final TableViewer fTableViewer;
	private boolean fHeaderArea = false;
	private int fSelectedColumnIndex = NO_COLUMN;

	public TableRowTracker(TableViewer tableViewer) {
		fTableViewer = tableViewer;
		fTableViewer.getTable().addListener(SWT.MenuDetect, this);
	}

	@Override
	public void handleEvent(Event event) {
		Table table = fTableViewer.getTable();

		// calculate click offset within table area
		Point point = Display.getDefault().map(null, table, new Point(event.x, event.y));
		Rectangle clientArea = table.getClientArea();
		fHeaderArea = (clientArea.y <= point.y) && (point.y < (clientArea.y + table.getHeaderHeight()));

		ViewerCell cell = fTableViewer.getCell(point);
		if (cell != null)
			fSelectedColumnIndex = cell.getColumnIndex();

		else {
			// no cell detected, click on header
			int xOffset = point.x;
			int columnIndex = 0;
			int[] order = table.getColumnOrder();
			while ((columnIndex < table.getColumnCount()) && (xOffset > table.getColumn(order[columnIndex]).getWidth())) {
				xOffset -= table.getColumn(order[columnIndex]).getWidth();
				columnIndex++;
			}

			fSelectedColumnIndex = (columnIndex < table.getColumnCount()) ? order[columnIndex] : NO_COLUMN;
		}
	}

	public boolean isHeaderArea() {
		return fHeaderArea;
	}

	public int getSelectedColumnIndex() {
		return fSelectedColumnIndex;
	}
}
