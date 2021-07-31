package org.tonylin.util.selenium;

import static org.junit.Assert.*;

import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

public class SeleniumUtilTest extends SeleniumTest {

	@Test
	public void testGetPID() {
		Selenium selenium = mSeleniumManager.createSeleniumClient("chrome");
		selenium.open("http://www.youtube.com/");
		selenium.waitForPageToLoad("60000");
		
		String pid = SeleniumUtil.getFlashPID(selenium);
		System.out.println("pid=" + pid);
		
		assertNotNull(pid);
	}

}
