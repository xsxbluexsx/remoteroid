package org.secmem.remoteroid.network;

public interface VirtualEventListener {
	public void onSetCoordinates(int xPosition, int yPosition);
	public void onTouchDown();
	public void onTouchUp();
	public void onKeyDown(int keyCode);
	public void onKeyUp(int keyCode);
}
