package quickstep;


	/**
	 * Encapsulates information about a test environment.
	 * 
	 * @author mark.micallef
	 */
	public class Environment {

	    String id;
	    String preamble;

	    public Environment(String id, String preamble) {
	        this.id = id;
	        this.preamble = preamble;
	    }

	    public String getId() {
	        return id;
	    }

	    public String getDefaultPreamble() {
	        return preamble;
	    }

	    public String getPreamblePropName() {
	        return "pUrlPreamble-" + getId();
	    }

	    @Override
	    public String toString() {
	        return getId();
	    }

	    public boolean equals(Environment env) {
	        return this.getId().equals(env.getId());
	    }

	}
