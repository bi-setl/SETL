package model;

public class MapperTransform {
	private String sourceProperty;
	private String sourcePropertyType;
	private String targetProperty;
	
	public MapperTransform(String sourceProperty, String sourcePropertyType, String targetProperty) {
		super();
		this.sourceProperty = sourceProperty;
		this.sourcePropertyType = sourcePropertyType;
		this.targetProperty = targetProperty;
	}

	public MapperTransform() {
		super();
		this.sourceProperty = "";
		this.sourcePropertyType = "";
		this.targetProperty = "";
	}

	public String getSourceProperty() {
		return sourceProperty;
	}

	public void setSourceProperty(String sourceProperty) {
		this.sourceProperty = sourceProperty;
	}

	public String getSourcePropertyType() {
		return sourcePropertyType;
	}

	public void setSourcePropertyType(String sourcePropertyType) {
		this.sourcePropertyType = sourcePropertyType;
	}

	public String getTargetProperty() {
		return targetProperty;
	}

	public void setTargetProperty(String targetProperty) {
		this.targetProperty = targetProperty;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return sourceProperty;
	}
}
