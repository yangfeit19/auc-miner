package cn.edu.nju.raua.transactions.cd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="DetailedInformation", propOrder={"startLine", "endLine", "fullQualifiedName"})
public class MethodInfo {
	
	@XmlElement(name="StartLine")
	//方法定义或调用在文件中的起始行数
	public int startLine;	
	@XmlElement(name="EndLine")
	//方法定义或调用在文件中的结束行数
	public int endLine;	
	@XmlElement(name="FullQualifiedName")
	//定义或调用方法的完全限定名:
	//  [QualifiedReturnType#](QualifiedMethodName|QualifiedFieldName)([QualifiedParameterType{,QualifiedParameterType}])
	public String fullQualifiedName;
	//方法定义或调用所在文件的绝对路径
	@XmlTransient
	public String fileFullPath;
	
	public MethodInfo(int startLine, int endLine, String fullQualifiedName, String fileFullPath) {
		this.startLine = startLine;
		this.endLine = endLine;
		this.fullQualifiedName = fullQualifiedName;
		this.fileFullPath = fileFullPath;
	}
	
	@Override
	public int hashCode()
	{
		return fullQualifiedName.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof MethodInfo) {
			return fullQualifiedName.equals(((MethodInfo)o).fullQualifiedName);
		}
		return false;
	}
}
