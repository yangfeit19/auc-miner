package aucrec.server.rules.searcher;

import java.util.List;

import aucrec.server.rules.AucRule;
import aucrec.server.rules.MethodSignature;

public interface AucRuleSearcher {
	public List<AucRule> searchAucRule(List<MethodSignature> apiList);
}
