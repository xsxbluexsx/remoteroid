package org.secmem.remoteroid.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.secmem.remoteroid.R;

import android.content.Context;
import android.util.Log;

/**
 * Contains method for Command-line command execution.
 * @author Taeho Kim
 *
 */
public class CommandLine {
	
	/**
	 * Determine this device has root permission or not.
	 * @return true if device has rooted, false otherwise
	 */
	public static boolean isRootAccessAvailable(){
	    boolean result = false;
	    Process suProcess;
	    
	    try{
	      suProcess = Runtime.getRuntime().exec("su");
	      
	      DataOutputStream outStream = new DataOutputStream(suProcess.getOutputStream());
	      DataInputStream inStream = new DataInputStream(suProcess.getInputStream());
	      
	      if (null != outStream && null != inStream){
	        // Getting the id of the current user to check if this is root
	        outStream.writeBytes("id\n");
	        outStream.flush();

	        String currUid = inStream.readLine();
	        boolean exitSu = false;
	        
	        if (currUid==null){
	          result = false;
	          exitSu = false;
	          Log.d("ROOT", "Can't get root access or denied by user");
	        }else if (currUid.contains("uid=0")){
	          result = true;
	          exitSu = true;
	          Log.d("ROOT", "Root access granted");
	        }else{
	          result = false;
	          exitSu = true;
	          Log.d("ROOT", "Root access rejected: " + currUid);
	        }

	        if (exitSu){
	          outStream.writeBytes("exit\n");
	          outStream.flush();
	        }
	      }
	    }catch (Exception e){
	      // Can't get root !
	      // Probably broken pipe exception on trying to write to output
	      // stream after su failed, meaning that the device is not rooted
	      
	      result = false;
	      Log.d("ROOT", "Root access rejected [" +
	            e.getClass().getName() + "] : " + e.getMessage());
	    }

	    return result;
	  }
	 
	/**
	 * Execute command as root.
	 * @param cmd Command to be executed as ROOT
	 * @return true if execution succeed, false otherwise
	 */
	private static boolean execAsRoot(String cmd){
		if(cmd==null || cmd.equals(""))
			throw new IllegalArgumentException();
		  
		boolean retval = false;
		
		try{
		    Process suProcess = Runtime.getRuntime().exec("su");
		
		    DataOutputStream os = 
		        new DataOutputStream(suProcess.getOutputStream());
		
		    os.writeBytes(cmd + "\n");
		    os.flush();
		
		    os.writeBytes("exit\n");
		    os.flush();
		
		    try{
		      int suProcessRetval = suProcess.waitFor();
		      if (255 != suProcessRetval){
		        // Root access granted
		        retval = true;
		      }else{
		        // Root access denied
		        retval = false;
		      }
		    }catch (Exception ex){
		      Log.e("Error executing root action", ex.toString());
		    }
		  
		}catch (IOException ex){
		  Log.w("ROOT", "Can't get root access", ex);
		}catch (SecurityException ex){
		  Log.w("ROOT", "Can't get root access", ex);
		}catch (Exception ex){
		  Log.w("ROOT", "Error executing internal operation", ex);
		}
		    
		return retval;
	}
	
	/**
	 * Execute list of commands.
	 * @param cmds list of commands to be executed as ROOT
	 * @return true execution succeed, false otherwise
	 */
	private static boolean execAsRoot(ArrayList<String> cmds){
		if(cmds==null || cmds.size()==0)
			throw new IllegalArgumentException();
		  
		boolean retval = false;
		
		try{
		    Process suProcess = Runtime.getRuntime().exec("su");
		
		    DataOutputStream os = 
		        new DataOutputStream(suProcess.getOutputStream());
		
		    for(String cmd : cmds){
			    os.writeBytes(cmd + "\n");
			    os.flush();
		    }
		
		    os.writeBytes("exit\n");
		    os.flush();
		
		    try{
		      int suProcessRetval = suProcess.waitFor();
		      if (255 != suProcessRetval){
		        // Root access granted
		        retval = true;
		      }else{
		        // Root access denied
		        retval = false;
		      }
		    }catch (Exception ex){
		      Log.e("Error executing root action", ex.toString());
		    }
		  
		}catch (IOException ex){
		  Log.w("ROOT", "Can't get root access", ex);
		}catch (SecurityException ex){
		  Log.w("ROOT", "Can't get root access", ex);
		}catch (Exception ex){
		  Log.w("ROOT", "Error executing internal operation", ex);
		}
		    
		return retval;
	}
	
