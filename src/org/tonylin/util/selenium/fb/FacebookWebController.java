package org.tonylin.util.selenium.fb;
import org.openqa.selenium.remote.BrowserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tonylin.util.PlatformUtil;
import org.tonylin.util.ThreadUtil;
import org.tonylin.util.proc.CommandExecuteException;
import org.tonylin.util.proc.CommandInvocater;
import org.tonylin.util.proc.IExecuteResult;
import org.tonylin.util.selenium.SeleniumManager;
import org.tonylin.util.selenium.SeleniumUtil;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;


public class FacebookWebController {
	static private Logger logger = LoggerFactory
			.getLogger(FacebookWebController.class);
	static private FacebookWebController mInstance = null;
	private Selenium mSelenium = null;
	final private String DEFAULT_BROWSER_TYPE = BrowserType.CHROME;
	
	private FacebookWebController(){
		restartBrowser();
	}
	
	static synchronized public FacebookWebController getInstance(){ 
		if( mInstance == null ){
			mInstance = new FacebookWebController();
		}
		return mInstance;
	}
	
	private void waitForChromeDriverBeKilled(){
		int max = 5;
		int count = 0;
		do {
			if( !PlatformUtil.processExist("chromedriver.exe")){
				return;
			}
			count++;
			if( count == max ) {
				break;
			}
			ThreadUtil.sleep(200);
		} while(true);
		
		PlatformUtil.killProcess("chromedriver.exe");
		PlatformUtil.killProcess("chrome.exe");
	}
	
	public void flushDNS(){
		try {
			IExecuteResult result = CommandInvocater.execute("ipconfig /flushdns");
			if( result.getExitCode()!=0 )
				logger.warn("flush DNS failed: {}", result.getSummary());
		} catch (CommandExecuteException e) {
			logger.warn("flush dns failed.", e);
		}
	}
	
	public synchronized void restartBrowser(){
		SeleniumManager sm = SeleniumManager.getInstance();
		sm.stopAllClient();
		
		waitForChromeDriverBeKilled();
		flushDNS();
		
		mSelenium = sm.createSeleniumClient(DEFAULT_BROWSER_TYPE);
		mSelenium.setTimeout("60000");
	}
	
	public boolean isLogin(){
		checkSelenium();
		return FBOperationUtil.isLogin(mSelenium);
	}
	
	public void login(String user, String password) throws FBException{
		checkSelenium();
		
		FBOperationUtil.login(mSelenium, user, password);
	}
	
	public Selenium getSeleniumClient(){
		return mSelenium;
	}
	
	private void checkSelenium(){
		try {
			SeleniumUtil.checkSelenium(mSelenium);
		} catch( SeleniumException e ){
			logger.warn("Check selenium failed. Restart the selenium server.", e);
			restartBrowser();
		}
	}
	
	public boolean isInAppPage(String appName){
		checkSelenium();
		return FBOperationUtil.isInAppPage(mSelenium, appName);
	}
	
	public void playApp(String appName){
		FBOperationUtil.playApp(mSelenium, appName);
	}
}
