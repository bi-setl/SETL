package model;

import java.io.Serializable;

public class ModelDouble implements Serializable {
	private static final long serialVersionUID = 1L;
	Object objectOne;
	Object objectTwo;
	
	public Object getObjectOne() {
		return objectOne;
	}
	public void setObjectOne(Object objectOne) {
		this.objectOne = objectOne;
	}
	public Object getObjectTwo() {
		return objectTwo;
	}
	public void setObjectTwo(Object objectTwo) {
		this.objectTwo = objectTwo;
	}
}
