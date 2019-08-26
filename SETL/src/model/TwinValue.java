package model;

public class TwinValue {
	private String firstValue;
	private String secondValue;
	
	public TwinValue(String firstValue, String secondValue) {
		super();
		this.firstValue = firstValue;
		this.secondValue = secondValue;
	}

	public String getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}

	public String getSecondValue() {
		return secondValue;
	}

	public void setSecondValue(String secondValue) {
		this.secondValue = secondValue;
	}

	public TwinValue() {
		super();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String text = firstValue + " - " + secondValue;
		return text;
	}
}
