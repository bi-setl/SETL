package model;

import java.util.ArrayList;

public class ReferencingObjectMap {
	
	String parentTripleMapName;
	ArrayList<JoinCondition> joinConditions;
	
	
	
	public ReferencingObjectMap() {
	
		joinConditions = new ArrayList<>();
	}

	public ReferencingObjectMap(String parentTripleMapName, ArrayList<JoinCondition> joinConditions) {
		
		this.parentTripleMapName = parentTripleMapName;
		this.joinConditions = joinConditions;
	}

	public String getParentTripleMap() {
		return parentTripleMapName;
	}

	public void setParentTripleMap(String parentTripleMap) {
		this.parentTripleMapName = parentTripleMap;
	}

	public ArrayList<JoinCondition> getJoinConditions() {
		return joinConditions;
	}

	public void setJoinConditions(ArrayList<JoinCondition> joinConditions) {
		this.joinConditions = joinConditions;
	}
	
	
	

}
