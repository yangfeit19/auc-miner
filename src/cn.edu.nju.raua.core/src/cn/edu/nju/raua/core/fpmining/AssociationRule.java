package cn.edu.nju.raua.core.fpmining;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import cn.edu.nju.raua.core.transactions.Item;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="AssociationRule", propOrder={"support", 
		"confidence", "antecedent", "consequent"})
public class AssociationRule {
	@XmlElement(name="Support")
	private int support;
	@XmlElement(name="Confidence")
	private double confidence;
	@XmlElementWrapper(name="Antecedent")
	@XmlElement(name="Item")
	private List<Item> antecedent;
	@XmlElementWrapper(name="Consequent")
	@XmlElement(name="Item")
	private List<Item> consequent;
	
	public AssociationRule (List<Item> antecedent, List<Item> consequent, int support) {
		if ((antecedent == null) || (consequent == null)) {
			throw new NullPointerException("Parameters cannot be null.");
		}
		this.antecedent = antecedent;
		this.consequent = consequent;
		this.support = support;
		this.confidence = 100;
	}
	
	public int getSupport() {
		return support;
	}

	public double getConfidence() {
		return confidence;
	}

	public List<Item> getAntecedent() {
		return antecedent;
	}

	public List<Item> getConsequent() {
		return consequent;
	}

	public void setConfidence(double confidence) {
		if ((confidence <= 0) || (confidence > 100)) {
			throw new IllegalArgumentException("confidence must be in (0, 100]: " + confidence);
		}
		this.confidence = confidence;
	}
	
}
