package aucminer.transactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

@XmlRootElement(name = "CallDependencyModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class CDModel {
	/**
	 * key: 调用者。
	 * value: 被调用方法或字段的列表。 
	 * 调用者和被调用者均为MethodInfo类型
	 */
	@XmlElement(name="CallDependencyList")
	@XmlJavaTypeAdapter(CallDependenciesXmlAdapter.class)
	private Map<MethodInfo, List<MethodInfo>> callerToCalleesMap;
	
	@XmlTransient
	private Map<MethodInfo, List<MethodInfo>> calleeToCallersMap;
	@XmlTransient
	private Map<String, Set<String>> classToMethodsMap;
	@XmlTransient
	private Set<String> declaredMethodAndFieldSet;
	
	public CDModel() {
		callerToCalleesMap = new HashMap<MethodInfo, List<MethodInfo>>();
		calleeToCallersMap = new HashMap<MethodInfo, List<MethodInfo>>();
		declaredMethodAndFieldSet = new HashSet<String>();
		classToMethodsMap = new HashMap<String, Set<String>>();
	}
	
	public void addCallRelationship(MethodInfo caller, List<MethodInfo> callees) {
		List<MethodInfo> _callees = callerToCalleesMap.get(caller);
		if(_callees == null) {
			_callees = new ArrayList<MethodInfo>();
			callerToCalleesMap.put(caller, _callees);
		}
		_callees.addAll(callees);
		
		for(MethodInfo callee : callees) {
			List<MethodInfo> _callers = calleeToCallersMap.get(callee);
			if (_callers == null) {
				_callers = new ArrayList<MethodInfo>();
				calleeToCallersMap.put(callee, _callers);
			}
			_callers.add(caller);
		}
	}
	
	public void addCallRelationship(MethodInfo caller, MethodInfo callee) {
		List<MethodInfo> callees = new ArrayList<MethodInfo>();
		callees.add(callee);
		this.addCallRelationship(caller,  callees);
	}
	
	public void appendCDModel(CDModel model) {
		callerToCalleesMap.putAll(model.getCallerToCalleesMap());
		calleeToCallersMap.putAll(model.getCalleeToCallersMap());
		declaredMethodAndFieldSet.addAll(model.getDeclaredMethodAndFieldSet());
		classToMethodsMap.putAll(model.getClassToMethodsMap());
	}
	
	public void addDeclarationString(String declaration) {
		declaredMethodAndFieldSet.add(declaration);
	}
	
	public Map<MethodInfo, List<MethodInfo>> getCallerToCalleesMap() {
		return callerToCalleesMap;
	}
	
	public Map<MethodInfo, List<MethodInfo>> getCalleeToCallersMap() {
		return calleeToCallersMap;
	}
	
	public Set<String> getDeclaredMethodAndFieldSet() {
		return declaredMethodAndFieldSet;
	}
	
	public Map<String, Set<String>> getClassToMethodsMap() {
		return classToMethodsMap;
	}
	
	public void addMethodDeclaration(String className, String method) {
		Set<String> methodSet = classToMethodsMap.get(className);
		if (methodSet == null) {
			methodSet = new HashSet<String>();
		}
		methodSet.add(method);
		classToMethodsMap.put(className, methodSet);
	}
	
	public static class CallDependenciesXmlAdapter extends XmlAdapter<Object, Map<MethodInfo, List<MethodInfo>>> {

		/**
		 * Convert callerToCalleeMap to element object.
		 */
		@Override
		public Object marshal(Map<MethodInfo, List<MethodInfo>> callerToCalleeMap) throws Exception {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.newDocument();
			
			Element rootElement = document.createElement("CallDependencyList");
			document.appendChild(rootElement);
			for(Entry<MethodInfo, List<MethodInfo>> entry : callerToCalleeMap.entrySet()) {
				Element cdElement = document.createElement("CallDependency");
				
				Element callerElement = document.createElement("Caller");
				
				Element startLine = document.createElement("StartLine");
				startLine.setTextContent("" + entry.getKey().startLine);
				callerElement.appendChild(startLine);
				Element endLine = document.createElement("EndLine");
				endLine.setTextContent("" + entry.getKey().endLine);
				callerElement.appendChild(endLine);
				Element fullQualifiedName = document.createElement("FullQualifiedName");
				fullQualifiedName.setTextContent(entry.getKey().fullQualifiedName);
				callerElement.appendChild(fullQualifiedName);
				
				cdElement.appendChild(callerElement);
				
				for(MethodInfo callee : entry.getValue()) {
					Element calleeElement = document.createElement("Callee");
					
					startLine = document.createElement("StartLine");
					startLine.setTextContent("" +callee.startLine);
					calleeElement.appendChild(startLine);
					endLine = document.createElement("EndLine");
					endLine.setTextContent("" + callee.endLine);
					calleeElement.appendChild(endLine);
					fullQualifiedName = document.createElement("FullQualifiedName");
					fullQualifiedName.setTextContent(callee.fullQualifiedName);
					calleeElement.appendChild(fullQualifiedName);
					
					cdElement.appendChild(calleeElement);
				}
				
				rootElement.appendChild(cdElement);
			}
			return rootElement;
		}

		/**
		 * Convert element object to callerToCalleeMap.
		 */
		@Override
		public Map<MethodInfo, List<MethodInfo>> unmarshal(Object v) throws Exception {
			return new HashMap<MethodInfo, List<MethodInfo>>();
		}
		
	}
}
