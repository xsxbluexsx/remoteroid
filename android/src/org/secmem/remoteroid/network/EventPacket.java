package org.secmem.remoteroid.network;

import java.nio.*;

import android.util.*;

/**
 * This class combine eventCode, xPosition, yPosition, keyCode from packet
 * @author ssm
 */
public class EventPacket {
	
	public static final int SETCOORDINATES = 0;
	public static final int TOUCHDOWN = 1;
	public static final int TOUCHUP = 2;
	public static final int KEYDOWN = 3;
	public static final int KEYUP = 4;
	
	public static final int EVENTCODE_SIZE = 1;
	public static final int XPOSITION_SIZE = 4;
	public static final int YPOSITION_SIZE = 4;
	public static final int KEYCODE_SIZE = 4;
	public static final int MAXEVENT_SIZE = EVENTCODE_SIZE+XPOSITION_SIZE+YPOSITION_SIZE;
	
	private int eventCode;
	private int xPosition;
	private int yPosition;
	private int keyCode;
	
	//Host is Little endian, but JVM is Big endian
	//ByteBuffer support Little endian
	private static ByteBuffer buffer = ByteBuffer.allocate(MAXEVENT_SIZE);	
	
	
	protected EventPacket(){			
	}
	
	public void SetEventCode(int eventCode){
		this.eventCode = eventCode;
	}
	
	public void SetXPosition(int xPosition){
		this.xPosition = xPosition;
	}
	
	public void SetYPosition(int yPosition){
		this.yPosition = yPosition;
	}
	
	public void SetKeyCode(int keyCode){
		this.keyCode = keyCode;
	}
	
	public int GetEventCode(){
		return eventCode;
	}
	
	public int GetXPosition(){
		return xPosition;
	}
	
	public int GetYPosition(){
		return yPosition;
	}
	
	public int GetKeyCode(){
		return keyCode;
	}	

	
	//Parsing EventPacket from packet
	public static EventPacket parse(Packet packet){
		EventPacket eventPacket = new EventPacket();

		//host is little endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);		
		
		buffer.put(packet.getPayload(), 0, MAXEVENT_SIZE);
		buffer.rewind();
		
		eventPacket.SetEventCode(buffer.get());
		
		switch(eventPacket.GetEventCode()){		
		case SETCOORDINATES:
		case TOUCHDOWN:
			eventPacket.SetXPosition(buffer.getInt());
			eventPacket.SetYPosition(buffer.getInt());
			break;		
		case KEYDOWN:
		case KEYUP:
			eventPacket.SetKeyCode(buffer.getInt());
			break;		
		}
		buffer.rewind();
		return eventPacket;
	}

	
}
