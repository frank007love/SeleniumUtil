package org.tonylin.util.selenium;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.Selenium;


public class SeleniumManager {

	static private SeleniumManager mInstance = null;
	private SeleniumServer mServer = null;
	private List<Selenium> mSeleniumClientList = new ArrayList<Selenium>();
	
	private SeleniumManager(){
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		stopAllClient();
		stopServer();
	}
	
	static synchronized public SeleniumManager getInstance(){
		if( mInstance == null )
			mInstance = new SeleniumManager();
		return mInstance;
	}
	
	public void stopServer(){
		if( !mSeleniumClientList.isEmpty()){
			stopAllClient();
		}
		if( mServer != null ){
			mServer.stop();
		}
	}
	
	public void stopAllClient(){
		for( Selenium selenium : mSeleniumClientList ){
			selenium.stop();
		}
		mSeleniumClientList.clear();
	}
	
	public void startServer() throws Exception {
		if( mServer == null ) {
			mServer = new SeleniumServer();
			mServer.getConfiguration().setSingleWindow(false);
			mServer.getConfiguration().setTrustAllSSLCertificates(true);
		}
		mServer.start();
	}
	
	public Selenium createSeleniumClient(String type){
		Selenium selenium = SeleniumFactory.createInstance(type);
		mSeleniumClientList.add(selenium);
		return selenium;
	}
}
