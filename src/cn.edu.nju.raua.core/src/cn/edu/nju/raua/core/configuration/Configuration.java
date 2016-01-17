package cn.edu.nju.raua.core.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
		"workingDirectory", 
		"oldVersionProject", 
		"oldVersionWihtNewFramework", 
		"newVersionProject", 
		"operation"})
@XmlRootElement(name="configurations")
public class Configuration {
	
	@XmlElement(name="workingDirectory")
	private String workingDirectory;
	@XmlElement(name="oldVersionProject")
	private String oldVersionProject;
	@XmlElement(name="oldVersionWihtNewFramework")
	private String oldVersionWihtNewFramework;
	@XmlElement(name="newVersionProject")
	private String newVersionProject;
	@XmlElement(name="operation")
	private String operation;
	
	
	public String getSaveDir() {
		return workingDirectory;
	}
	public String getOldVersionProject() {
		return oldVersionProject;
	}
	public String getReserved_1() {
		return oldVersionWihtNewFramework;
	}
	public String getNewVersionProject() {
		return newVersionProject;
	}
	public String getReserved_2() {
		return operation;
	}

}
