package cn.edu.nju.aucminer.recommender;

import java.util.List;

public class MethodInfo {
	
	private String returnType;
	private String ownedClass;
	private String methodName;
	private List<String> arguments;
	private String fullQualifiedName;
	
	public MethodInfo(String fullQualifiedName) {
		this.fullQualifiedName = fullQualifiedName;
		
		if (fullQualifiedName != null) {
			// TODO: extract return type, owned class, method name and arguments.
			
		}
	}

	public String getReturnType() {
		return returnType;
	}

	public String getOwnedClass() {
		return ownedClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public String getFullQualifiedName() {
		return fullQualifiedName;
	}
	
}
