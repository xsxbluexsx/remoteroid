package com.Remotroid.NetworkMod;

import java.io.*;
import java.net.*;
import java.util.*;

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
        		module.SendPacket(6, null, 0);
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
				ArrayList<File> fileList = new ArrayList<File>();
				fileList.add(file);
				fileList.add(new File("/mnt/sdcard/melon/박봄-01-YOU AND I-128.dcf"));
				module.SendFileList(fileList);
				
			} catch (UnknownHostException e) {
				Log.i("qqq", "UnknownHostException : "+e.getMessage());
			} catch (IOException e) {
				Log.i("qqq", "IOException : "+e.getMessage());
			}
    	}
    }
    NetworkModule module = null;
}
