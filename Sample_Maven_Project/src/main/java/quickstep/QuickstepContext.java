package quickstep;

	


	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Collection;
	import java.util.Enumeration;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.Properties;
	import java.util.StringTokenizer;

	/**
	 * Manages context specific aspects of a test run. This can include URLs,
	 * credentials, data sources, etc.
	 * <p>
	 * It is recommended that every project creates a subclass of QuickstepContext called Context (for convention's sake)
	 * and extend it as necessary with project-specific methods.
	 * 
	 * @author mark.micallef
	 */

	public class QuickstepContext {

	    /**
	     * Stores default values for important properties. The object is populated
	     * by the initDefaults() method.
	     */
	    protected Properties defaults;

	    /**
	     * A key-value pair properties object which is used to provide a context.
	     * This is automatically serialised to quickstep.properties at the end of a
	     * test run.
	     */

	    private Properties props = null;

	    /**
	     * Environment factory
	     */
	    EnvironmentFactory environmentFactory = EnvironmentFactory.getInstance();

	    /**
	     * Instance variable for the singleton pattern.
	     */
	    private static QuickstepContext instance = null;

	    /**
	     * A generic map for sharing objects between tests and methods. This object
	     * is never serialised so it should only be used for setting context within
	     * a single test run.
	     */
	    public Map<String, Object> map = new HashMap<String, Object>();

	    /**
	     * The path to the properties file which configures the context.
	     */
	    private final String propsFile = "quickstep.properties";

	    /**
	     * A list of shutdown hooks.
	     */
	    public List<Thread> shutdownHooks = new ArrayList<Thread>();

	    /**
	     * Enumeration of all supported browsers
	     * 
	     * @author mark.micallef
	     * 
	     */
	    public enum Browser {
	        firefox, phantomjs, iphoneBrowser, iphoneApp, ipadBrowser, unkown
	    };

	    /**
	     * Enumeration of different modes in which the test framework can be
	     * executed.
	     * 
	     * <ul>
	     * <li><b>Test mode</b> runs all tests which are not tagged as unrunnable (e.g. wip, broken, manual, etc).</li>
	     * <li><b>Dev mode</b> only runs scenarios tagges as wip. This mode is usually used during development when
	     * creating/maintaining a scenario.</li>
	     * </ul>
	     * 
	     * @author mark.micallef
	     * 
	     */
	    public enum Mode {
	        test, dev, custom
	    };

	    /**
	     * Contains the location of the mobile application which is to be tested in
	     * the case of mobile app testing.
	     */
	    public String appPath = null;

	    // -------------------------------------------------------------------------
	    // Property names
	    // -------------------------------------------------------------------------

	    /**
	     * The name of the property which will contain the Environment against which
	     * test will execute.
	     */
	    public final String pEnvironment = "env";

	    /**
	     * The name of the property which will contain the mode in which the test
	     * framework will execute.
	     */
	    public final String pMode = "mode";

	    /**
	     * The name of the property which will contain any custom tags which the
	     * user wants to use for filtering scenarios.
	     */
	    public final String pTags = "cucumberTags";

	    /**
	     * The name of the property which will contain the the location of the
	     * mobile application which is to be tested in the case of mobile app
	     * testing.
	     */
	    public final String pAppPath = "appPath";

	    /**
	     * The name of the property which will contain the name of the browser which
	     * will be used during a paritcular test run.
	     */
	    public final String pBrowser = "browser";

	    /**
	     * The name of the property which will contain the URL preamble for the
	     * local environment.
	     */
	    public final String pUrlPreambleLocal = "url-preamble-local";

	    /**
	     * The name of the property which will contain the URL preamble for the
	     * sandbox environment.
	     */
	    public final String pUrlPreambleSandbox = "url-preamble-sandbox";

	    /**
	     * The name of the property which will contain the URL preamble for the test
	     * environment.
	     */
	    public final String pUrlPreambleTestwww = "url-preamble-testwww";

	    /**
	     * The name of the property which will contain the URL preamble for the
	     * nf-test environment.
	     */
	    public final String pUrlPreambleNfTest = "url-preamble-nftest";

	    /**
	     * The name of the property which will contain the URL preamble for the
	     * staging environment.
	     */
	    public final String pUrlPreambleStagingwww = "url-preamble-stagingwww";

	    /**
	     * The name of the property which will contain the URL preamble for the live
	     * environment.
	     */
	    public final String pUrlPreambleLive = "url-preamble-live";

	    /**
	     * The name of the property which will contain the URL preamble for the vm
	     * environment.
	     */
	    public final String pUrlPreambleVm = "url-preamble-vm";

	    //
	    // Constructors and Singleton implementation
	    //

	    /**
	     * Constructor for singleton pattern. It is protected and not private so
	     * that it can be reused by classes extending QuickstepContext. More
	     * specifically, Quickstep will search for
	     * com.nature.quickstep.util.CustomContext on startup and if it exists, will
	     * use it instead of QuickstepContext.
	     */
	    protected QuickstepContext() {
	        init();
	    }

	    /**
	     * Returns a singleton instance of QuickstepContext
	     * 
	     * @return A singleton instance of QuickstepContext.
	     */
	    public static QuickstepContext getInstance() {
	        if (instance == null) {

	            instance = new QuickstepContext();

	            try {
	                instance = (QuickstepContext) Class.forName("com.nature.quickstep.util.CustomContext").newInstance();
	            } catch (Exception e) {
	            }

	        }

	        return instance;
	    }

	    /**
	     * Overrides the singleton instance. This is required for the use of
	     * com.nature.quickstep.util.CustomContext when available.
	     * 
	     * @param context
	     *            A replacement context.
	     */
	    public static void overrideInstance(QuickstepContext context) {
	        instance = context;
	    }

	    /**
	     * Initialises default values of important properties.
	     */
	    protected void initDefaults() {
	        defaults = new Properties();

	        // Execution context properties
	        defaults.setProperty(pEnvironment, EnvironmentFactory.test.getId());
	        defaults.setProperty(pBrowser, Browser.phantomjs.toString());
	        defaults.setProperty(pAppPath, "");
	        defaults.setProperty(pMode, Mode.test.toString());

	        // URL Preambles
	        Collection<Environment> environments = environmentFactory.getEnvironments();

	        for (Environment env : environments) {
	            defaults.setProperty(env.getPreamblePropName(), env.getDefaultPreamble());
	        }
	    }

	    // -------------------------------------------------------------------------
	    // Arrays for management of properties
	    // Ensure that any new properties are added to these arrays.
	    // -------------------------------------------------------------------------

	    // -------------------------------------------------------------------------
	    // Getters and Setters
	    // -------------------------------------------------------------------------

	    /**
	     * Returns a handle to the environment factory being used by the context.
	     * 
	     * @return The environment factory.
	     */
	    public EnvironmentFactory getEnvironmentFactory() {
	        return environmentFactory;
	    }

	    /**
	     * Retrieves the environment against which this test run is being executed.
	     * 
	     * @return Environment instance
	     * @throws Exception
	     *             if the environment is not recognised.
	     */
	    public Environment getEnvironment() {

	        Environment result = null;

	        String sEnv = System.getProperty(pEnvironment);

	        if (sEnv == null) {
	            sEnv = props.getProperty(pEnvironment, defaults.getProperty(pEnvironment));
	        }

	        result = environmentFactory.getEnvironment(sEnv);

	        if (result == null) {
	            throw new IllegalStateException("Unrecognised environment: " + sEnv);
	        }

	        setEnvironment(result);

	        return result;
	    }

	    /**
	     * Sets the environment against which tests will be executed.
	     * 
	     * @param env
	     *            The environment
	     */
	    public void setEnvironment(Environment env) {
	        props.setProperty(pEnvironment, env.getId());
	    }

	    /**
	     * Retrieves the mode in which the test run was executed.
	     * 
	     * @return Mode instance
	     */
	    public Mode getMode() {

	        Mode result = null;

	        String sMode = System.getProperty(pMode);

	        if (sMode == null) {
	            sMode = props.getProperty(pMode, defaults.getProperty(pMode));
	        }

	        for (Mode mode : Mode.values()) {
	            if (mode.toString().equalsIgnoreCase(sMode)) {
	                result = mode;
	            }
	        }

	        setMode(result);

	        return result;
	    }

	    /**
	     * Retrieves the mode path of the mobile app being tested.
	     * 
	     * @return A string containing the path of the mobile app being tested.
	     */
	    public String getMobileAppPath() {

	        String result = System.getProperty(pAppPath);

	        if (result == null) {
	            result = props.getProperty(pAppPath, defaults.getProperty(pAppPath));
	        }

	        props.put(pAppPath, result);

	        return result;
	    }

	    /**
	     * Retrieves any custom tags required by the user. This is currently only
	     * supported as a command-line argument -Dtags={comma-separated list of
	     * tags}.
	     * 
	     * @return String tags, as passed in by the user (assumed to be a
	     *         comma-separated list)
	     */
	    public String[] getTags() {

	        String sTags = System.getProperty(pTags);

	        String[] result = new String[] {};

	        if (sTags != null) {
	            StringTokenizer tokenizer = new StringTokenizer(sTags, ",");
	            result = new String[tokenizer.countTokens()];
	            int i = 0;

	            while (tokenizer.hasMoreTokens()) {
	                result[i] = tokenizer.nextToken();
	                i++;
	            }

	        }

	        return result;
	    }

	    /**
	     * Sets the mode of execution.
	     * 
	     * @param mode
	     *            The mode of execution
	     */
	    public void setMode(Mode mode) {
	        props.setProperty(pMode, mode.toString());
	    }

	    /**
	     * Retrieves the browser on which this test run is being executed.
	     * 
	     * @return Browser (enumeration) instance
	     */
	    public Browser getBrowserType() {

	        Browser result = Browser.unkown;

	        String sBrowser = System.getProperty(pBrowser);

	        if (sBrowser == null) {
	            sBrowser = props.getProperty(pBrowser, defaults.getProperty(pBrowser));
	        }

	        for (Browser browser : Browser.values()) {
	            if (browser.toString().equalsIgnoreCase(sBrowser)) {
	                result = browser;
	            }
	        }

	        // Save property in case it came from system property
	        setBrowser(result);

	        return result;
	    }

	    /**
	     * Sets the browser in which tests will execute.
	     * 
	     * @param newBrowser
	     *            The browser
	     */
	    public void setBrowser(Browser newBrowser) {
	        props.setProperty(pBrowser, newBrowser.toString());
	    }

	    /**
	     * Returns the URL preamble for a particular environment.
	     * 
	     * @param env
	     *            The environment
	     * @return A string containing the URL preamble. See <a href=
	     *         "http://powerplant.nature.com/wiki/display/QSTP/Working+with+URLs+on+different+environments"
	     *         >wiki documentation</a> for more info.
	     * @throws Exception
	     *             If the URL preamble could not be determined.
	     */
	    public String getURLPreamble(Environment env) {

	        String preamble = props.getProperty(env.getPreamblePropName(), null);

	        if (preamble == null) {
	            throw new IllegalStateException("Could not find URL preamble for environment: " + env.getId());
	        }

	        return preamble;
	    }

	    /**
	     * Given a relative URL and an environment, this method returns an absolute
	     * URL by determining the proper preamble for the environment. See <a href=
	     * "http://powerplant.nature.com/wiki/display/QSTP/Working+with+URLs+on+different+environments"
	     * >wiki documentation</a> for more info.
	     * 
	     * @param relativeUrl
	     *            The relative URL
	     * @param env
	     *            The environment
	     * @return An absolute URL tailored to the test environment.
	     * @throws Exception
	     *             if the URL preamble could not be determined.
	     */
	    public String getURL(String relativeUrl, Environment env) {
	        return getURLPreamble(env) + relativeUrl;
	    }

	    /**
	     * Given a relative URL, this method returns an absolute URL by determining
	     * the proper preamble for the current environment. See <a href=
	     * "http://powerplant.nature.com/wiki/display/QSTP/Working+with+URLs+on+different+environments"
	     * >wiki documentation</a> for more info.
	     * 
	     * @param relativeUrl
	     *            The relative URL
	     * @return An absolute URL tailored to the test environment.
	     * @throws Exception
	     *             if the URL preamble could not be determined.
	     */
	    public String getURL(String relativeUrl) {
	        //System.out.println("====> " + relativeUrl + " is " + getURL(relativeUrl, getEnvironment()));
	        return getURL(relativeUrl, getEnvironment());
	    }

	    /**
	     * Returns the maximum number of threads to be used by the current test run.
	     * 
	     * @return The maximum number of threads to be used by the current test run.
	     */
	    public int getMaxThreads() {
	        int result = 1;

	        try {
	            result = Integer.parseInt(System.getProperty("threads", "1"));
	        } catch (Exception e) {
	            // Do nothing. Simply return default value;
	        }

	        return result;
	    }

	    // -------------------------------------------------------------------------
	    // Boiler plating
	    // -------------------------------------------------------------------------

	    /**
	     * Initialises the context by loading properties from disk. If the file does
	     * not exist, it is created with default values.
	     * 
	     * @return <code>true</code> if successful, <code>false</code> if not.
	     */
	    public boolean init() {

	        boolean result = true;

	        props = new Properties();
	        initDefaults();

	        try {
	            props.load(new FileReader(propsFile));
	        } catch (FileNotFoundException fnfe) {

	            // Do nothing. File will be saved with default values by the end of
	            // the method.

	        } catch (IOException ioe) {

	            // Unkown IO error. Print stack trace and stop tests.
	            ioe.printStackTrace();
	            result = false;
	        }

	        save();

	        return result;
	    }

	    /**
	     * Saves the properties to file. Defaults are saved where the property value
	     * has not been specified.
	     */
	    public void save() {

	        // First ensure that all default values are explicitly set.
	        // This helps users know what's configurable when they examine
	        // quickstep.properties.

	        Enumeration<Object> keys = defaults.keys();

	        while (keys.hasMoreElements()) {
	            String key = (String) keys.nextElement();
	            props.setProperty(key, props.getProperty(key, defaults.getProperty(key)));
	        }

	        // Save all properties to disk
	        try {
	            props.store(new FileWriter(propsFile), "Properties configuring the Quickstep test framework.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * Adds a shutdown hook to the java runtime and keeps track of it. Tracking
	     * is required so that we can run shutdown hooks manually in the case of
	     * single-threaded execution.
	     * 
	     * @param hook
	     */
	    public void addShutdownHook(Thread hook) {
	        Runtime.getRuntime().addShutdownHook(hook);
	        shutdownHooks.add(hook);
	    }

	    /**
	     * Executes all shutdown hooks. This is used in single-threaded mode whereby
	     * Cucumber's default behaviour of calling System.exit() after a test-run is
	     * disabled. Therefore, we must run shutdown hooks manually.
	     */
	    public void runShutdownHooks() {
	        for (Thread hook : shutdownHooks) {
	            hook.start();
	            // Ensure the hook is not executed again when System.exit() is
	            // called.
	            Runtime.getRuntime().removeShutdownHook(hook);
	        }
	    }

	    public String getWorkingDirectory() {
	        String prop = System.getProperty("workingdir");

	        if (prop == null) {
	            prop = ".";
	        }

	        return new File(prop).getAbsolutePath();
	    }

	    public String getAbsolutePath(String relativePath) {
	        return getWorkingDirectory() + File.separator + relativePath;
	    }

	}



