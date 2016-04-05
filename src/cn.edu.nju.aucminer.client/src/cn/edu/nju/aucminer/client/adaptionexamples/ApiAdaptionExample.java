package cn.edu.nju.aucminer.client.adaptionexamples;

public class ApiAdaptionExample {
	
	private String pathFrom;
	private int fromStartLine;
	private int fromEndLine;
	private String pathTo;
	private int toStartLine;
	private int toEndLine;
	
	public ApiAdaptionExample(String pathFrom, int fromStartLine, int fromEndLine, String pathTo, int toStartLine, int toEndLine) {
		this.pathFrom = pathFrom;
		this.fromStartLine = fromStartLine;
		this.fromEndLine = fromEndLine;
		this.pathTo = pathTo;
		this.toStartLine = toStartLine;
		this.toEndLine = toEndLine;
	}

	public String getPathFrom() {
		return pathFrom;
	}

	public int getFromStartLine() {
		return fromStartLine;
	}

	public int getFromEndLine() {
		return fromEndLine;
	}

	public String getPathTo() {
		return pathTo;
	}

	public int getToStartLine() {
		return toStartLine;
	}

	public int getToEndLine() {
		return toEndLine;
	}
	
}
