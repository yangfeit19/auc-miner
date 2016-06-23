package aucrec.client.examples;

import java.io.File;

public class LocalAdaptionExampleGetter implements IAdaptionExampleGetter {

	@Override
	public File[] getAdaptionExampleSourceFiles(String pathFrom, String pathTo) {
		AucExampleFilesDownloader downloader = new AucExampleFilesDownloader();
		String oldFileAbsolutePath = downloader.downloadFile(pathFrom);
		String newFileAbsolutePath = downloader.downloadFile(pathTo);
		File[] result = new File[] {new File(oldFileAbsolutePath), new File(newFileAbsolutePath)};
		return result;
	}

}
