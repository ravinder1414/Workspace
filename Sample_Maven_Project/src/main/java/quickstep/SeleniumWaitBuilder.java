package quickstep;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import quickstep.SeleniumWaitBuilder.WaitCondition;


/**
 * 
 * @author martin.vanek
 * 
 */
public class SeleniumWaitBuilder {

    public interface WaitInit {

        /**
         * Positive condition outcome expected
         */
        public WaitCondition ensure();

        /**
         * Negative condition outcome expected
         */
        public WaitCondition refuse();
    }

    /**
     * Term
     */
    public interface WaitTime {

        public void seconds(int seconds);
    }

    public interface WaitNext extends WaitInit, WaitTime {

    }

    public interface WaitString {

        public WaitNext equals(String value);

        public WaitNext contains(String value);

        public WaitNext endsWith(String value);

        public WaitNext matches(String value);
    }

    /**
     * Non Term
     */
    public interface WaitCondition {

        /**
         * Wait for presence of element
         */
        public WaitNext element(By locator);

        WaitCondition ensure();

		/**
         * Wait for url becomes...
         * 
         * public WaitTime url(ConditionType type, String value);
         */
        public WaitString url();

        public WaitString title();

        /**
         * PageObject.isPresent() is used as condition
         */
        public WaitNext page(PageObject page);

        /**
         * Wait for root tag become $lt;hmtl$gt; - use to skip redirects
         */
        public WaitNext html();

        /**
         * Generic condition method
         */
        public WaitTime condition(ExpectedCondition<?> condition);

    }

    public static class WaitBuilder implements WaitInit, WaitNext, WaitCondition, WaitTime {

        private final WebDriver driver;

        private boolean positive;

        private ExpectedCondition<?> ensure;

        private ExpectedCondition<?> refuse;

        public WaitBuilder(WebDriver driver) {
            this.positive = true; //important
            this.driver = driver;
        }

       //@Override
        public WaitCondition ensure() {
            this.positive = true;
            return this;
        }

        //@Override
        public WaitCondition refuse() {
            this.positive = false;
            return this;
        }

        public WaitNext element(By locator) {
            if (locator == null) {
                throw new IllegalArgumentException("Null element locator");
            }
            ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(locator);
            condition(condition);
            return this;
        }

        public WaitNext html() {
            ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(By.xpath("/html"));
            condition(condition);
            return this;
        }

        public WaitString url() {
            return new WaitUrlImpl();
        }

        public WaitString title() {
            return new WaitTitleImpl();
        }

        public WaitBuilder page(final PageObject page) {
            ExpectedCondition<Boolean> condition = new PagePresentCondition(page);
            condition(condition);
            return this;

        }

        //@Override
        public WaitTime condition(ExpectedCondition<?> condition) {
            if (condition == null) {
                throw new IllegalArgumentException("Null condition");
            }
            if (positive) {
                if (ensure != null) {
                    //XXX this check wouldn't be necessary if builder API was better
                    throw new IllegalStateException("Ensure condition is already set to " + ensure);
                }
                ensure = condition;
            } else {
                //XXX this check wouldn't be necessary if builder API was better
                if (refuse != null) {
                    throw new IllegalStateException("Refuse condition is already set to " + refuse);
                }
                refuse = condition;
            }
            return this;
        }

        //@Override
        public void seconds(int seconds) {
            WebDriverWait wait = new WebDriverWait(driver, seconds);
            if (ensure != null) {
                if (refuse != null) {
                    wait.until(new DualityCondition(ensure, refuse));
                } else {
                    wait.until(ensure); //until true or TimeoutException
                }
            } else {
                if (refuse != null) {
                    try {
                        Object result = wait.until(refuse);
                        if (result != null) {
                            if (result instanceof Boolean) {
                                if ((Boolean) result) {
                                    throw new InvalidElementStateException("Unwanted happened: " + refuse);
                                }
                            } else {
                                throw new InvalidElementStateException("Unwanted result found: " + result
                                        + " of condition: " + refuse);
                            }

                        }
                    } catch (org.openqa.selenium.TimeoutException tx) {
                        //this is ok - unwanted condition did not happened
                    }
                } else {
                    throw new IllegalStateException("Both ensure and refuse conditions null");
                }
            }

        }

        class WaitUrlImpl implements WaitString {

            //@Override
            public WaitNext equals(String value) {
                URLCondition condition = new URLCondition(value, ConditionType.EQUALS);
                condition(condition);
                return WaitBuilder.this;
            }

            //@Override
            public WaitNext contains(String value) {
                URLCondition condition = new URLCondition(value, ConditionType.CONTAINS);
                condition(condition);
                return WaitBuilder.this;
            }

            //@Override
            public WaitNext endsWith(String value) {
                URLCondition condition = new URLCondition(value, ConditionType.ENDS_WITH);
                condition(condition);
                return WaitBuilder.this;
            }

            //@Override
            public WaitNext matches(String value) {
                URLCondition condition = new URLCondition(value, ConditionType.MATCH_REGEX);
                condition(condition);
                return WaitBuilder.this;
            }
        }

        class WaitTitleImpl implements WaitString {

            //@Override
            public WaitNext equals(String value) {
                TitleCondition condition = new TitleCondition(value, ConditionType.EQUALS);
                condition(condition);
                return WaitBuilder.this;
            }

            //@Override
            public WaitNext contains(String value) {
                TitleCondition condition = new TitleCondition(value, ConditionType.CONTAINS);
                condition(condition);
                return WaitBuilder.this;
            }

