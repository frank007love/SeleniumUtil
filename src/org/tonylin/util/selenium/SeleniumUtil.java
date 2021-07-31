package org.tonylin.util.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tonylin.util.ThreadUtil;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class SeleniumUtil {
	static private Logger logger = LoggerFactory
			.getLogger(SeleniumUtil.class);
	
	static private String DEFAULT_TIMEOUT = "60000"; 
	
	/**
	 * 
	 * @param aSelenium
	 * @param aTarget
	 * @param aTimeout timeout with milli-seconds
	 * @throws SeleniumException
	 */
	static public void waitForElementPresent(Selenium aSelenium, String aTarget, String aTimeout) throws SeleniumException {
		long timeout = translateTimeout(aTimeout);
		waitForElement(aSelenium, aTarget, timeout, true);
	}
	
	static private void waitForElement(Selenium aSelenium, String aTarget, long aTimeout, boolean aPresentOperation){
		int count = 0;
		while( aSelenium.isElementPresent(aTarget) != aPresentOperation ){
			if( count >= aTimeout )
				throw new SeleniumException(String.format("Element %s is not %s.", aTarget, aPresentOperation ? "present" : "disappear"));
			count++;
			ThreadUtil.sleep(1000);
		}
	}
	
	static private long translateTimeout(String aTimeout){
		if(  aTimeout != null ){
			return Integer.parseInt(aTimeout) / 1000;
		}
		return 30;
	}
	
	static public void waitForElementDisappear(Selenium aSelenium, String aTarget, String aTimeout) throws SeleniumException {
		long timeout = translateTimeout(aTimeout);
		waitForElement(aSelenium, aTarget, timeout, false);
	}
	
	static public void waitForElementDecrease(Selenium aSelenium, String aTarget, int aPreviousCount){
		int count = 0;
		do {
			Number currentCount = aSelenium.getXpathCount(aTarget);
			//System.out.println("currentCount.intValue="+currentCount.intValue() + ", aPreviousCount="+aPreviousCount);
			if( currentCount.intValue() <  aPreviousCount ) {
				return;
			}
			ThreadUtil.sleep(200);
			count++;
		} while( count < 15);
		throw new SeleniumException("Element " + aTarget  + " doesn't decrease.");
	}
	
	static public void clickElement(Selenium aSelenium, String aTarget) throws SeleniumException {
		SeleniumException lastExecption = null;
		int count = 0;
		do {
			try {
				logger.debug("wait {}", aTarget);
				waitForClickable(aSelenium, aTarget, 2);
				logger.debug("click {}", aTarget);
				aSelenium.click(aTarget);

				return;
			} catch( SeleniumException e ){
				count++;
				lastExecption = e;
			}
			ThreadUtil.sleep(200);
		} while( count < 20);
		throw lastExecption;
	}
	
	static public void checkSelenium(Selenium aSelenium){
		aSelenium.getLocation();
	}
	
	static public void controlScrolbar(Selenium aSelenium, int aX, int aY){
		aSelenium.runScript(String.format("window.scrollBy(%d,%d)", aX, aY));
	}
	
	static public String getFlashPID(Selenium aSelenium){
		return getProcessID(aSelenium, "Pepper Plugin");
	}
	
	static public WebDriverBackedSelenium getWebDriverBackedSelenium(Selenium aSelenium){
		if( !(aSelenium instanceof WebDriverBackedSelenium) ){
			throw new SeleniumException("The operation only supports for web driver.");
		}
		return (WebDriverBackedSelenium)aSelenium;
	}
	
	static public void waitForClickable(Selenium aSelenium, String aXPath, long aTimeout){
		if( aXPath.startsWith("xpath=") ) {
			aXPath = aXPath.replace("xpath=", "");
		}
		waitFor( aSelenium, ExpectedConditions.elementToBeClickable(By.xpath(aXPath)), aTimeout);
	}
	
	static public void waitFor(Selenium aSelenium, ExpectedCondition<WebElement>  aCondition, long aTimeout){
		WebDriverBackedSelenium selenium = 	getWebDriverBackedSelenium(aSelenium);
		WebDriver driver =  selenium.getWrappedDriver();

		WebDriverWait wait = new WebDriverWait(driver, aTimeout);
		wait.until(aCondition);
	}
	
	static public String getProcessID(Selenium aSelenium, String aProcName){
		if( !(aSelenium instanceof WebDriverBackedSelenium) )
			return null;
		WebDriverBackedSelenium selenium = 	getWebDriverBackedSelenium(aSelenium);
		WebDriver driver =  selenium.getWrappedDriver();
		
		String mainWindow = driver.getWindowHandle();
		logger.debug("main window id: {}", mainWindow);
		
		aSelenium.openWindow("", "memory-redirect");
		aSelenium.waitForPopUp("memory-redirect", DEFAULT_TIMEOUT);
		aSelenium.selectWindow("memory-redirect");
		aSelenium.open("chrome://memory-redirect");
		aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
		
		logger.debug("current window id: {}", driver.getWindowHandle());

		String xpathFormat = "xpath=//span[../../td/div[text()='%s'] and @jscontent='pid']";
		String pid = aSelenium.getText(String.format(xpathFormat, aProcName));
		
		aSelenium.close();
		
		driver.switchTo().window(mainWindow);
		return pid;
	}
}
