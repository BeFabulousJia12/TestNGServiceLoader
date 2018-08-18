import com.google.auto.service.AutoService;
import mapper.TestCaseManagementMapper;
import model.TestResultSummary;
import model.TestResults;
import org.apache.ibatis.session.SqlSession;
import org.testng.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author You Jia
 * @Date 7/24/2018 5:44 PM
 */

public class TestResultListener extends TestListenerAdapter {
    String durationKey = "duration";
    String countKey = "count";
    //Get current timestamp
    Timestamp currentTimestamp = new Timestamp(new Date().getTime());

    @Override
    public void onFinish(ITestContext testContext){
        SqlSessionInitial sqlSessionInitial = new SqlSessionInitial();
        SqlSession sqlSession = null;

        long totalDuration = 0;

        try {
            sqlSession = sqlSessionInitial.getSqlSession("ConfigurationMySQL.xml");

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(sqlSession!=null){
            TestCaseManagementMapper mapper = sqlSession.getMapper(TestCaseManagementMapper.class);
            TestResultSummary testResultSummary = new TestResultSummary();
            Map passedResult = testResultsHandler(getPassedTests(),testContext,sqlSession);
            Map failedResult = testResultsHandler(getFailedTests(),testContext,sqlSession);
            Map ignoredResult = testResultsHandler(getSkippedTests(),testContext,sqlSession);
            totalDuration = (long)passedResult.get(durationKey) + (long)failedResult.get(durationKey) + (long)ignoredResult.get(durationKey);
            testResultSummary.setTestName(testContext.getName());
            testResultSummary.setPassed((int)passedResult.get(countKey));
            testResultSummary.setFailed((int)failedResult.get(countKey));
            testResultSummary.setIgnored((int)ignoredResult.get(countKey));
            testResultSummary.setDuration(totalDuration);
            testResultSummary.setTimestamp(currentTimestamp);

            mapper.insertTestResultSummary(testResultSummary);
            sqlSession.commit();
            sqlSession.close();
        }
    }

    private Map testResultsHandler(List<ITestResult> iTestResults,ITestContext testContext,SqlSession sqlSession){
        TestCaseManagementMapper mapper = sqlSession.getMapper(TestCaseManagementMapper.class);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        List<TestResults> testResultsList = new ArrayList<>();

        long duration = 0;

        for(ITestResult iTestResult : iTestResults){
            TestResults testResults = new TestResults();
            testResults.setTestName(testContext.getName());
            testResults.setTestMethodName(iTestResult.getName());
            testResults.setTestStatus(iTestResult.getStatus());
            testResults.setTimestamp(currentTimestamp);
            if(iTestResult.getThrowable()!=null){
                testResults.setFailedReason(iTestResult.getThrowable().getMessage());
            }
            testResultsList.add(testResults);
            //duration calculation for test summary
            duration += iTestResult.getEndMillis() - iTestResult.getStartMillis();
        }
        concurrentHashMap.put(durationKey,duration);
        concurrentHashMap.put(countKey,iTestResults.size());

        if(!testResultsList.isEmpty()){
            mapper.insertTestResultsByBatch(testResultsList);
        }

        return concurrentHashMap;
    }
}
