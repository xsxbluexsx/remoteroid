package org.secmem.remoteroid.web;

import java.io.IOException;
import java.net.MalformedURLException;

import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Request.RequestFactory;
import org.secmem.remoteroid.lib.request.Response;

import android.util.Log;

public class RemoteroidWeb {
	
	public static Response addAccount(String email, String password) throws MalformedURLException, IOException{
		Request req = RequestFactory.getRequest(API.Account.ADD_ACCOUNT);
		
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(password);
		
		return req.attachPayload(account).sendRequest();
		
	}
	
	public static Response doLogin(String email, String password) throws MalformedURLException, IOException{
		Request req = RequestFactory.getRequest(API.Account.LOGIN);
		
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(password);
		
		
		return req.attachPayload(account).sendRequest();
				
	}
	
	public static Response addDevice(String nickname, String email, String pwd, String reg, String deviceUUID) throws MalformedURLException, IOException{
		Request req = RequestFactory.getRequest(API.Device.ADD_DEVICE);
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(pwd);
		
		Device dev = new Device();
		dev.setNickname(nickname);
		dev.setRegistrationKey(reg);
		dev.setOwnerAccount(account);
		dev.setDeviceUUID(deviceUUID);
		
		Log.i("qq","nickname = "+nickname);
		Log.i("qq","email = "+email);
		Log.i("qq","pwd = "+pwd);
		Log.i("qq","reg = "+reg);
		
		return req.attachPayload(dev).sendRequest();
	}
	
	public static void deleteAccount(){
		Request req = RequestFactory.getRequest(API.Account.DELETE_ACCOUNT);
		
//		authentication
	}
	
	public static Response updateInfo(String uuid, String nickname, String email, String pwd, String reg) throws MalformedURLException, IOException{
		
		Request req = RequestFactory.getRequest(API.Device.UPDATE_DEVICE_INFO);
		
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(pwd);
		
		Device dev = new Device();
		dev.setNickname(nickname);
		dev.setDeviceUUID(uuid);
		dev.setRegistrationKey(reg);
		dev.setOwnerAccount(account);
		
		return req.attachPayload(dev).sendRequest();
	}
	
	

}
