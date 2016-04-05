package cn.edu.nju.aucminer.client.adaptionexamples;

import java.io.File;

public class LocalAdaptionExampleGetter implements IAdaptionExampleGetter {

	@Override
	public File[] getAdaptionExampleSourceFiles(String pathFrom, String pathTo) {
		File[] result = new File[] {new File(pathFrom), new File(pathTo)};
		return result;
	}

}
