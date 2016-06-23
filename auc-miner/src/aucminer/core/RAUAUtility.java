package aucminer.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class RAUAUtility {
public static Configuration parseConfiguration(String file) {
		
		if (file == null) {
			throw new NullPointerException("The 'file' argument cannot be null.");
		}
		
		Configuration config = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(Configuration.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			System.gc();
			config = (Configuration)unmarshaller.unmarshal(new File(file));
			System.gc();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return config;
	}
	
	public static String getConfigureFile(Shell shell) {
		if (shell == null) {
			throw new NullPointerException("PluginUtil.getConfigureFile(Shell shell) "
					+ "cann't called with null argument.");
		}
		
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
		dialog.setText("Open configuration file");
		String absoluteFilePath = dialog.open();
		return absoluteFilePath;
	}
	
	public static void outputAssociationRuleList(AssociationRuleList assRuleList, String filePath) {
		marshal(assRuleList, AssociationRuleList.class, filePath);
	}
	
	public static void outputFrequentItemsetList(FrequentItemsetList freqItemsetList, String filePath) {
		marshal(freqItemsetList, FrequentItemsetList.class, filePath);
	}
	
	private static void marshal(Object object, Class<?> classesToBeBound, String filePath) {
		try {
			JAXBContext jc = JAXBContext.newInstance(classesToBeBound);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			System.gc();
			
			FileOutputStream stream = new FileOutputStream(filePath);
			marshaller.marshal(object, stream);
			stream.close();
		
			System.gc();
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
}
