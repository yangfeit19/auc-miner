package aucrec.client.examples;

import aucrec.client.rec.CodeLocation;

public class UsageChangeExample {
	protected CodeLocation oldCode;
	protected CodeLocation newCode;
	
	public CodeLocation getOldCode() {
		return oldCode;
	}
	
	public void setOldCode(CodeLocation oldCode) {
		this.oldCode = oldCode;
	}
	
	public CodeLocation getNewCode() {
		return newCode;
	}
	
	public void setNewCode(CodeLocation newCode) {
		this.newCode = newCode;
	}
}
