package org.secmem.remoteroid.data;

public class ExplorerType {
	
	public static int TYPE_FOLDER = 1;
	public static int TYPE_FILE = 2;
	
	private int type;
	private String name;
	
	public ExplorerType(String name, int type) {
		this.name = name;
		this.type = type;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	
}
