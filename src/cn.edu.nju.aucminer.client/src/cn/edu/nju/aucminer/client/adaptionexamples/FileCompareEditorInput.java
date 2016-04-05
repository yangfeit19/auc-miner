package cn.edu.nju.aucminer.client.adaptionexamples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;

public class FileCompareEditorInput extends CompareEditorInput {
	
	private File left;
	private File right;
	
	public FileCompareEditorInput(CompareConfiguration configuration, File left, File right) {
		super(configuration);
		this.left = left;
		this.right = right;
	}

	
	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		FileNode fNode1 = new FileNode(left);
		FileNode fNode2 = new FileNode(right);
		Differencer diff = new Differencer();
		
		Object returnObj = diff.findDifferences(false, new NullProgressMonitor(), null, null, fNode1, fNode2);
			
		
		return returnObj;
	}

}

class FileNode implements IStreamContentAccessor, ITypedElement {

	private File file;
	
	public FileNode(File file) {
		super();
		this.file = file;
	}

	public InputStream getContents() throws CoreException {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch(IOException ioe) {
			throw new CoreException(new Status(IStatus.ERROR,"me",ioe.getMessage(),ioe));
		}
		return is;
	}

	public Image getImage() {
		return null;
	}

	public String getName() {
		return file.getName();
	}

	public String getType() {
		String name = file.getName();
		int index = name.lastIndexOf(".");
		
		String type = ITypedElement.UNKNOWN_TYPE;
		if (file.isDirectory()) {
			type = ITypedElement.FOLDER_TYPE;
		} else if (index > -1 && index + 1 < name.length()) {
			type = file.getName().substring(index+1);
		}
		
		return type;
	}
	
}