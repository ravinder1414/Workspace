package quickstep;

	import java.util.Collection;
	import java.util.HashMap;
	import java.util.Map;

	/**
	 * Manages default and custom environments. Environments are used to adapt test
	 * runs to a particular environment. This currently involves simply adapting a
	 * URL to point to an appropriate location but could grow in future.
	 * 
	 * @author mark.micallef
	 */
	public class EnvironmentFactory {

	    protected Map<String, Environment> environments = new HashMap<String, Environment>();

	    private static EnvironmentFactory instance = null;

	    // Default environments
	    public static Environment dev = new Environment("local", "http://10.78.74.213");
	    public static Environment test = new Environment("test", "http://10.78.4.202");
	    public static Environment Orion_B2B = new Environment("	B2B", "http://10.78.100.195/kuleads/b2b/b2bleadsentry.aspx");
	    public static Environment staging = new Environment("staging-www", "http://staging-www.");
	    public static Environment live = new Environment("live", "http://www.");
	    public static Environment vm = new Environment("vm", "http://vm.");

	    protected EnvironmentFactory() {
	        initDefaultEnvironments();
	    }

	    public static EnvironmentFactory getInstance() {
	        if (instance == null) {
	            instance = new EnvironmentFactory();
	        }

	        return instance;
	    }

	    public void initDefaultEnvironments() {
	        environments.put("dev", EnvironmentFactory.dev);
	       
	        environments.put("test", EnvironmentFactory.test);
	        environments.put("Orion_B2B", EnvironmentFactory.Orion_B2B);
	        //environments.put("staging-www", EnvironmentFactory.stagingwww);
	        environments.put("live", EnvironmentFactory.live);
	        environments.put("vm", EnvironmentFactory.vm);
	    }

	    public Collection<Environment> getEnvironments() {
	        return environments.values();
	    }

	    /**
	     * Retrieves the environment against which this test run is being executed.
	     * 
	     * @return Environment instance
	     * @throws Exception
	     *             if the environment is not recognised.
	     */
	    public Environment getEnvironment(String id) {
	        return environments.get(id);
	    }

	    public void addEnvironment(Environment env) {
	        environments.put(env.getId(), env);
	    }

	}

