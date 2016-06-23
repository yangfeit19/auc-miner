package aucrec.client.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import aucrec.client.Activator;

@SuppressWarnings("restriction")
public class AucExampleFilesDownloader {
	private static final String DOWNLOAD_URL = "http://localhost:8080/AUCRecServer/downloadExampleFile.action?fileName=";
	
	public List<String> downloadExampleFiles(List<String> fileNames) {
		List<String> absoluteFilePathList = new ArrayList<String>();
		for (String fileName : fileNames) {
			String absolutePath = downloadFile(fileName);
			if (absolutePath != null && !absolutePath.isEmpty()) {
				absoluteFilePathList.add(absolutePath);
			}
		}
		return absoluteFilePathList;
	}
	
	public String downloadFile(String fileName) {
		String absolutePath = null;
		if (fileName != null && !fileName.isEmpty()) {
			String fileEncodedName = fileName;
			try {
				fileEncodedName = URLEncoder.encode(fileName, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// Do nothing.
			}
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(DOWNLOAD_URL + fileEncodedName);
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity entity = httpResponse.getEntity();    
		        if (entity != null) {    
		            InputStream instreams = entity.getContent();    
		            String content = convertStreamToString(instreams); 
		            absolutePath = Activator.EXAMPLE_FILES_DIR + fileName;
		            if (content != null && createFile(absolutePath)) {
		            	try(PrintWriter out = new PrintWriter(absolutePath)) {
		            	    out.println(content);
		            	}
		            }
		            httpGet.abort();    
		        }  
			} catch (IOException e) {
				// Do nothing.
			}
		}
		return absolutePath;
	}
	
	private String convertStreamToString(InputStream is) {      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
        StringBuilder sb = new StringBuilder();      
       
        String line = null;      
        try {      
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");      
            }      
        } catch (IOException e) {      
            e.printStackTrace();      
        } finally {      
            try {      
                is.close();      
            } catch (IOException e) {      
               e.printStackTrace();      
            }      
        }      
        return sb.toString();      
    }
	
	private boolean createFile(String destFileName) {  
        File file = new File(destFileName); 
        
        if(file.exists()) {  
            return true; 
        }  
        
        if (destFileName.endsWith(File.separator)) {  
            return false;  
        }  

        if(!file.getParentFile().exists()) {  
            if(!file.getParentFile().mkdirs()) {  
                return false;  
            }  
        }  
        
        try {  
            if (file.createNewFile()) {  
                return true;  
            } else {   
                return false;  
            }  
        } catch (IOException e) {  
            e.printStackTrace();
            return false;  
        }  
    }  
}
