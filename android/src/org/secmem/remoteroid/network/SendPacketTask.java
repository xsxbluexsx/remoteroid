package org.secmem.remoteroid.network;

import java.io.IOException;
import java.io.OutputStream;

import android.os.AsyncTask;

/**
 * Provides method for sending packet via stream on the background.
 * @author Taeho Kim
 *
 */
public class SendPacketTask extends AsyncTask<Packet, Integer, Boolean> {
	
	private OutputStream sendStream;
	private PacketSendListener listener;
	
	public SendPacketTask(OutputStream stream, PacketSendListener listener){
		this.sendStream = stream;
		this.listener = listener;
	}
	
	public void setOutputStream(OutputStream stream){
		this.sendStream = stream;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if(result==true){ // Packet sent successful
			if(listener!=null)
				listener.onPacketSent();
		}else{ // Failed to send packet
			if(listener!=null)
				listener.onSendFailed();
		}
	}

	@Override
	protected Boolean doInBackground(Packet... packets) {
		int packetCnt = packets.length;
		for(int i=0; i<packetCnt; i++){
			try{
				sendStream.write(packets[i].asByteArray());
			}catch(IOException e){
				return false;
			}
			publishProgress(i, packetCnt);
		}
		return true;
	}

}
