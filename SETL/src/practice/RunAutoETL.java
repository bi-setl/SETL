package practice;

import java.util.ArrayList;

import controller.Definition;
import core.PrefixExtraction;

public class RunAutoETL {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String basePath = "C:\\Users\\Amrit\\Documents\\SETL\\AutoETL\\";
		// String basePath = "C:\\Users\\USER\\Documents\\SETL\\AutoETL\\";
		
		String mapFile = basePath + "map_version_1582180161488.ttl";
		String targetTBoxFile = basePath + "subsidy.ttl";
		
		PrefixExtraction prefixExtraction = new PrefixExtraction();
		prefixExtraction.extractPrefix(mapFile);
		prefixExtraction.extractPrefix(targetTBoxFile);
		
		ArrayList<String> selectedArrayList = new ArrayList();
		selectedArrayList.add("mdProperty:BudgetLine");
		
		Definition definition = new Definition();
		definition.setMapPath(mapFile);
		definition.setTboxPath(targetTBoxFile);
		
		definition.extractDependency(selectedArrayList, prefixExtraction);
	}
}
