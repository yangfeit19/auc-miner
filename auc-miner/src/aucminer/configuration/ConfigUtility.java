package aucminer.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ConfigUtility {
	
	public static Configuration parseConfigFromXmlFile(File configXmlFile) {
		Configuration config = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(Configuration.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			config = (Configuration)unmarshaller.unmarshal(configXmlFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public static Configuration parseConfigFromXmlString(String configXmlString) {
		Configuration config = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(Configuration.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			config = (Configuration)unmarshaller.unmarshal(new StringReader(configXmlString));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public static void saveConfigToXmlFile(Configuration config, File configXmlFile) {
		try {
			JAXBContext jc = JAXBContext.newInstance(Configuration.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			FileOutputStream stream = new FileOutputStream(configXmlFile);
			marshaller.marshal(config, stream);
			stream.close();
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String configToXmlStirng(Configuration config) {
		String xmlString = "";
		try {
			JAXBContext jc = JAXBContext.newInstance(Configuration.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter writer = new StringWriter();
			marshaller.marshal(config, writer);
			xmlString = writer.toString();
			writer.close();
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return xmlString;
	}
}
