package aucrec.server.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Location")
@XmlAccessorType(XmlAccessType.FIELD)
public class CodeLocation {
	@XmlElement(name="FileRelativePath")
	protected String fileRelativePath;
	
	@XmlElement(name="StartLine")
	protected int startLine;
	
	@XmlElement(name="EndLine")
	protected int endLine;

	public String getFileRelativePath() {
		return fileRelativePath;
	}

	public void setFileRelativePath(String fileRelativePath) {
		this.fileRelativePath = fileRelativePath;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
}
