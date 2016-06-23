package aucrec.client.rec;

import java.util.List;

import aucrec.client.examples.UsageChangeExample;


public class AucRule {
	protected List<MethodSignature> antecedent;
	protected List<MethodSignature> consequent;
	protected int support;
	protected float confidence;
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
