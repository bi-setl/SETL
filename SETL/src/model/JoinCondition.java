package model;


public class JoinCondition {

	String child;
	String parent;
	
	public JoinCondition(String child, String parent) {
		
		
		this.child = child;
		this.parent = parent;
	}

	public String getChild() {
		return child;
	}

	public void setChild(String child) {
		this.child = child;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	
	
	
	
}
