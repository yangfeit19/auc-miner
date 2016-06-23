package aucminer.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="configuration")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
		"oldVersion", 
		"newVersion", 
		"resultStorage", 
		"parameters"})
public class Configuration {
	
	@XmlElement(name="oldVersion")
	private String oldVersion;
	
	@XmlElement(name="newVersion")
	private String newVersion;
	
	@XmlElement(name="resultStorage")
	@XmlJavaTypeAdapter(StorageXmlAdapter.class)
	private Storage resultStorage;
	
	@XmlElement(name="parameters")
	@XmlJavaTypeAdapter(ParametersXmlAdapter.class)
	private Parameters parameters;
	
	public String getOldVersion() {
		return oldVersion;
	}
	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}
	public String getNewVersion() {
		return newVersion;
	}
	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}
	public Storage getResultStorage() {
		return resultStorage;
	}
	public void setResultStorage(Storage resultStorage) {
		this.resultStorage = resultStorage;
	}
	public Parameters getParameters() {
		return parameters;
	}
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	
}

