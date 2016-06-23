package aucrec.server.rules;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ApiUsageChangeRule")
@XmlAccessorType(XmlAccessType.FIELD)
public class AucRule {
	
	@XmlElementWrapper(name="Antecedent")
	@XmlElement(name="MethodSignature")
	protected List<MethodSignature> antecedent;
	@XmlElementWrapper(name="Consequent")
	@XmlElement(name="MethodSignature")
	protected List<MethodSignature> consequent;
	
	@XmlElement(name="Support")
	protected int support;
	@XmlElement(name="Confidence")
	protected float confidence;
	
	@XmlElementWrapper(name="UsageChangeExampleList")
	@XmlElement(name="UsageChangeExample")
	protected List<UsageChangeExample> examples;
	
	public List<MethodSignature> getAntecedent() {
		return antecedent;
	}

	public void setAntecedent(List<MethodSignature> antecedent) {
		this.antecedent = antecedent;
	}

	public List<MethodSignature> getConsequent() {
		return consequent;
	}

	public void setConsequent(List<MethodSignature> consequent) {
		this.consequent = consequent;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}

	public float getConfidence() {
		return confidence;
	}

	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}

	public List<UsageChangeExample> getExamples() {
		return examples;
	}

	public void setExamples(List<UsageChangeExample> examples) {
		this.examples = examples;
	}
}
