package frameResize;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class FrameResize {
	public class ResizeExample {
		WebDriver driver;

		@Test
		public void testToResizeElement() {

			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			driver.navigate().to("http://jqueryui.com/resizable/");
			//WebDriverWait wait = new WebDriverWait(driver, 5);
			//wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector(".demo-frame")));
			WebElement resizeableElement = driver.findElement(By.cssSelector(".ui-resizable-handle.ui-resizable-se"));
			Actions action = new Actions(driver);
			action.clickAndHold(resizeableElement).moveByOffset(100, 200).release().build().perform();
			
			//resize(resizeableElement, 10, 50);
			
			/*// Waiting 30 seconds for an element to be present on the page, checking
			 
			  // for its presence once every 5 seconds.
			 
			  Wait wait = new FluentWait(driver)
			 
			    wait.withTimeout(30, SECONDS);
			 
			    .pollingEvery(5, SECONDS)
			 
			    .ignoring(NoSuchElementException.class);
			 
			  WebElement foo = wait.until(driver.findElement(By.id("foo")); 
			 
			    //public WebElement apply(WebDriver driver) {
			 
			    //return driver.findElement(By.id("foo"));
			 
			    }
			 
			   */};
		
		//@Test
		public void resize(WebElement elementToResize, int xOffset, int yOffset) {
			try {
				if (elementToResize.isDisplayed()) {
					Actions action = new Actions(driver);
					action.clickAndHold(elementToResize).moveByOffset(10, 20).release().build().perform();
				} else {
					System.out.println("Element was not displayed to drag");
				}
			} catch (StaleElementReferenceException e) {
				System.out.println("Element with " + elementToResize + "is not attached to the page document "	+ e.getStackTrace());
			} catch (NoSuchElementException e) {
				System.out.println("Element " + elementToResize + " was not found in DOM " + e.getStackTrace());
			} catch (Exception e) {
				System.out.println("Unable to resize" + elementToResize + " - "	+ e.getStackTrace());
			}
		}

	}

}
