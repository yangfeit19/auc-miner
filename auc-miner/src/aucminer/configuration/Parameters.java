package aucminer.configuration;

public class Parameters {
	
	public static final int NUM_OF_PARAMETERS = 6;
	
	boolean includeField;
	boolean splitMethod;
	int splitThreshold;
	int invocationChainLen;
	int minSupport;
	float minConfidence;
	
	public boolean isIncludeField() {
		return includeField;
	}
	public void setIncludeField(boolean includeField) {
		this.includeField = includeField;
	}
	public boolean isSplitMethod() {
		return splitMethod;
	}
	public void setSplitMethod(boolean splitMethod) {
		this.splitMethod = splitMethod;
	}
	public int getSplitThreshold() {
		return splitThreshold;
	}
	public void setSplitThreshold(int splitThreshold) {
		this.splitThreshold = splitThreshold;
	}
	public int getInvocationChainLen() {
		return invocationChainLen;
	}
	public void setInvocationChainLen(int invocationChainLen) {
		this.invocationChainLen = invocationChainLen;
	}
	public int getMinSupport() {
		return minSupport;
	}
	public void setMinSupport(int minSupport) {
		this.minSupport = minSupport;
	}
	public float getMinConfidence() {
		return minConfidence;
	}
	public void setMinConfidence(float minConfidence) {
		this.minConfidence = minConfidence;
	}
	public static int getNumOfParameters() {
		return NUM_OF_PARAMETERS;
	}
	
}
