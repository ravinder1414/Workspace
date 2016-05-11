package quickstep;

	import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import quickstep.SeleniumWaitBuilder.WaitBuilder;
import quickstep.SeleniumWaitBuilder.WaitCondition;
import quickstep.SeleniumWaitBuilder.WaitInit;

//import com.nature.quickstep.util.SeleniumWaitBuilder.WaitInit;

//import quickstep.SeleniumWaitBuilder.WaitInit;

//import com.nature.quickstep.util.SeleniumWaitBuilder.WaitInit;

//import com.nature.quickstep.util.SeleniumWaitBuilder.WaitInit;

	//import com.nature.quickstep.util.QuickstepContext;
	//import com.nature.quickstep.util.SeleniumWaitBuilder.WaitBuilder;
	//import com.nature.quickstep.util.SeleniumWaitBuilder.WaitCondition;
	//import com.nature.quickstep.util.SeleniumWaitBuilder.WaitInit;
	//import com.nature.quickstep.util.WebDriverUtils;

	/**
	 * This abstract class provides core functionality for all page objects and also
	 * specifies a number of methods which should be implemented by each page
	 * object. All page objects should be subclasses of this class.
	 * 
	 * @author mark.micallef
	 * 
	 */
	public abstract class PageObject {

	    /**
	     * Cached value violates lifacycle of WebDriver instance
	     * This value is going to be removed in Quickstep 1.2
	     * 
	     * Use browser() instead of accessing this field directly
	     * 
	     */
	    
	    protected WebDriver browser;

	    /**
	     * Use context() instead of accessing this field directly
	     */
	    //@Deprecated
	    //protected QuickstepContext context = QuickstepContext.getInstance();

	    /**
	     * Instantiates the page object given a reference to a WebDriver.
	     * 
	     * @param browser
	     */
	    public PageObject(WebDriver browser) {

	        if (browser == null) {
	            this.browser = WebDriverUtils.getBrowser();
	        } else {
	            this.browser = browser;
	        }

	    }

	    /**
	     * Instantiates the page object using a default web browser.
	     */
	    public PageObject() {
	        this(null);
	    }

	    /**
	     * @return WebDriver instance
	     */
	    protected WebDriver browser() {
	        return WebDriverUtils.getBrowser();
	    }

	    /**
	     * @return QuickstepContext instance
	     */
	    protected QuickstepContext context() {
	        return QuickstepContext.getInstance();
	    }

	    /**
	     * Navigates to the page being represented, ideally using the same method
	     * that a user would. <BR>
	     * <BR>
	     * <b>Note:</b>Should be implemented by every page object.
	     * 
	     */
	    public abstract void navigateTo();

	    /**
	     * Checks whether the page or object represented by the page object is
	     * currently present in the browser.
	     * 
	     * @return <code>true</code> if present, <code>false</code> if not
	     */
	    public abstract boolean isPresent();

	    /**
	     * Conditional wait after click
	     */
	    protected WaitInit click(By locator) {
	        WebElement element = browser().findElement(locator);
	        return click(element);
	    }

	    /**
	     * Conditional wait after click
	     */
	    protected WaitInit click(WebElement element) {
	        element.click();
	        return ensure();
	    }

	    /**
	     * Conditional wait after navigation
	     * Depends on correct navigateTo() and isPresent() implementation
	     */
	    public void navigateTo(int seconds) {
	        this.navigateTo();
	        ensure().page(this).seconds(seconds);
	    }

	    public WaitBuilder ensure() {
	        return new WaitBuilder(browser());
	    }

	    public WaitCondition refuse() {
	        return new WaitBuilder(browser()).refuse();
	    }

	    /**
	     * Navigate with optional waiting
	     * 
	     * XXX Probably should not be here...
	     */
	    public WaitInit goTo(String url) {
	        browser().navigate().to(url);
	        return ensure();
	    }

	}



