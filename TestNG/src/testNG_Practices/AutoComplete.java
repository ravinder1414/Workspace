package testNG_Practices;


	


	import java.util.List;

	import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

	public class AutoComplete {

		WebDriver driver;
		WebDriverWait wait;

		String URL = "http://jqueryui.com/autocomplete/";
		private By frameLocator = By.className("demo-frame");
		private By tagText = By.id("tags");

		@BeforeClass
		public void Setup() {
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			wait = new WebDriverWait(driver, 5);
		}
 //To find through by text
		/*@Test
		public void rightClickTest() throws InterruptedException {
			driver.navigate().to(URL);
			WebElement frameElement=driver.findElement(frameLocator);
			driver.switchTo().frame(frameElement);
			//wait.until(ExpectedConditions.presenceOfElementLocated(tagText));
			WebElement textBoxElement = driver.findElement(tagText);
			textBoxElement.sendKeys("a");
			Thread.sleep(10000);
			
			//This is the id of autoOption values
			//WebElement autoOptions = driver.findElement(By.id("ui-id-1"));
		//wait.until(ExpectedConditions.visibilityOf(autoOptions));
		//List<WebElement> optionsToSelect = autoOptions.findElements(By.tagName("li"));
			
			//To fetch all results based upon tah values
			List<WebElement> optionsToSelect = driver.findElements(By.tagName("li"));
		
		for(WebElement option : optionsToSelect){
			System.out.println(option.getText());
			
	        if(option.getText().equals("Java")) {
	        	
	            option.click();
	            
	            break;
	        */
		
		//To find through by Index
		@Test(priority=0)
		public void selectOptionWithIndex(int indexToSelect) {
			
			try {
				driver.navigate().to(URL);
				WebElement frameElement=driver.findElement(frameLocator);
				driver.switchTo().frame(frameElement);
				//wait.until(ExpectedConditions.presenceOfElementLocated(tagText));
				WebElement textBoxElement = driver.findElement(tagText);
				textBoxElement.sendKeys("a");
				Thread.sleep(10000);
				//WebElement autoOptions = driver.findElement(By.id("ui-id-1"));
				//wait.until(ExpectedConditions.visibilityOf(autoOptions));

				List<WebElement> optionsToSelect = driver.findElements(By.tagName("li"));
			        if(indexToSelect<=optionsToSelect.size()) {
			        	System.out.println("Trying to select based on index: "+indexToSelect);
			           optionsToSelect.get(indexToSelect).click();
			        }
			} 		
			catch (NoSuchElementException e) {
				System.out.println(e.getStackTrace());
			}
			catch (Exception e) {
				System.out.println(e.getStackTrace());
			}
		}
		@Test(priority=1)
		public void mainMethod(){
		
		selectOptionWithIndex(2);
		
		}
	
	
	@AfterClass
	public void tearDown() {
		driver.close();
	}
	}

	        
					   
			
			
			

