package aucminer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="AssociationRuleList")
public class AssociationRuleList {
	
	@XmlElement(name="AssociationRule") 
	private List<AssociationRule> rules;
	
	public AssociationRuleList () {
		rules = new ArrayList<>();
	}

	public void addAssociationRule(AssociationRule rule) {
		if (rule != null) {
			rules.add(rule);
		}
	}
	
	public List<AssociationRule> getRules() {
		return rules;
	}
	
	public void sortByDescendingSupport() {
		Collections.sort(rules, new AssociationRuleComparator());
	}
	
	class AssociationRuleComparator implements Comparator<AssociationRule> {

		@Override
		public int compare(AssociationRule arg0, AssociationRule arg1) {
			return arg1.getSupport() - arg0.getSupport();
		}

	}
}
