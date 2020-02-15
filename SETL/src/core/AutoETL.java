package core;

import org.apache.jena.rdf.model.Model;

import helper.Methods;

public class AutoETL {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String basePath = "C:\\Users\\USER\\Documents\\SETL\\AutoETL\\";
		
		String mapFile = basePath + "map_current.ttl";
		String targetTBoxFile = "subsidy.ttl";
		
		performAutoETL(mapFile, targetTBoxFile);
	}

	private static void performAutoETL(String mapFile, String targetTBoxFile) {
		// TODO Auto-generated method stub
		Model mapModel = Methods.readModelFromPath(mapFile);
		Model targetTBoxModel = Methods.readModelFromPath(targetTBoxFile);
	}
}
