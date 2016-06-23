package aucrec.server.rules;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MethodSignature")
@XmlAccessorType(XmlAccessType.FIELD)
public class MethodSignature {
	@XmlElement(name="ReturnType")
	protected String returnType;
	
	@XmlElement(name="DefClass")
	protected String defClass;
	
	@XmlElement(name="MethodName")
	protected String methodName;
	
	@XmlElementWrapper(name="ParameterList")
	@XmlElement(name="Parameter")
	protected List<String> parameterList;
	
	/*方法（或字段）的完全限定名:
	 *[QualifiedReturnType#](QualifiedMethodName|QualifiedFieldName)([QualifiedParameterType{,QualifiedParameterType}])
	 */
	@XmlElement(name="FullQualifiedName")
	protected String fullQualifiedName;
	
	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getDefClass() {
		return defClass;
	}

	public void setDefClass(String defClass) {
		this.defClass = defClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<String> getParameterList() {
		return parameterList;
	}

	public void setParameterList(List<String> paramTypeList) {
		this.parameterList = paramTypeList;
	}

	public String getFullQualifiedName() {
		return fullQualifiedName;
	}

	public void setFullQualifiedName(String fullQualifiedName) {
		this.fullQualifiedName = fullQualifiedName;
	}

	@Override
	public String toString() {
		return fullQualifiedName;
	}
	
	@Override
	public int hashCode()
	{
		return fullQualifiedName.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof MethodSignature) {
			return fullQualifiedName.equals(((MethodSignature)o).fullQualifiedName);
		}
		return false;
	}
}