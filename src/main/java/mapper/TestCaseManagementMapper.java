package mapper;

import model.TestCaseInfo;
import model.TestResultSummary;
import model.TestResults;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author You Jia
 * @Date 8/1/2018 1:16 PM
 */
@Mapper
public interface TestCaseManagementMapper {
    //Get All test cases
    List<TestCaseInfo> getAllTestCaseInfo();
    //Insert test cases one by one
    void insertTestCase(TestCaseInfo testCaseInfo);
    //Insert test cases by batch
    void insertTestCaseByBatch(List<TestCaseInfo> testCaseInfoList);
    //Delete test cases by test name
    void deleteTestCaseByTestName(String name);
    //Insert into test_results
    void insertTestResultsByBatch(List<TestResults> testResultsList);
    //Insert into test_result_summary
    void insertTestResultSummary(TestResultSummary testResultSummary);
    //Query the latest test result

    //Query test results according to different testName and timestamp
    List<TestResults> getTestResults(@Param("testName")String testName, @Param("timestamp")Timestamp timestamp);
    //Query latest test result summaries for different test_name
    List<TestResultSummary> getLatestTestResultSummary();
}
