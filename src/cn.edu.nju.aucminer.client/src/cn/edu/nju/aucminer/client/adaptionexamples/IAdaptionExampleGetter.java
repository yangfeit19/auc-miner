package cn.edu.nju.aucminer.client.adaptionexamples;

import java.io.File;

public interface IAdaptionExampleGetter {
	
	File[] getAdaptionExampleSourceFiles(String oldFile, String newFile);
	
}
