package demo;

public class TreeValue {
	private String name;
	private boolean isChecked;
	
	public TreeValue(String name, boolean isChecked) {
		super();
		this.name = name;
		this.isChecked = isChecked;
	}
	
	public TreeValue(String name) {
		super();
		this.name = name;
		this.isChecked = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
