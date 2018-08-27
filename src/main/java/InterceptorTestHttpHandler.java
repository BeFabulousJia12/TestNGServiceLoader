import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGListener;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @Author You Jia
 * @Date 8/2/2018 3:35 PM
 */
@AutoService(ITestNGListener.class)
public class InterceptorTestHttpHandler implements IMethodInterceptor {
    private boolean descriptionsRecord = false;
    public static String token="";
    Timestamp currentTime = new Timestamp(new Date().getTime());


    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        System.out.println("Test Case Http handler!");
        HttpClient httpclient = new HttpClient();

        // List<IMethodInstance> result = new ArrayList<IMethodInstance>();
        if (descriptionsRecord == false){
            try {
                httpclient.start();
                ContentResponse response = null;
                String baseURL = System.getenv("testserver");
                //login to test platform to get token
                JSONObject user = new JSONObject();
                //hardcode normal user info.
                user.put("username",System.getenv("loginname"));
                user.put("password",System.getenv("loginpassword"));
                user.put("timestamp",currentTime);
                String encodeContent = new DESEncrypt().encode("9ba45bfd50061212328ec03adfef1b6e75","utf-8",user.toString());
                response = httpclient.newRequest(baseURL + "login").method(HttpMethod.POST).content(new BytesContentProvider(encodeContent.getBytes()),"application/json;charset=UTF-8").send();
                token = (String)JSON.parseObject(response.getContentAsString()).get("token");
                JSONArray testCaseInfos = new JSONArray();
                String url = baseURL + "db/TestCase";
                for (IMethodInstance method : methods) {
                    JSONObject testCaseInfo = new JSONObject();
                    Test testMethod = method.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class);
                    testCaseInfo.put("featureName",method.getMethod().getTestClass().getRealClass().getName());
                    testCaseInfo.put("testMethod",method.getMethod().getConstructorOrMethod().getName());
                    testCaseInfo.put("testSteps",testMethod.description());
                    testCaseInfo.put("testName",context.getName());
                    testCaseInfo.put("user",context.getCurrentXmlTest().getParameter("username"));
                    testCaseInfos.add(testCaseInfo);
                }
                response = httpclient.newRequest(url).method(HttpMethod.POST).header("Authorization",token).content(new BytesContentProvider(testCaseInfos.toString().getBytes()),"application/json;charset=UTF-8").send();
                System.out.println(response.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }

            descriptionsRecord = true;

            }

        return methods;
    }
}
