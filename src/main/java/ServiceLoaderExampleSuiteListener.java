import com.google.auto.service.AutoService;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestNGListener;

/**
 * @Author You Jia
 * @Date 8/2/2018 2:40 PM
 */
@AutoService(ITestNGListener.class)
public class ServiceLoaderExampleSuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        System.out.println("on Start " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("on finish " + suite.getName());
    }
}
