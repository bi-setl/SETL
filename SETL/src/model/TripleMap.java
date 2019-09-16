package model;

import java.util.ArrayList;

public class TripleMap {

	private String tableName;
	String tripleMapName;
	
	private SubjectMap subjectMap;
	private ArrayList<PredicateObjectMap> predicateObjectMaps;
	
	public TripleMap(String tableName, String tripleMapName, SubjectMap subjectMap, ArrayList<PredicateObjectMap> predicateObjectMaps) {
		this.tableName = tableName;
		this.subjectMap = subjectMap;
		this.tripleMapName = tripleMapName;
		this.predicateObjectMaps = predicateObjectMaps;
	}
	
	public TripleMap(String tableName, String tripleMapName) {
		this.tableName = tableName;
		this.tripleMapName = tripleMapName;
		this.predicateObjectMaps = new ArrayList<>();
	}
	
	
	public String getTripleMapName() {
		return tripleMapName;
	}


	public void setTripleMapName(String tripleMapName) {
		this.tripleMapName = tripleMapName;
	}

	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public SubjectMap getSubjectMap() {
		return subjectMap;
	}

	public void setSubjectMap(SubjectMap subjectMap) {
		this.subjectMap = subjectMap;
	}

	public ArrayList<PredicateObjectMap> getPredicateObjectMaps() {
		return predicateObjectMaps;
	}

	public void setPredicateObjectMaps(ArrayList<PredicateObjectMap> predicateObjectMaps) {
		this.predicateObjectMaps = predicateObjectMaps;
	}
	
	public void printTripleMap() {
		
		System.out.println("\n\nTableName: "+tableName);
		System.out.println("TripleMap Name: " + tripleMapName);
		
	
		System.out.println("SUBJECT MAP:");
		
		System.out.println("Term Map Type: " +subjectMap.termMapType);
		System.out.println("Term map Value: " + subjectMap.termMapValue);
	
		System.out.println("PREDICATE OBJECT_MAPS:");
		for(PredicateObjectMap predicateObjectMap: predicateObjectMaps){
			
			PredicateMap predicateMap = predicateObjectMap.getPredicateMap();
			System.out.println("Predicate Type: "+predicateMap.getTermpMapType());
			System.out.println("Predicate Value: " + predicateMap.getTermMapvalue());
			
			if(predicateObjectMap.isReferencing()){
				
				ReferencingObjectMap referencingObjectMap = predicateObjectMap.getReferencingObjectMap();
				System.out.println("Ref Parent Triple: " + referencingObjectMap.getParentTripleMap());
				for(JoinCondition joinCondition: referencingObjectMap.getJoinConditions()){
					System.out.println("Parent: " + joinCondition.getParent());
					System.out.println("Child: " + joinCondition.getChild());
					
					
				}
				
				
			}else{
				
				ObjectMap objectMap = predicateObjectMap.getObjectMap();
				
				System.out.println("Object Term Map Type: " + objectMap.getTermMapType());
				System.out.println("Object Term Map Value: " + objectMap.getTermMapValue());
				System.out.println("Obeject Term Type: " + objectMap.getTermType());
			}
			
			
			
		}
		
		
	}
	
	
}
