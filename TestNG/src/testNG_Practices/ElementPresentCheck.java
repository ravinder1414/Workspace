package testNG_Practices;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ElementPresentCheck {
	 WebDriver driver; 
	@BeforeTest  
	 public void start(){  
	  driver = new FirefoxDriver();  
	 }  
	   
	 @Test  
	 public void Test() throws InterruptedException{ 
		 
		 Thread.sleep(15000);
		 
		 driver.get("www.google.co.in");
	
	if(driver.findElements(By.name("btnK")).size() != 0){
		
		System.out.println("Element is Present");
		System.out.println(driver.findElements(By.name("btnK")).size() != 0);
	
		}else{
		System.out.println("Element is Absent");
		}

	 }
}
