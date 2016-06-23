package aucrec.server.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.opensymphony.xwork2.ActionSupport;

public class DownloadExampleFileAction extends ActionSupport{
	private static final long serialVersionUID = 1L;

	private static final String fileStoreDir = "C:/Users/fei/Desktop/";
	  
    private String fileName;  //相对路径
      
    public String getFileName() {  
        return fileName;  
    }  
  
    public void setFileName(String fileName) {  
        this.fileName = fileName;  
    }  
  
    //返回一个输入流，作为一个客户端来说是一个输入流，但对于服务器端是一个输出流  
    public InputStream getDownloadFile() throws Exception {  
        String fileAbsolutePath = fileStoreDir + fileName;
        File file = new File(fileAbsolutePath);
    	return new FileInputStream(file);  
    }  
      
    @Override  
    public String execute() throws Exception {  
        return SUCCESS;  
    } 
}
