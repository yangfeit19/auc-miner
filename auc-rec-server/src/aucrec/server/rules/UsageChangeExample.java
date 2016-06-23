package aucrec.server.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="UsageChangeExample")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageChangeExample {
	@XmlElement(name="OldCode")
	protected CodeLocation oldCode;
	@XmlElement(name="NewCode")
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
