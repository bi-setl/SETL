package model;

import java.util.ArrayList;

public class PredicateObjectMap {
	
	private PredicateMap predicateMap;
	private ObjectMap objectMap;
	private ReferencingObjectMap referencingObjectMap;
	private boolean isReferencing;
	
	
	public PredicateObjectMap(PredicateMap predicateMap, ObjectMap objectMap) {
		this.predicateMap = predicateMap;
		this.objectMap = objectMap;
		this.isReferencing = false;
	}
	
	public PredicateObjectMap(PredicateMap predicateMap, ReferencingObjectMap referencingObjectMap) {
		this.predicateMap = predicateMap;
		this.referencingObjectMap = referencingObjectMap;
		this.isReferencing = true;
	}

	public PredicateMap getPredicateMap() {
		return predicateMap;
	}

	public void setPredicateMap(PredicateMap predicateMap) {
		this.predicateMap = predicateMap;
	}

	public ObjectMap getObjectMap() {
		return objectMap;
	}

	public void setObjectMap(ObjectMap objectMap) {
		this.objectMap = objectMap;
	}

	public ReferencingObjectMap getReferencingObjectMap() {
		return referencingObjectMap;
	}

	public void setReferencingObjectMap(ReferencingObjectMap referencingObjectMap) {
		this.referencingObjectMap = referencingObjectMap;
	}

	public boolean isReferencing() {
		return isReferencing;
	}

	public void setReferencing(boolean isReferencing) {
		this.isReferencing = isReferencing;
	}
	
	
}
