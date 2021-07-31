package org.tonylin.util.selenium;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;
import org.tonylin.util.PlatformUtil;
import org.tonylin.util.proc.CommandExecuteException;
import org.tonylin.util.proc.CommandInvocater;
import org.tonylin.util.proc.IExecuteResult;

import com.thoughtworks.selenium.Selenium;

public class SeleniumManagerTest extends SeleniumTest {
	
	@Test
	public void testRemoteWebDriver() throws Exception{
		final String currentDir = new File("./webdrivers/chrome/chromedriver.exe").getAbsolutePath();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				IExecuteResult result = null;
				try {
					result = CommandInvocater.execute("cmd /c \"" + currentDir+"\"");
				} catch (CommandExecuteException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		Thread.sleep(3000);

		
		Selenium selenium = mSeleniumManager.createSeleniumClient("chrome");
		
		assertEquals("data:,", selenium.getLocation());
	}
	
	@Test
	public void testGetLocation() {
		Selenium selenium = mSeleniumManager.createSeleniumClient("chrome");
		
		assertEquals("chrome-search://local-ntp/local-ntp.html", selenium.getLocation());
	}

}
