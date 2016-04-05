package cn.edu.nju.aucminer.recommender;

public class InvocationInfo {
	
	private MethodInfo method;
	private String projectName;
	private String filePath;
	private int startLineNum;
	private int startPos;
	private int endPos;
	
	public InvocationInfo(MethodInfo method, String projectName, String filePath, int startLineNum, int startPos, int endPos) {
		this.method = method;
		this.projectName = projectName;
		this.filePath = filePath;
		this.startLineNum = startLineNum;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	public MethodInfo getMethodInfo() {
		return method;
	}
	
	public String getPath() {
		return filePath;
	}
	
	public int getStartLineNum() {
		return startLineNum;
	}
	
	public int getStartPos() {
		return startPos;
	}
	
	public int getEndPos() {
		return endPos;
	}

	public String getProjectName() {
		return projectName;
	}
}
