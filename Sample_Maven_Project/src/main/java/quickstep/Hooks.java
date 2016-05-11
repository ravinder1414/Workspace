package quickstep;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;



import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Hooks {

    QuickstepContext context = QuickstepContext.getInstance();

    /**
     * Used to time scenarios when required
     */
    private long startTimeStamp;

    /**
     * Flag indicating whether or not the test runner has been initialised.
     */
    private boolean initialised = false;

    @Before
    public void init() {
        if (!initialised) {
            //Init code here
            initialised = true;
        }
    }

    @Before("@cleanbrowser")
    public void resetBrowserInstance() {
        WebDriverUtils.shutDown();
    }

    @Before("@javascriptoff")
    public void requireJavascriptOff() {
        WebDriverUtils.javascriptEnabledExpected = false;
    }

    @After("@javascriptoff")
    public void requireJavascriptOn() {
        WebDriverUtils.javascriptEnabledExpected = true;
    }

    @Before("@timed")
    public void startTimer() {
        startTimeStamp = System.currentTimeMillis();
    }

    @After("@timed")
    public void stopTimer(Scenario scenario) {
        int durationInSeconds = (int) ((System.currentTimeMillis() - startTimeStamp) / 1000);
        String message = "time-taken-seconds," + Integer.toString(durationInSeconds);
        scenario.write(message);
    }

    @Before("@useProxy")
    public void usePorxyOn() {
        WebDriverUtils.setUseProxyConfiguration(true);
    }

    @After("@useProxy")
    public void userProxyOff() {
        WebDriverUtils.setUseProxyConfiguration(false);
    }

    @After
    public void afterScenario(Scenario scenario) throws Exception {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) WebDriverUtils.getBrowser()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png");
        }
    }

}
