package org.tonylin.util.selenium;
import java.io.File;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;


public class SeleniumFactory {
	static private Logger logger = LoggerFactory
			.getLogger(SeleniumFactory.class);
	static final public String BASEURL = "http://lcoalhost"; 
	
	static private String getDefaultChromePath(){
		String defaultPath = "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe";
		if( new File(defaultPath).exists() ){
			return defaultPath;
		} else {
			return "C:/Program Files/Google/Chrome/Application/chrome.exe";
		}
	}
	
	static private WebDriver createChromeDriver(ChromeOptions aOptions){
		WebDriver driver = null;
		try {
			driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());
		} catch( Exception e ){
			logger.warn("Remote chrome driver doesn't exist. Create local driver.");
			driver = new ChromeDriver(aOptions);
		}
		return driver;
	}
	
	static public Selenium createInstance(String type){
		if(  type != null && type.equals(BrowserType.CHROME) ){
			File currentDir = new File(".");
			String driverPath = currentDir.getAbsolutePath() + "/webdrivers/chrome/chromedriver.exe";
			String data = currentDir.getAbsolutePath() + "/webdrivers/chrome/data";
			System.setProperty("webdriver.chrome.driver",driverPath);
			
			ChromeOptions options = new ChromeOptions();
			options.addArguments("user-data-dir="+data);
			options.addArguments("--start-maximized");
			options.addArguments("--test-type");
			options.addArguments("--disable-popup-blocking");
		
			WebDriver driver = createChromeDriver(options);
			return new WebDriverBackedSelenium(driver, BASEURL);
		}
		
		Selenium defaultSelenium = new DefaultSelenium("localhost", 4444, "googlechrome " + getDefaultChromePath(), BASEURL);
		defaultSelenium.start();
		return defaultSelenium;
	}
	
}
