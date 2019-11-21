package practice;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import helper.Methods;

public class OlapTest {
	static Methods methods;
	static String sourceFilePathString = "C:\\Users\\Amrit\\Documents\\New_ODE_ETL\\source_abox.ttl";
	static String secondFilePathString = "C:\\Users\\Amrit\\Documents\\Data\\C04\\Fact_Census_C04_TargetABox.ttl";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		methods = new Methods();
		
		String sparqlString = "PREFIX qb: <http://purl.org/linked-data/cube#>\r\n" + 
				"PREFIX qb4o: <http://purl.org/qb4olap/cubes#>\r\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\r\n" + 
				"SELECT * \r\n" + 
				"WHERE {\r\n" + 
				"?o a qb:Observation ."
				+ "?o qb:dataSet <http://linked-statistics-bd.org/2011/data#populationByAdm5ResidenceAgeGroup> .\r\n" +  
				"}";
		
		Model firstModel = methods.readModelFromPath(sourceFilePathString);
		Model secondModel = methods.readModelFromPath(secondFilePathString);
		
		// executeQuery(sparqlString, firstModel, "*** This is source model ***");
		System.out.println("Done");
	}

	private static void executeQuery(String sparqlString, Model firstModel, String messageString) {
		// TODO Auto-generated method stub
		System.out.println(messageString);
		System.out.println();
		
		ResultSet resultSet = methods.executeQuery(firstModel, sparqlString);
		
		int count = 0;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			count++;
		}
		
		System.out.println("Total result: " + count);
	}
}
