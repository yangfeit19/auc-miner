package aucrec.client.rec;

import java.util.List;

public interface IAucRecSearcher {
	
	public List<AucRule> searchAucRec(List<MethodSignature> oldMethods);
	
}
