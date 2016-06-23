package aucminer.configuration;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParametersXmlAdapter extends XmlAdapter<Object, Parameters> {

	@Override
	public Object marshal(Parameters parameters) throws Exception {
		
		if (parameters == null) {
			return null;
		}
		
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = docBuilder.newDocument();
		
		Element rootElement = document.createElement("parameters");
		document.appendChild(rootElement);
		
		Element includeField = document.createElement("includeField");
		includeField.setTextContent(parameters.isIncludeField() ? "true" : "false");
		rootElement.appendChild(includeField);
		
		Element splitMethod = document.createElement("splitMethod");
		splitMethod.setTextContent(parameters.isSplitMethod() ? "true" : "false");
		rootElement.appendChild(splitMethod);
		
		Element splitThreshold = document.createElement("splitThreshod");
		splitMethod.setTextContent(Integer.toString(parameters.getSplitThreshold()));
		rootElement.appendChild(splitThreshold);
		
		Element invocationChainLen = document.createElement("invocationChainLen");
		invocationChainLen.setTextContent(Integer.toString(parameters.getInvocationChainLen()));
		rootElement.appendChild(invocationChainLen);
		
		Element minSupport = document.createElement("minSupport");
		minSupport.setTextContent(Integer.toString(parameters.getMinSupport()));
		rootElement.appendChild(minSupport);
		
		Element minConfidence = document.createElement("minConfidence");
		minConfidence.setTextContent(Float.toString(parameters.getMinConfidence()));
		rootElement.appendChild(minConfidence);
		
		return rootElement;
	}

	@Override
	public Parameters unmarshal(Object obj) throws Exception {
		
		if (obj == null) {
			return null;
		}

		Element element = (Element)obj;
		NodeList parameterNodes = element.getChildNodes();
		if (parameterNodes == null || parameterNodes.getLength() != Parameters.NUM_OF_PARAMETERS + 1) {
			return null;
		}

		Parameters parameters = new Parameters();
		for (int i = 0; i < Parameters.NUM_OF_PARAMETERS; i++) {
			Node parmNode = parameterNodes.item(i);
			switch (parmNode.getNodeName()) {
				case "includeField":
					parameters.setIncludeField(Boolean.parseBoolean(parmNode.getTextContent()));
					break;
				case "splitMethod":
					parameters.setSplitMethod(Boolean.parseBoolean(parmNode.getTextContent()));
					break;
				case "splitThreshold":
					parameters.setSplitThreshold(Integer.parseInt(parmNode.getTextContent()));
					break;
				case "invocationChainLen":
					parameters.setInvocationChainLen(Integer.parseInt(parmNode.getTextContent()));
					break;
				case "minSupport":
					parameters.setMinSupport(Integer.parseInt(parmNode.getTextContent()));
					break;
				case "minConfidence":
					parameters.setMinConfidence(Float.parseFloat(parmNode.getTextContent()));
					break;
				default:
					return null;
			}
		}
		
		return parameters;
	}

}