	public static boolean isDriverExists(){
		File file;
		boolean fileExists = true;
		
		try{
			// Check IDC
			file = new File("/system/usr/idc/remoteroid.idc");
			fileExists &= file.exists();
			
			// Check kcm.bin
			file = new File("/system/usr/keychars/remoteroid.kcm.bin");
			fileExists &= file.exists();
			
			// Check kcm
			file = new File("/system/usr/keychars/remoteroid.kcm");
			fileExists &= file.exists();
			
			// Check KeyLayout
			file = new File("/system/usr/keylayout/remoteroid.kl");
			fileExists &= file.exists();
			
		}catch(Exception e){
			return false;
		}
		return fileExists;
	}

	public static void copyInputDrivers(Context context) throws IOException, SecurityException{
		
		if(!isRootAccessAvailable())
			throw new SecurityException();
		
		// Step 0. Install and configure Busybox
		// See here : http://benno.id.au/blog/2007/11/14/android-busybox
		// TODO Extract busybox from resources
		// TODO Exec Busybox as following : #./busybox -install
		// TODO Set path for Busybox : #export PATH=/PATH_TO_BBX:$PATH
		
		// Mount /system as r/w
		execAsRoot("mount -orw,remount /system");
		
		// Step 1. Extract driver files from resources
		// Copy IDC(Input Device Configuration)
		copyRawResourceIntoFile(context, R.raw.remotdroid_idc, context.getFilesDir().getAbsolutePath()+"/remoteroid.idc");
		// Copy kcm.bin
		copyRawResourceIntoFile(context, R.raw.remoteroid_kb, context.getFilesDir().getAbsolutePath()+"/remoteroid.kcm.bin");
		// Copy kcm
		copyRawResourceIntoFile(context, R.raw.remoteroid_kcm, context.getFilesDir().getAbsolutePath()+"/remoteroid.kcm");
		// Copy KeyLayout
		copyRawResourceIntoFile(context, R.raw.remoteroid_kl, context.getFilesDir().getAbsolutePath()+"/remoteroid.kl");
	
		// Step 2. Move driver files into appropriate path
		ArrayList<String> cmdList = new ArrayList<String>();
		
		// FIXME Must use cp command rather than mv command, using busybox
		cmdList.add("mv /data/data/org.secmem.remoteroid/files/remoteroid.idc /system/usr/idc/remoteroid.idc");
		cmdList.add("mv /data/data/org.secmem.remoteroid/files/remoteroid.kcm.bin /system/usr/keychars/remoteroid.kcm.bin");
		cmdList.add("mv /data/data/org.secmem.remoteroid/files/remoteroid.kcm /system/usr/keychars/remoteroid.kcm");
		cmdList.add("mv /data/data/org.secmem.remoteroid/files/remoteroid.kl /system/usr/keylayout/remoteroid.kl");
		
		execAsRoot(cmdList);
		
		// Mount /system as r/o
		execAsRoot("mount -oro,remount /system");
	}
	
	private static void copyRawResourceIntoFile(Context context, int resId, String pathOfFile) throws IOException{
		InputStream ins = context.getResources().openRawResource(resId);
		int size = ins.available();

		byte[] buffer = new byte[size];
		ins.read(buffer);
		ins.close();

		FileOutputStream fos = new FileOutputStream(pathOfFile);
		fos.write(buffer);
		fos.close();
	}
	
	/**
	 * Restart device.
	 */
	public static void restartDevice(){
		execAsRoot("reboot");
	}
}
