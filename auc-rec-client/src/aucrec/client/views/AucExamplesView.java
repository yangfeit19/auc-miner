package aucrec.client.views;

import java.io.File;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;

import aucrec.client.examples.IAdaptionExampleGetter;
import aucrec.client.examples.LocalAdaptionExampleGetter;
import aucrec.client.examples.UsageChangeExample;

public class AucExamplesView {
	
	private IAdaptionExampleGetter exampleGetter;
	private List<UsageChangeExample> aucExamples;
	private int exampleIndex;
	
	public AucExamplesView(List<UsageChangeExample> aucExamples) {
		this.aucExamples = aucExamples;
		this.exampleIndex = 0;
		this.exampleGetter = new LocalAdaptionExampleGetter();
	}
	
	public void openAucExamplesView() {
		if (this.aucExamples != null && !this.aucExamples.isEmpty()) {
			UsageChangeExample example = this.aucExamples.get(0);
			compare(example.getOldCode().getFileRelativePath(), example.getNewCode().getFileRelativePath());
		}
	}
	
	private void compare(String pathFrom, String pathTo) {
		CompareEditorInput input = null;
		
		File[] filePair = exampleGetter.getAdaptionExampleSourceFiles(pathFrom, pathTo);
		if (filePair[0] != null && filePair[1] != null) {
			CompareConfiguration configuration = new CompareConfiguration();
			input = new FileCompareEditorInput(configuration, filePair[0], filePair[1]);
		}
		
		if (input != null) {
			CompareUI.openCompareEditor(input);
		}
	}
	
	public int getCurrentExampleIndex() {
		return this.exampleIndex;
	}
}
