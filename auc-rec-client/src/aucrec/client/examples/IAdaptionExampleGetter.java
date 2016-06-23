package aucrec.client.examples;

import java.io.File;

public interface IAdaptionExampleGetter {
	
	File[] getAdaptionExampleSourceFiles(String oldFile, String newFile);
	
}
