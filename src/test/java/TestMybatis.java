import org.testng.Assert;
import org.testng.IMethodInterceptor;
import org.testng.annotations.Test;

import java.util.ServiceLoader;

/**
 * @Author You Jia
 * @Date 8/1/2018 3:34 PM
 */
public class TestMybatis {
    @Test(description = "Define test cases02")
    public void testcase_02(){
        Assert.assertEquals(3, 3);

    }
    @Test(description = "ServiceLoader")
    public void test(){
        ServiceLoader<IMethodInterceptor> serviceLoader = ServiceLoader
                .load(IMethodInterceptor.class);
        for (IMethodInterceptor service : serviceLoader) {
            System.out.println(service);
        }
    }
}
