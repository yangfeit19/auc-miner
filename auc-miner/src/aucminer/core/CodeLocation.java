package aucminer.core;

public class CodeLocation {
	private String fileFullPath; 	//所在文件的路径
	private int startLine; 		//在文件中的起始行数
	private int endLine;  			//在文件中的结束行数
	
	public String getFileFullPath() {
		return fileFullPath;
	}
	
	public int getStartLine() {
		return startLine;
	}
	
	public int getEndLine() {
		return endLine;
	}
}
