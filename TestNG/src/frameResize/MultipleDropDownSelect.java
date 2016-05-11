package frameResize;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

	public class MultipleDropDownSelect {
		
			WebDriver driver;

			@Test
			public void testToResizeElement() throws InterruptedException {

				driver = new FirefoxDriver();
				driver.manage().window().maximize();
				
				Thread.sleep(20000);
				driver.navigate().to("http://toolsqa.com/automation-practice-form/");
				WebDriverWait wait = new WebDriverWait(driver, 5);
				//wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector(".demo-frame")));
				//WebElement resizeableElement = driver.findElement(By.cssSelector(".ui-resizable-handle.ui-resizable-se"));
				
				Select oSelect = new Select(driver.findElement(By.name("selenium_commands")));
				
				Actions builder = new Actions(driver);
				builder.keyDown(Keys.CONTROL).click(oSelect.getOptions().get(2)).click(oSelect.getOptions().get(3)).click(oSelect.getOptions().get(4))
				.keyUp(Keys.CONTROL);
				
				

				builder.build().perform();
				//Actions action = new Actions(driver);
				//action.clickAndHold(resizeableElement).moveByOffset(100, 200).release().build().perform();
				
				//resize(resizeableElement, 10, 50);
			}
	

	}



