package aucrec.client.rec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import aucrec.client.examples.UsageChangeExample;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("restriction")
public class AucRecSearcher implements IAucRecSearcher {
	
	private static final String SEARCH_URL = "http://localhost:8080/AUCRecServer/searchAucRules.action?methodSignature=";

	@Override
	public List<AucRule> searchAucRec(List<MethodSignature> oldMethods) {
		List<AucRule> result = new ArrayList<AucRule>();
		if (oldMethods != null && !oldMethods.isEmpty()) {
			String methodSignatureJson = JSONObject.fromObject(oldMethods.get(0)).toString();
			try {
				methodSignatureJson = URLEncoder.encode(methodSignatureJson, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// Do nothing.
			}
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(SEARCH_URL + methodSignatureJson);
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity entity = httpResponse.getEntity();    
		        if (entity != null) {    
		            InputStream instreams = entity.getContent();    
		            JSONArray resultJsonArray = JSONArray.fromObject(convertStreamToString(instreams)); 
		            for (int i = 0; i < resultJsonArray.size(); i++) {
		            	
		            	@SuppressWarnings("rawtypes")
						Map<String, Class> classMap = new HashMap<String, Class>();
		            	classMap.put("antecedent", MethodSignature.class);
		            	classMap.put("consequent", MethodSignature.class);
		            	classMap.put("examples", UsageChangeExample.class);
		            	classMap.put("parameterList", String.class);
		            	classMap.put("oldCode", CodeLocation.class);
		            	classMap.put("newCode", CodeLocation.class);
		            	
		            	result.add((AucRule)JSONObject.toBean(resultJsonArray.getJSONObject(i), AucRule.class, classMap));
		            }
		            httpGet.abort();    
		        }  
			} catch (IOException e) {
				// Do nothing.
			}
		}
		return result;
	}
	
	public String convertStreamToString(InputStream is) {      
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

}
