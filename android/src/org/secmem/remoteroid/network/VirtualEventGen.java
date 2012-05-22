package org.secmem.remoteroid.network;

import android.util.*;

/**
 * Generate Virtual Event from Packet
 * @author ssm
 *
 */
public class VirtualEventGen {	
	
	private VirtualEventListener listener;
	
	public VirtualEventGen(VirtualEventListener listener){
		this.listener = listener; 
	}		
	
	public void GenerateVirtualEvent(Packet packet){
		EventPacket eventPacket = EventPacket.parse(packet);
		
		switch(eventPacket.GetEventCode()){
		case EventPacket.SETCOORDINATES:
			//listener.onSetCoordinates(eventPacket.GetXPosition(), eventPacket.GetYPosition());
			Log.i("qqqq", "SETCOORDINATES x:"+eventPacket.GetXPosition() + "y:"+eventPacket.GetYPosition());
			break;
		case EventPacket.TOUCHDOWN:
			//listener.onSetCoordinates(eventPacket.GetXPosition(), eventPacket.GetYPosition());
			//listener.onTouchDown();
			Log.i("qqqq", "SetCoordinate x : "+eventPacket.GetXPosition()+" y : "+eventPacket.GetYPosition());
			Log.i("qqqq", "TOUCHDOWN");
			break;
		case EventPacket.TOUCHUP:
			//listener.onTouchUp();
			Log.i("qqqq", "TOUCHUP");
			break;
		case EventPacket.KEYDOWN:
			//listener.onKeyDown(eventPacket.GetKeyCode());
			Log.i("qqqq", "KEYDOWN keycode : "+eventPacket.GetKeyCode());
			break;
		case EventPacket.KEYUP:
			//listener.onKeyUp(eventPacket.GetKeyCode());
			Log.i("qqqq", "KEYUP : "+eventPacket.GetKeyCode());
			break;
		}
	}
	
}