            //@Override
            public WaitNext endsWith(String value) {
                TitleCondition condition = new TitleCondition(value, ConditionType.ENDS_WITH);
                condition(condition);
                return WaitBuilder.this;
            }

            //@Override
            public WaitNext matches(String value) {
                TitleCondition condition = new TitleCondition(value, ConditionType.MATCH_REGEX);
                condition(condition);
                return WaitBuilder.this;
            }
        }
    }

    static class DualityCondition implements ExpectedCondition<Boolean> {

        private ExpectedCondition<?> ensure;

        private ExpectedCondition<?> refuse;

        public DualityCondition(ExpectedCondition<?> ensure, ExpectedCondition<?> refuse) {
            this.ensure = ensure;
            this.refuse = refuse;
        }

        //@Override
        public Boolean apply(WebDriver input) {
            //check for refuse first
            Object result = refuse.apply(input);
            if (result != null) {
                if (result instanceof Boolean) {
                    if ((Boolean) result) {
                        throw new InvalidElementStateException("Unwanted happened: " + refuse + " while expecting: "
                                + ensure);
                    } else {
                        //false is ok - unwanted didn't happend
                    }
                } else {
                    //Unwanted WebElement or so... DIE!
                    throw new InvalidElementStateException("Unwanted result: " + result + " for condition: " + refuse
                            + " while expecting: " + ensure);
                }
            } else {
                //null is ok
            }

            //ok, not refused - check if expected is there 
            result = ensure.apply(input);
            if (result != null) {
                if (result instanceof Boolean) {
                    return (Boolean) result;
                } else {
                    return true; //Wanted WebElement or so...
                }
            } else {
                return false;
            }
        }

    }

    static class PagePresentCondition implements ExpectedCondition<Boolean> {

        private final PageObject page;

        private final boolean positive;

        public PagePresentCondition(PageObject page) {
            this(page, true);
        }

        public PagePresentCondition(PageObject page, boolean positive) {
            if (page == null) {
                throw new IllegalArgumentException("Null PageObject");
            }
            this.page = page;
            this.positive = positive;
        }

        //@Override
        public Boolean apply(WebDriver input) {
            boolean present = page.isPresent();
            if (positive) {
                return present;
            } else {
                if (present) {
                    throw new InvalidElementStateException("Unwanted page appeared: " + page);
                } else {
                    return false; //not present -> continue waiting
                }
            }
        }

        @Override
        public String toString() {
            return "Page " + page;
        }
    }

    static class NegatedCondition implements ExpectedCondition<Boolean> {

        private ExpectedCondition<?> condition;

        public NegatedCondition(ExpectedCondition<?> condition) {
            this.condition = condition;
        }

        //@Override
        public Boolean apply(WebDriver input) {
            Object result = condition.apply(input);
            if (result != null) {
                if (result instanceof Boolean) {
                    if ((Boolean) result) {
                        throw new IllegalStateException("Error Condition happened: " + condition);
                    } else {
                        return false; //keep checking
                    }
                } else {
                    throw new IllegalStateException("Error Condition happened: " + condition);
                }
            } else {
                return false;
            }
        }
    }

    public static enum ConditionType {
        EQUALS, CONTAINS, ENDS_WITH, MATCH_REGEX;
    }

    static abstract class AbstractStringCondition implements ExpectedCondition<Boolean> {

        private final String expectedValue;

        private final ConditionType type;

        private final boolean positive;

        private transient String currentValue;

        protected abstract String getCurrentValue(WebDriver driver);

        public AbstractStringCondition(String value, ConditionType type) {
            this(value, type, true);
        }

        public AbstractStringCondition(String value, ConditionType type, boolean positive) {
            if (value == null || value.length() == 0) {
                throw new IllegalArgumentException("Blank condition value " + value);
            }
            this.expectedValue = value;
            if (type == null) {
                throw new IllegalArgumentException("Null condition type");
            }
            this.type = type;
            this.positive = positive;
        }

        //@Override
        public Boolean apply(WebDriver driver) {
            currentValue = getCurrentValue(driver);
            if (currentValue == null) {
                return false;
            }
            boolean result;
            switch (type) {
                case EQUALS:
                    result = currentValue.equals(expectedValue);
                    break;
                case CONTAINS:
                    result = currentValue.contains(expectedValue);
                    break;
                case ENDS_WITH:
                    result = currentValue.endsWith(currentValue);
                    break;
                case MATCH_REGEX:
                    result = currentValue.matches(expectedValue);
                    break;
                default:
                    throw new IllegalStateException("Unsupported type: " + type);
            }
            return positive ? result : !result;
        }

        @Override
        public String toString() {
            return String.format("Value to check: \"%s\" type: %s. Current value: \"%s\"", expectedValue, type,
                    currentValue);
        }
    }

    static class URLCondition extends AbstractStringCondition {

        public URLCondition(String value, ConditionType type, boolean positive) {
            super(value, type, positive);
        }

        public URLCondition(String value, ConditionType type) {
            super(value, type, true);
        }

        @Override
        protected String getCurrentValue(WebDriver driver) {
            return driver.getCurrentUrl();
        }

    }

    static class TitleCondition extends AbstractStringCondition {

        public TitleCondition(String value, ConditionType type, boolean positive) {
            super(value, type, positive);
        }

        public TitleCondition(String value, ConditionType type) {
            super(value, type, true);
        }

        @Override
        protected String getCurrentValue(WebDriver driver) {
            return driver.getTitle();
        }

    }
}
