import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.testng.ITestContext;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @Author You Jia
 * @Date 8/3/2018 10:40 AM
 */
@AutoService(ITestNGListener.class)
public class TestResultListenerHttpHandler extends TestListenerAdapter {
    String durationKey = "duration";
    String countKey = "count";
    ContentResponse response = null;
    //Get current timestamp
    Timestamp currentTimestamp = new Timestamp(new Date().getTime());
    private static String token ="";

    @Override
    public void onFinish(ITestContext testContext) {
        if (System.getenv("testserver") != null && System.getenv("loginname") != null && System.getenv("loginpassword") != null) {
            System.out.println("Test Results Http handler");
            HttpClient httpclient = new HttpClient();
            long totalDuration = 0;
            try {
                httpclient.start();
                String baseURL = System.getenv("testserver");
                token = InterceptorTestHttpHandler.token;
                String url = baseURL + "db/TestResultSummary";
                String username = System.getenv("loginname");
                JSONObject testResultSummary = new JSONObject();
                Map passedResult = testResultsHandler(getPassedTests(), testContext, httpclient, username);
                Map failedResult = testResultsHandler(getFailedTests(), testContext, httpclient, username);
                Map ignoredResult = testResultsHandler(getSkippedTests(), testContext, httpclient, username);
                totalDuration = (long) passedResult.get(durationKey) + (long) failedResult.get(durationKey) + (long) ignoredResult.get(durationKey);
                testResultSummary.put("testName", testContext.getName());
                testResultSummary.put("passed", passedResult.get(countKey));
                testResultSummary.put("failed", failedResult.get(countKey));
                testResultSummary.put("ignored", ignoredResult.get(countKey));
                testResultSummary.put("duration", totalDuration);
                testResultSummary.put("timestamp", currentTimestamp);
                testResultSummary.put("user", username);

                response = httpclient.newRequest(url).method(HttpMethod.POST).header("Authorization", token).content(new BytesContentProvider(testResultSummary.toString().getBytes()), "application/json;charset=UTF-8").send();
                System.out.println(response.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private Map testResultsHandler(List<ITestResult> iTestResults, ITestContext testContext, HttpClient httpclient,String user)  {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        JSONArray testResultsList = new JSONArray();
        String baseURL = System.getenv("testserver");
        String url = baseURL + "db/TestResults";

        long duration = 0;

        for(ITestResult iTestResult : iTestResults){
            JSONObject testResults = new JSONObject();
            testResults.put("testName",testContext.getName());
            testResults.put("testMethodName",iTestResult.getName());
            testResults.put("testStatus",iTestResult.getStatus());
            testResults.put("timestamp",currentTimestamp);
            testResults.put("user",user);
            if(iTestResult.getThrowable()!=null){
                testResults.put("failedReason",iTestResult.getThrowable().getMessage());
            }
            testResultsList.add(testResults);
            //duration calculation for test summary
            duration += iTestResult.getEndMillis() - iTestResult.getStartMillis();
        }
        concurrentHashMap.put(durationKey,duration);
        concurrentHashMap.put(countKey,iTestResults.size());

        if(!testResultsList.isEmpty()){
            try {
                response = httpclient.newRequest(url).method(HttpMethod.POST).header("Authorization",token).content(new BytesContentProvider(testResultsList.toString().getBytes()),"application/json;charset=UTF-8").send();
                System.out.println(response.getStatus());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        return concurrentHashMap;
    }
}
