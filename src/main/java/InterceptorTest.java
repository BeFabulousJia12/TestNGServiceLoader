import com.google.auto.service.AutoService;
import mapper.TestCaseManagementMapper;
import model.TestCaseInfo;
import org.apache.ibatis.session.SqlSession;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGListener;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author You Jia
 * @Date 8/1/2018 1:11 PM
 */
public class InterceptorTest implements IMethodInterceptor {
    private boolean descriptionsRecord = false;

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        System.out.println("override intercept");
        SqlSessionInitial sqlSessionInitial = new SqlSessionInitial();
        // List<IMethodInstance> result = new ArrayList<IMethodInstance>();
        if (descriptionsRecord == false){
            SqlSession sqlSession = null;
            try {
                sqlSession = sqlSessionInitial.getSqlSession("ConfigurationMySQL.xml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(sqlSession!=null){
                TestCaseManagementMapper mapper = sqlSession.getMapper(TestCaseManagementMapper.class);
                List<TestCaseInfo> testCaseInfos = new ArrayList<>();

                for (IMethodInstance method : methods) {
                    TestCaseInfo testCaseInfo = new TestCaseInfo();
                    Test testMethod = method.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class);
                    testCaseInfo.setTestName(context.getName());
                    testCaseInfo.setFeatureName(method.getMethod().getTestClass().getRealClass().getName());
                    testCaseInfo.setTestMethod(method.getMethod().getConstructorOrMethod().getName());
                    testCaseInfo.setTestSteps(testMethod.description());
                    //mapper.insertTestCase(testCaseInfo);
                    testCaseInfos.add(testCaseInfo);
                }
                System.out.println(context.getName());
                mapper.deleteTestCaseByTestName(context.getName());
                sqlSession.commit();
                mapper.insertTestCaseByBatch(testCaseInfos);
                sqlSession.commit();
                descriptionsRecord = true;
                sqlSession.close();
            }
        }
        return methods;
    }
}
