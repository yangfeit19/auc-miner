package cn.edu.nju.raua.fpmining.aprior.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class PluginUtil {
	public static String getTransactionFile(Shell shell) {
		if (shell == null) {
			throw new NullPointerException("Parameter cannot be null");
		}
		
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
		dialog.setText("Open Transactions File");
		String absoluteFilePath = dialog.open();
		return absoluteFilePath;
	}
}
