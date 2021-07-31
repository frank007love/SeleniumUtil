package org.tonylin.util.selenium;

import org.junit.Test;
import org.openqa.selenium.remote.BrowserType;
import org.tonylin.util.selenium.fb.FBException;
import org.tonylin.util.selenium.fb.FBOperationUtil;

import com.thoughtworks.selenium.Selenium;

public class FBOperationUtilTest {

	@Test
	public void test() throws Exception{
		
		SeleniumManager manager = SeleniumManager.getInstance();
		try {
			manager.startServer();
			Selenium s = manager.createSeleniumClient(BrowserType.CHROME);
			if(FBOperationUtil.isLogin(s)) {
				FBOperationUtil.logout(s);
			} else {
			
				FBOperationUtil.login(s, "sclin0824i@hotmail.com", "123456");
				FBOperationUtil.logout(s);
			}
		} finally {
			manager.stopAllClient();
			manager.stopServer();
		}
	}
	
}
