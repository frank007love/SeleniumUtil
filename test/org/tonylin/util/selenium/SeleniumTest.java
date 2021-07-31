package org.tonylin.util.selenium;

import org.junit.AfterClass;
import org.tonylin.util.PlatformUtil;

public abstract class SeleniumTest {
	protected static SeleniumManager mSeleniumManager = SeleniumManager.getInstance();
	
	@AfterClass
	public static void classTeardown(){
		mSeleniumManager.stopAllClient();
		mSeleniumManager.stopServer();
		
		
		PlatformUtil.killProcess("chromedriver.exe");
	}
}
