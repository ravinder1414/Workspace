package quickstep;

	import static org.openqa.selenium.firefox.FirefoxDriver.PROFILE;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.openqa.selenium.WebDriver;

	import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

	import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import quickstep.QuickstepContext.Browser;


	public class WebDriverUtils {

	    private static final String JAVA_NET_SOCKS_PASSWORD = "java.net.socks.password";

	    private static final String JAVA_NET_SOCKS_USERNAME = "java.net.socks.username";

	    private static final String SOCKS_PROXY_URL = "socksProxyUrl";

	    private static final String SOCKS_PROXY_PORT = "socksProxyPort";

	    private static final String SOCKS_PROXY_HOST = "socksProxyHost";

	    private static final String FTP_PROXY_URL = "ftp.proxyUrl";

	    private static final String FTP_PROXY_PORT = "ftp.proxyPort";

	    private static final String FTP_PROXY_HOST = "ftp.proxyHost";

	    private static final String HTTPS_PROXY_URL = "https.proxyUrl";

	    private static final String HTTPS_PROXY_PORT = "https.proxyPort";

	    private static final String HTTPS_PROXY_HOST = "https.proxyHost";

	    private static final String HTTP_PROXY_URL = "http.proxyUrl";

	    private static final String HTTP_PROXY_PORT = "http.proxyPort";

	    private static final String HTTP_PROXY_HOST = "http.proxyHost";

	    public static QuickstepContext context = QuickstepContext.getInstance();

	    /**
	     * Indicates whether or not the current test requires javascript to be
	     * enabled
	     */
	    public static boolean javascriptEnabledExpected = true;

	    /**
	     * Indicates whether or not the current browser has javascript enabled
	     */
	    public static boolean javascriptEnabledActual = true;

	    /**
	     * Indicates whether or not to use the proxy configuration supplied at runtime
	     */

	    private static boolean useProxyConfiguration = false;

	    /**
	     * A a static reference to browser which is used by all tests.
	     */
	    private static WebDriver browser = null;

	    /**
	     * A flag indicating whether or not the shutdown hook has been configured.
	     * The shutdown hook basically shuts down all browsers and does any required
	     * house keeping just before the JVM exits.
	     */
	    private static boolean shutDownHookConfigured = false;

	    /**
	     * Default timeout in milliseconds. This is used whenever there is a wait
	     * operation such as waiting for the browser to redirect to a different URL.
	     */
	    public static long DEFAULT_TIMEOUT = 20000;

	    /**
	     * This enumeration enumerates a number of webdriver related actions that
	     * are used as paramaters to specify behaviour of methods in this utility
	     * class.
	     * 
	     * @author mark.micallef
	     * 
	     */
	    public static enum actions {
	        DO_NOT_WAIT, WAIT_UNTIL_TRUE, WAIT_UNTIL_FALSE
	    };

	    /**
	     * Returns the static reference to the current browser.
	     * 
	     * @return Reference to the browser.
	     */
	    public static WebDriver getBrowser() {

	        // Check if current browser characteristics matches test's requirements
	        if (browser != null) {
	            if (javascriptEnabledActual != javascriptEnabledExpected) {
	                shutDown(); // This will force browser to be launched with
	                            // correct preferences
	            }
	        }

	        if (browser == null) {

	            // Sleep for a random period because browsers launching at the same
	            // time might affect each other
	            // TODO: Find a more efficient way to do this.

	            if (context.getMaxThreads() > 1) {
	                Random r = new Random();
	                int sleepSecs = r.nextInt(10);
	                System.out.println("Sleeping for " + sleepSecs + " seconds before launching browser");
	                sleep(sleepSecs * 1000);
	            }

	            Browser whichBrowser = context.getBrowserType();

	            DesiredCapabilities caps = new DesiredCapabilities();
	            caps.setJavascriptEnabled(javascriptEnabledExpected);

	            caps.setCapability("takesScreenshot", true);

	            if (isUseProxyConfiguration()) {
	                setupProxies(caps);
	            }

	            javascriptEnabledActual = javascriptEnabledExpected;

	            if (whichBrowser == Browser.firefox) {
	                browser = launchFirefox(caps);
	            } else if (whichBrowser == Browser.phantomjs) {
	                browser = launchPhantomJS(caps);
	            } else if (whichBrowser == Browser.iphoneBrowser) {
	                browser = launchIPhone(caps);
	            } else if (whichBrowser == Browser.iphoneApp) {
	                browser = launchIPhoneApp(caps);
	            } else if (whichBrowser == Browser.ipadBrowser) {
	                browser = launchIPad(caps);
	            } else {
	                throw new RuntimeException("Unkown browser: " + whichBrowser.toString());
	            }

	            if (whichBrowser != Browser.iphoneBrowser && whichBrowser != Browser.ipadBrowser
	                    && whichBrowser != Browser.iphoneApp) {
	                browser.manage().window().setSize(new Dimension(1280, 800));
	                browser.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

	                // Implicit waits for iphone and ipad are set in the
	                // launchIPhone() and launchIPad() methods
	            }

	        }

	        setShutDownHook();

	        return browser;
	    }

	    /**
	     * Sets up proxy in DesiredCapabilities if specified via system properties
	     * 
	     * Standard property names for proxy properties already existing are used
	     * http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
	     * 
	     * java options example: -Dhttp.proxyHost=10.0.0.100 -Dhttp.proxyPort=8800
	     * 
	     * Selenium docs - http://docs.seleniumhq.org/docs/04_webdriver_advanced.jsp#using-a-proxy
	     * 
	     */
	    private static void setupProxies(DesiredCapabilities caps) {
	        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
	        String httpProxy = getProxy(HTTP_PROXY_HOST, HTTP_PROXY_PORT, HTTP_PROXY_URL);
	        if (httpProxy != null) {
	            proxy.setHttpProxy(httpProxy);
	        }

	        String httpsProxy = getProxy(HTTPS_PROXY_HOST, HTTPS_PROXY_PORT, HTTPS_PROXY_URL);
	        if (httpsProxy != null) {
	            proxy.setSslProxy(httpsProxy);
	        }

	        String ftpProxy = getProxy(FTP_PROXY_HOST, FTP_PROXY_PORT, FTP_PROXY_URL);
	        if (ftpProxy != null) {
	            proxy.setFtpProxy(ftpProxy);
	        }

	        String socksProxy = getProxy(SOCKS_PROXY_HOST, SOCKS_PROXY_PORT, SOCKS_PROXY_URL);
	        if (socksProxy != null) {
	            proxy.setSocksProxy(socksProxy);
	            String username = System.getProperty(JAVA_NET_SOCKS_USERNAME);
	            if (username != null) {
	                proxy.setSocksUsername(username);
	            }
	            String password = System.getProperty(JAVA_NET_SOCKS_PASSWORD);
	            if (password != null) {
	                proxy.setSocksPassword(password);
	            }
	        }

	        if (proxy.getHttpProxy() != null || proxy.getSslProxy() != null || proxy.getFtpProxy() != null
	                || proxy.getSocksProxy() != null) {
	            caps.setCapability(CapabilityType.PROXY, proxy);
	        }
	    }

	    /**
	     * @param envHost
	     *            - standard proxy host property name
	     * @param envPort
	     *            - standard proxy port property name
	     * @param envUrl
	     *            - shortcut proxy url property name
	     * @return proxy string or null
	     */
	    private static String getProxy(String envHost, String envPort, String envUrl) {
	        String host = System.getProperty(envHost);
	        String proxy;
	        if (host != null) { //try standard java property first
	            String port = System.getProperty(envPort);
	            if (port == null || !StringUtils.isNumeric(port)) {
	                throw new IllegalStateException("When " + envHost + " is specified: " + host
	                        + ", correct port must be specified using " + envPort);
	            }
	            proxy = host + ":" + port;
	        } else { //try hacky shortcut property then
	            proxy = System.getProperty(envUrl);
	        }

	        if (proxy != null) {
	            System.out.println("Setting proxy " + envUrl + " to " + proxy); //TODO better way to inform user that proxy is used
	        }
	        return proxy; //might be null
	    }

	    /**
	     * Returns the IP address of the test machine as seen from the nature
	     * server. Depending on the environment, this could be the ip of the test
	     * machine or a public/gateway IP.
	     * 
	     * @throws Exception
	     *             if http errors occur
	     */
	    public static String getIpAsSeenFromNatureServer() throws Exception {
	        String result = null;

	        HttpClient client = new DefaultHttpClient();
	        HttpGet request = new HttpGet(context.getURL("nature.com/debug.html"));
	        HttpResponse response = client.execute(request);

	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

	        String line = "";
	        while ((line = rd.readLine()) != null) {
	            if (line.contains("Your IP address as seen by our server is ")) {
	                line = line.substring("Your IP address as seen by our server is ".length());
	                line = line.substring(0, line.indexOf("<BR>"));
	                result = line;
	                break;
	            }
	        }

	        return result;
	    }

	    /**
	     * Launches the firefox browser and returns a handle to it.
	     * 
	     * @return A handle to the firefox browser instance that has just been
	     *         launched.
	     */
	    protected static WebDriver launchFirefox(DesiredCapabilities caps) {
	    	FirefoxProfile firefoxProfile = new FirefoxProfile();
	    	firefoxProfile.setPreference("javascript.enabled", javascriptEnabledExpected);
	    	caps.setCapability(PROFILE, firefoxProfile);
	        return new FirefoxDriver(caps);
	    }

	    /**
	     * Launches the headless PhanthomJS browser and returns a handle to it.
	     * 
	     * @return A handle to the headless PhantomJS browser instance that has just
	     *         been launched.
	     */
	    protected static WebDriver launchPhantomJS(DesiredCapabilities caps) {

	        String phantomJSLocation = "/usr/local/bin/phantomjs";

	        if (System.getProperty("os.name").toLowerCase().contains("win")) {
	            phantomJSLocation = ".\\target\\test-classes\\binaries\\phantomjs\\phantomjs.exe";
	        } else if (System.getProperty("os.name").toLowerCase().contains("mac os")) {

	            phantomJSLocation = "target/test-classes/binaries/phantomjs/phantomjs";
	        } else {
	            // Assume Linux
	            phantomJSLocation = "target/test-classes/binaries/phantomjs/phantomjs";
	        }

	        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJSLocation);

	        return new PhantomJSDriver(caps);
	    }

	    protected static WebDriver launchIPhone(DesiredCapabilities caps) {
	        caps = new DesiredCapabilities();
	        caps.setCapability("device", "iPhone Simulator");
	        caps.setCapability("version", "6.1");
	        caps.setCapability("app", "safari");
	        WebDriver driver = null;
	        try {
	            driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	        return driver;
	    }

	    protected static WebDriver launchIPhoneApp(DesiredCapabilities caps) {

	        WebDriver driver = null;

	        String appPath = context.getMobileAppPath();
	        File app = new File(appPath);

	        // Check if file exists
	        if (!app.exists()) {
	            if (appPath == null || appPath.trim().isEmpty()) {
	                throw new RuntimeException(
	                        "No path for iPhone app provided. Use -DappPath={path} or set the appPath property in stf.properties.");
	            } else {
	                throw new RuntimeException("iPhone app does not exist at: " + appPath);
	            }
	        }

	        DesiredCapabilities capabilities = new DesiredCapabilities();
	        capabilities.setCapability(CapabilityType.BROWSER_NAME, "iOS");
	        capabilities.setCapability(CapabilityType.VERSION, "6.0");
	        capabilities.setCapability(CapabilityType.PLATFORM, "Mac");
	        capabilities.setCapability("app", app.getAbsolutePath());

	        try {
	            driver = new SwipeableWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
	        } catch (MalformedURLException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	        return driver;
	    }

	    protected static WebDriver launchIPad(DesiredCapabilities caps) {
	        caps = new DesiredCapabilities();
	        caps.setCapability("device", "iPad Simulator");
	        caps.setCapability("version", "6.1");
	        caps.setCapability("app", "safari");
	        WebDriver driver = null;
	        try {
	            driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	        return driver;
	    }

	    /**
	     * Sets up a shutdown hook in order to close all browsers just before the
	     * JVM exits.
	     */
	    private static void setShutDownHook() {

	        if (!shutDownHookConfigured) {
	            context.addShutdownHook(new Thread() {
	                @Override
	                public void run() {
	                    shutDown();
	                }
	            });
	            shutDownHookConfigured = true;
	        }
	    }

	    /**
	     * Convenience methods for explicit sleeps. Should be used sparingly.
	     * 
	     * @param millis
	     *            - The amount of miliseconds to sleep.
	     */
	    public static void sleep(long millis) {

	        try {
	            Thread.sleep(millis);
	        } catch (Exception e) {
	        }

	    }

	    /**
	     * Waits (until DEFAULT_TIMEOUT milliseconds have expired) for the URL of
	     * the given browser instance to contain the given substring.
	     * 
	     * @param browser
	     *            - The browser
	     * @param substring
	     *            - The substring
	     * @throws Exception
	     */
	    public static void waitForURLToContainSubstring(WebDriver browser, String substring) throws Exception {

	        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT;

	        if (browser == null) {
	            browser = WebDriverUtils.getBrowser();
	        }

	        while (!browser.getCurrentUrl().contains(substring) && System.currentTimeMillis() < deadline) {
	            sleep(500);
	        }

	        if (!browser.getCurrentUrl().contains(substring)) {
	            String msg = "Violated expectation: Expected URL (" + browser.getCurrentUrl() + ")  to contain \""
	                    + substring + "\"";
	            throw new Exception(msg);
	        }

	    }

	    /**
	     * Waits (until DEFAULT_TIMEOUT milliseconds have expired) for the URL of
	     * the given browser instance to <B><U>NOT</U></B> contain the given
	     * substring.
	     * 
	     * @param browser
	     *            - The browser
	     * @param substring
	     *            - The substring
	     */
	    public static void waitForURLNotToContainSubstring(WebDriver browser, String substring) throws Exception {

	        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT;

	        if (browser == null) {
	            browser = WebDriverUtils.getBrowser();
	        }

	        while (browser.getCurrentUrl().contains(substring) && System.currentTimeMillis() < deadline) {
	            sleep(500);
	        }

	        if (browser.getCurrentUrl().contains(substring)) {
	            String msg = "Violated expectation: Expected URL (" + browser.getCurrentUrl() + ") -->not<-- to contain \""
	                    + substring + "\"";
	            throw new Exception(msg);
	        }

	    }

	    /**
	     * Waits (until DEFAULT_TIMEOUT milliseconds have expired) for the URL of
	     * the given browser instance to be equal to the given url.
	     * 
	     * @param browser
	     *            - The browser
	     * @param url
	     *            - The url
	     * @throws Exception
	     */
	    public static void waitForURLToBe(WebDriver browser, String url) throws Exception {

	        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT;

	        if (browser == null) {
	            browser = WebDriverUtils.getBrowser();
	        }

	        while (!browser.getCurrentUrl().equals(url) && System.currentTimeMillis() < deadline) {
	            sleep(500);
	        }

	        if (!browser.getCurrentUrl().equals(url)) {
	            String msg = "Violated expectation: Expected URL (" + browser.getCurrentUrl() + ") to be \"" + url + "\"";
	            throw new Exception(msg);
	        }

	    }

	    /**
	     * Waits (until DEFAULT_TIMEOUT milliseconds have expired) for the URL of
	     * the currently used browser instance to contain the given substring.
	     * 
	     * @param url
	     *            - The url
	     */
	    public static void waitForURLToBe(String url) throws Exception {
	        waitForURLToBe(null, url);
	    }

	    /**
	     * Waits (until DEFAULT_TIMEOUT milliseconds have expired) for the URL of
	     * the currently used browser instance to <B><U>NOT</U></B> contain the
	     * given substring.
	     * 
	     * @param substring
	     *            - The substring
	     */
	    public static void waitForURLNotToContainSubstring(String substring) throws Exception {
	        waitForURLNotToContainSubstring(null, substring);
	    }

	    /**
	     * Waits (until DEFAULT_TIMEOUT milliseconds have expired) for the URL of
	     * the currently used browser instance to be equal to the given url.
	     * 
	     * @param url
	     *            - The url
	     */
	    public static void waitForURLToContainSubstring(String url) throws Exception {
	        waitForURLToContainSubstring(null, url);
	    }

	    /**
	     * Shuts down the current browser and carries out any required housekeeping.
	     * This is required for post-testsuite cleanups as well as for scenarios
	     * which request their own browser instance instead of reusing a common one.
	     */
	    public static void shutDown() {
	        if (browser != null) {
	            browser.quit();
	            browser = null;
	        }

	        // Shut down context
	        context.save();
	    }

	    public static String saveScreenshot() {
	        return saveScreenshot(null);
	    }

	    public static String saveScreenshot(String filename) {

	        String result = filename;

	        if (filename == null) {
	            result = "target" + "/" + "screenshot-" + System.currentTimeMillis() + ".jpg";
	        }

	        try {
	            File f = ((TakesScreenshot) WebDriverUtils.getBrowser()).getScreenshotAs(OutputType.FILE);
	            FileUtils.copyFile(f, new File(context.getAbsolutePath(result)));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return result;

	    }

	    /**
	     * Hits the back button on the browser.
	     */
	    public static void goBack() {
	        browser.navigate().back();
	    }

	    /**
	     * Hits the forward button on the browser.
	     */
	    public static void goForward() {
	        browser.navigate().forward();
	    }

	    /**
	     * Gives Current Url of the Browser
	     */

	    public static String getCurrentUrl() {
	        return browser.getCurrentUrl();
	    }

	    public static boolean isUseProxyConfiguration() {
	        return useProxyConfiguration;
	    }

	    public static void setUseProxyConfiguration(boolean useProxyConfiguration) {
	        WebDriverUtils.useProxyConfiguration = useProxyConfiguration;
	    }

	}

