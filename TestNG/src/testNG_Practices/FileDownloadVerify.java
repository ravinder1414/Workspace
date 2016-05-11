package testNG_Practices;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;



import java.io.File;
import java.io.FileFilter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;


public class FileDownloadVerify {
	
	
		
		private WebDriver driver;
		
		private static String downloadPath = "D:\\seleniumdownloads";
		private String URL="http://spreadsheetpage.com/index.php/file/C35/P10/"; 
		
		@BeforeClass
		public void testSetup() throws Exception{
			//driver = new FirefoxDriver(firefoxProfile());
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
		}
		
		@Test
		public void example_VerifyDownloadWithFileName()  {
			driver.get(URL);
		    driver.findElement(By.linkText("mailmerge.xls")).click();
		    //Assert.assertTrue(isFileDownloaded(downloadPath, "mailmerge.xls"), "Failed to download Expected document");
		    //Assert.assertTrue(downloadPath, "mailmerge.xls"b, "Failed to download Expected document");
		}
		
	        @Test
		public void example_VerifyDownloadWithFileExtension()  {
			driver.get(URL);
		    driver.findElement(By.linkText("mailmerge.xls")).click();
		    //Assert.assertTrue(isFileDownloaded_Ext(downloadPath, ".xls"), "Failed to download document which has extension .xls");
		}

		//@Test
		public void example_VerifyExpectedFileName() {
			driver.get(URL);
		    driver.findElement(By.linkText("mailmerge.xls")).click();
		    //File getLatestFile = getLatestFilefromDir(downloadPath);
		    //String fileName = getLatestFile.getName();
		    //Assert.assertTrue(fileName.equals("mailmerge.xls"), "Downloaded file name is not matching with expected file name");
		}
		

		@AfterClass
		public void tearDown() {
			driver.quit();
		}

}
