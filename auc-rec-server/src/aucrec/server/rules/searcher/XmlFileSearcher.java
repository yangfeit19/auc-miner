package aucrec.server.rules.searcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import aucrec.server.rules.AucRule;
import aucrec.server.rules.AucRuleList;
import aucrec.server.rules.MethodSignature;

public class XmlFileSearcher implements AucRuleSearcher {
	
	public AucRuleList aucRuleList;
	
	public XmlFileSearcher(String xmlFilePath) {
		aucRuleList = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(AucRuleList.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			aucRuleList = (AucRuleList)unmarshaller.unmarshal(new File(xmlFilePath));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<AucRule> searchAucRule(List<MethodSignature> apiList) {
		List<AucRule> rules = new ArrayList<AucRule>();
		for (AucRule rule : aucRuleList.getAucRuleList()) {
			if (rule.getAntecedent().containsAll(apiList)) {
				rules.add(rule);
			}
		}
		return rules;
	}

}
