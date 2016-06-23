package aucrec.server.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

import aucrec.server.rules.MethodSignature;
import aucrec.server.rules.searcher.XmlFileSearcher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SearchAucRulesAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {
	private static final long serialVersionUID = 1L;
	
	private static String rulesFilePath = "C:\\Users\\fei\\Desktop\\test.xml";
	private static XmlFileSearcher xmlFileSearcher = new XmlFileSearcher(rulesFilePath);
	
	private HttpServletRequest request;  
    private HttpServletResponse response;
    
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public void searchAucRules() {
		String methodSignatureJson = this.request.getParameter("methodSignature");
		JSONObject jsonObject = JSONObject.fromObject(methodSignatureJson);
		MethodSignature methodSignature = (MethodSignature)JSONObject.toBean(jsonObject, MethodSignature.class);
		
		List<MethodSignature> apiList = new ArrayList<MethodSignature>();
		apiList.add(methodSignature);
		JSONArray jsonArray = JSONArray.fromObject(xmlFileSearcher.searchAucRule(apiList));  
        try {  
            this.response.setCharacterEncoding("UTF-8");  
            this.response.getWriter().write(jsonArray.toString()); 
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
	}

}
