package org.tonylin.util.selenium.fb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tonylin.util.ThreadUtil;
import org.tonylin.util.selenium.SeleniumUtil;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class FBOperationUtil {
	static private Logger logger = LoggerFactory
			.getLogger(FBOperationUtil.class);
	
	static final private String DEFAULT_TIMEOUT = "30000"; 
	static final private String FB_APP_FRAME_ID = "iframe_canvas_fb_https";
	static final private String TOP_FRAME_ID = "relative=top";
	
	static public boolean isLogin(Selenium aSelenium){
		String location = aSelenium.getLocation();
		if( location.contains("://apps.facebook.com/") )
			return true;
		
		if( location == null || !location.contains("www.facebook.com") ){
			logger.debug("Redirect to facebook index.");
			aSelenium.open("http://www.facebook.com");
			aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
		}
		
		logger.debug("Check xpath=//form[@id='searchBarClickRef'] and //input[@id='email']");
		boolean checkResult = !aSelenium.isElementPresent("xpath=//input[@id='email']") ||
				aSelenium.isElementPresent("xpath=//form[@id='searchBarClickRef']");
		logger.debug("Is login: {}", checkResult);
		return checkResult;
	}

	
	static public void login(Selenium aSelenium, String user, String password) throws FBException {
		boolean isLogin =  isLogin(aSelenium);

		if(isLogin)
			return;
		
		logger.debug("Start login.");
		
		aSelenium.type("email", user);
		aSelenium.type("pass", password);
		
		aSelenium.click("loginbutton");
		aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
		
		if( aSelenium.getLocation().contains("login.php?login_attempt=")){
			throw new FBException("Authentication failed."); 
		}
		
		String continueBtn = "submit[Continue]";
		String saveRadioXpath = "xpath=//input[@value='dont_save']";
		if( !aSelenium.isElementPresent(saveRadioXpath) && aSelenium.isElementPresent(continueBtn) ){
			aSelenium.click(continueBtn);
			aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
			
			String okBtnXpath = "xpath=//input[@name='submit[This is Okay]']";
			if( aSelenium.isElementPresent(okBtnXpath)){
				aSelenium.click(okBtnXpath);
				aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
			} else {
				//System.out.println(aSelenium.getHtmlSource());
				throw new FBException("Login Warning!");
			}
		}
		
		for( int i = 0; i < 2; i++ ){
			if (aSelenium.isElementPresent(saveRadioXpath)
					&& aSelenium.isElementPresent(continueBtn)) {
				aSelenium.click(saveRadioXpath);
				aSelenium.click(continueBtn);
				aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
			}
		}
	}
	
	static public void selectTopFrame(Selenium aSelenium){
		aSelenium.waitForFrameToLoad(TOP_FRAME_ID, DEFAULT_TIMEOUT);
		selectFrame(aSelenium, TOP_FRAME_ID);
	}
	
	static public void playApp(Selenium aSelenium, String appName){
		if( isInAppPage(aSelenium, appName))
			return;
		logger.debug("Login to app: {}", appName);
		aSelenium.open("https://apps.facebook.com/"+appName);
		logger.debug("Wait for page to be loaded.");
		aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
		logger.debug("Page is loaded.");
		logger.debug("Wait for frame to be loaded.");
		aSelenium.waitForFrameToLoad(FB_APP_FRAME_ID, DEFAULT_TIMEOUT);
		logger.debug("Frame is loaded.");
	}
	
	static public void selectAppFrame(Selenium aSelenium){
		aSelenium.waitForFrameToLoad(FB_APP_FRAME_ID, DEFAULT_TIMEOUT);
		selectFrame(aSelenium, FB_APP_FRAME_ID);
	}
	
	static public void selectFrame(Selenium aSelenium, String aFrame){
		int count = 0;
		int max = 60;
		SeleniumException finalException = null;
		do {
			if( count != 0 ){
				ThreadUtil.sleep(500);
			}
			logger.debug("Select frame {} {} time(s).", aFrame, (count+1));
			try {
				aSelenium.selectFrame(aFrame);
				return;
			} catch( SeleniumException e ){
				finalException = e;
			}
			count++;
		} while(count < max);
		
		throw finalException;
	}
	
	static public void selectFrameIfExist(Selenium aSelenium, String aFrame){
		try {
			aSelenium.waitForFrameToLoad(aFrame, "300");
			selectFrame(aSelenium, aFrame);
		} catch( Exception e ){
			logger.debug("Frame {} doesn't exist: {}", aFrame, e.getMessage());
		}
	}
	
	static public boolean isInAppPage(Selenium aSelenium, String appName){
		selectFrameIfExist(aSelenium, TOP_FRAME_ID);
		String location = aSelenium.getLocation();
		boolean checkResult =  location != null && location.contains("apps.facebook.com/"+appName);
		logger.debug("Location={}, CheckResult={}", location, checkResult);
		if( checkResult == true )
			selectAppFrame(aSelenium);
		return checkResult;
	}
	
	static private void clickLogoutSpan(Selenium aSelenium){
		try {
			SeleniumUtil.waitForElementPresent(aSelenium, "xpath=//form[@id='logout_form']//input[@value='登出']", "3000");
			SeleniumUtil.clickElement(aSelenium, "xpath=//form[@id='logout_form']//input[@value='登出']");
			return;
		} catch( SeleniumException e ){
			// ignore
		}
		
		SeleniumUtil.clickElement(aSelenium, "xpath=//span[text()='登出']");
	}
	
	static public void logout(Selenium aSelenium){
		if( !isLogin(aSelenium) ) {
			logger.debug("Facebook is not login status.");
			return;
		} 
		logger.debug("Start to logout.");
		aSelenium.refresh();
		
		int max = 3;
		int attempt = 0;
		SeleniumException final_ex = null;
		do {
			try {
				SeleniumUtil.clickElement(aSelenium, "xpath=//div[@id='userNavigationLabel']");
				clickLogoutSpan(aSelenium);
				aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
				break;
			} catch( SeleniumException e ){
				final_ex = e;
				refresh(aSelenium);
				attempt++;
			}
		} while(attempt < max);
		
		if( final_ex != null )
			throw final_ex;
	}
	
	static public void refresh(Selenium aSelenium){
		aSelenium.refresh();
		aSelenium.waitForPageToLoad(DEFAULT_TIMEOUT);
	}
}
