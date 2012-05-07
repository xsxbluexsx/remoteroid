package com.Remotroid.NetworkMod;

import java.io.*;
import java.net.*;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import org.secmem.remoteroid.network.*;

public class RemotroidNetworkModuleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        module = NetworkModule.getInstance();
        
        
        Button btn = (Button)findViewById(R.id.btnConnect);
        btn.setOnClickListener(new Button.OnClickListener(){        	

			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConnectThread connectThread = new ConnectThread();
		        connectThread.start();		       
			}        	
        });         
        Button btn2 = (Button)findViewById(R.id.btnTest);
        btn2.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		module.CloseSocket();
        	}
        });
	}    

    class ConnectThread extends Thread{
    	public void run(){
			try {
				module.ConnectSocket("210.118.74.85");
				File file = new File("/mnt/sdcard/melon/박봄-01-Don`t Cry-128.dcf");
//				for(int i = 0 ; i<file.listFiles().length ; i++){
//					Log.i("qq","fileName = "+file.listFiles()[i].getName());
//				}
				module.SendFileInfo(file);
				module.SendFileData(file);
			} catch (UnknownHostException e) {
				Log.i("q", "UnknownHostException : "+e.getMessage());
			} catch (IOException e) {
				Log.i("q", "IOException : "+e.getMessage());
			}
    	}
    }
    NetworkModule module = null;
}
