package org.tonylin.util.selenium;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.tonylin.util.selenium.fb.FacebookWebController;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;


public class Tester {

	private static void trySelenium() throws Exception {
		Selenium selenium = new DefaultSelenium("localhost", 4444,
				"*googlechrome C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe", "http://www.google.com");
		
		selenium.start();
		selenium.open("http://www.google.com");
		selenium.windowMaximize();
		selenium.windowFocus();
		Thread.sleep(5000);
		
		selenium.stop();
	}
	
	
	private static void tryChromeDriver() throws Exception {
		File currentDir = new File(".");
		String driverPath = currentDir.getAbsolutePath() + "/webdrivers/chrome/chromedriver.exe";
		String data = currentDir.getAbsolutePath() + "/chrome/data";
		
		System.setProperty("webdriver.chrome.driver",driverPath);
		ChromeOptions options = new ChromeOptions();
		//options.setBinary("C://Program Files (x86)/Google/Chrome/Application/chrome.exe");
		options.addArguments("user-data-dir="+data);
		options.addArguments("--start-maximized");
		ChromeDriver driver = new ChromeDriver(options);
		//driver.get("http://www.facebook.com");
		
		//Selenium s = new Se
		
		Selenium selenium = new WebDriverBackedSelenium(driver, "http://localhost");
		selenium.open("http://www.facebook.com");
		
		new Thread(new ReloadJob(selenium)).start();
		new Thread(new GetLocationJob(selenium)).start();
		
		Thread.sleep(200000);
		
		selenium.stop();
	}
	
	private static void sleep(){
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public class GetLocationJob implements Runnable {
		private Selenium mSelenium = null;

		public GetLocationJob(Selenium s) {
			mSelenium = s;
		}

		public void run() {
			do {
				System.out.println(new Date()+":"+mSelenium.getLocation());
				sleep();
			} while (true);
		}
	}

	static public class ReloadJob implements Runnable {
		private Selenium mSelenium = null;

		public ReloadJob(Selenium s) {
			mSelenium = s;
		}

		public void run() {
			do {
				System.out.println(new Date()+": refresh");
				mSelenium.refresh();
				sleep();
			} while (true);
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		try {
			//tryChromeDriver();
			FacebookWebController fbController = FacebookWebController.getInstance();
			
			
			
			//new Thread(new ReloadJob(selenium)).start();
			//new Thread(new GetLocationJob(selenium)).start();
			
			Thread.sleep(200000);
			
//			//fbController.login("sclin0824i@hotmail.com", "nb60185");
//			System.out.println(fbController.isLogin());
//			fbController.playApp(FBApps.TBATTLE);
			Thread.sleep(5000);
			//selenium.captureScreenshot("./gg.png");
		} catch( Exception e ){
			e.printStackTrace();
		} finally {
			SeleniumManager.getInstance().stopServer();
		}
	}

}
