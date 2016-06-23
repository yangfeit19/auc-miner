package aucrec.server.rules;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ApiUsageChangeRuleList")
@XmlAccessorType(XmlAccessType.FIELD)
public class AucRuleList {
	@XmlElement(name="ApiUsageChangeRule")
	List<AucRule> aucRuleList;

	public List<AucRule> getAucRuleList() {
		return aucRuleList;
	}

	public void setAucRuleList(List<AucRule> aucRuleList) {
		this.aucRuleList = aucRuleList;
	}
}
