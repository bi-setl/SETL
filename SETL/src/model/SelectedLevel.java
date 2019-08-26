package model;

import java.util.ArrayList;
import java.util.List;

public class SelectedLevel {
	private String levelName;
	private String levelPath;
	private String filterCondition;
	private List<String> viewProperties = new ArrayList<>();
	
	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public SelectedLevel() {
		// TODO Auto-generated constructor stub
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getLevelPath() {
		return levelPath;
	}

	public void setLevelPath(String levelPath) {
		this.levelPath = levelPath;
	}

	public SelectedLevel(String levelName, String levelPath, String filterCondition) {
		super();
		this.levelName = levelName;
		this.levelPath = levelPath;
		this.filterCondition = filterCondition;
	}

	public SelectedLevel(String levelName, String levelPath) {
		super();
		this.levelName = levelName;
		this.levelPath = levelPath;
		this.filterCondition = "";
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof SelectedLevel) {
			SelectedLevel selectedLevel = (SelectedLevel) obj;
			if (levelName.equals(selectedLevel.getLevelName())) {
				String parts[] = levelPath.split(",");
				String oldDimension = parts[1].trim();
				
				String segments[] = selectedLevel.getLevelPath().split(",");
				String newDimension = segments[1].trim();
				
				if (oldDimension.equals(newDimension)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean match(String level) {
		if (levelName.equals(level)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return levelName + "\n" + levelPath + "\n" + filterCondition;
	}

	public List<String> getViewProperties() {
		return viewProperties;
	}

	public void setViewProperties(List<String> viewProperties) {
		this.viewProperties = viewProperties;
	}
}
