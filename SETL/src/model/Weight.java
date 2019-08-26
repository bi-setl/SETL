package model;

import java.io.Serializable;

public class Weight implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String object;
	float weight;
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public Weight(String object, float weight) {
		super();
		this.object = object;
		this.weight = weight;
	}
}
