package model;

import java.io.Serializable;

public class Single implements Serializable {
	private static final long serialVersionUID = 1L;
	String string;
	
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
}
