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
import android.os.Build;
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
	public static boolean execAsRoot(String cmd){
		if(cmd==null || cmd.equals(""))
			throw new IllegalArgumentException();
		  
		boolean retval = false;
		
		try{
		    Process suProcess = Runtime.getRuntime().exec("su");
		
		    DataOutputStream os = 
		        new DataOutputStream(suProcess.getOutputStream());
		    
		    os.writeBytes(cmd + "\n");
		    os.flush();
		    
		    //String out = is.readLine();
		    //System.out.println(out);
		    
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
	public static boolean execAsRoot(ArrayList<String> cmds){
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

    public static boolean isDriverExists(Context context){
        File file;
        
        try{
                // Check Busybox
                file = new File(context.getFilesDir().getAbsolutePath()+"/busybox");
                if(!file.exists())
                        return false;
                
                // Check IDC
                file = new File("/system/usr/idc/remoteroid.idc");
                if(!file.exists())
                        return false;
                /*
                if(Build.VERSION.SDK_INT < 11){
	                // Check kcm.bin
	                file = new File("/system/usr/keychars/remoteroid.kcm.bin");
	                if(!file.exists())
	                        return false;
                }else{
	                // Check kcm
	                file = new File("/system/usr/keychars/remoteroid.kcm");
	                if(!file.exists())
	                        return false;
                }
                
                // Check KeyLayout
                file = new File("/system/usr/keylayout/remoteroid.kl");
                if(!file.exists())
                        return false;*/
                
        }catch(Exception e){
                return false;
        }
        return true;
    }

    public static void copyInputDrivers(Context context) throws IOException, SecurityException{
        
        if(!isRootAccessAvailable())
                throw new SecurityException();
        
        // Step 0. Install and configure Busybox
        // See here : http://benno.id.au/blog/2007/11/14/android-busybox
        
        // Extract busybox from resources
        copyRawResourceIntoFile(context, R.raw.busybox, context.getFilesDir().getAbsolutePath()+"/busybox");
        
        // Change permission
        execAsRoot("chmod 700 /data/data/org.secmem.remoteroid/files/busybox");
        
        // Mount /system as r/w
        execAsRoot("mount -orw,remount /system");
        
        // Step 1. Extract driver files from resources
        // Copy IDC(Input Device Configuration)
        copyRawResourceIntoFile(context, R.raw.remoteroid_idc, context.getFilesDir().getAbsolutePath()+"/remoteroid.idc");
        /*
        if(Build.VERSION.SDK_INT < 11){
        	// Copy kcm.bin
        	copyRawResourceIntoFile(context, R.raw.remoteroid_kb, context.getFilesDir().getAbsolutePath()+"/remoteroid.kcm.bin");
        }else{
        	// Copy kcm (new standard since honeycomb)
        	copyRawResourceIntoFile(context, R.raw.remoteroid_kcm, context.getFilesDir().getAbsolutePath()+"/remoteroid.kcm");
        }
        // Copy KeyLayout
        copyRawResourceIntoFile(context, R.raw.remoteroid_kl, context.getFilesDir().getAbsolutePath()+"/remoteroid.kl");
*/
        // Step 2. Move driver files into appropriate path
        ArrayList<String> cmdList = new ArrayList<String>();
        
        cmdList.add("/data/data/org.secmem.remoteroid/files/busybox busybox cp /data/data/org.secmem.remoteroid/files/remoteroid.idc /system/usr/idc/remoteroid.idc");
        //cmdList.add("/data/data/org.secmem.remoteroid/files/busybox busybox cp /data/data/org.secmem.remoteroid/files/remoteroid.kcm.bin /system/usr/keychars/remoteroid.kcm.bin");
        //cmdList.add("/data/data/org.secmem.remoteroid/files/busybox busybox cp /data/data/org.secmem.remoteroid/files/remoteroid.kcm /system/usr/keychars/remoteroid.kcm");
        //cmdList.add("/data/data/org.secmem.remoteroid/files/busybox busybox cp /data/data/org.secmem.remoteroid/files/remoteroid.kl /system/usr/keylayout/remoteroid.kl");
        cmdList.add("mount -oro,remount /system"); // Mount /system as r/o
        execAsRoot(cmdList);
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
}
