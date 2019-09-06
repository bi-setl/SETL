package practice;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;

import helper.Methods;

public class RunQuery {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String filepath = "C:\\Users\\Amrit\\Documents\\Data\\C04\\Census_C04_TargetABox.ttl";
		String filepath = "C:\\Users\\Amrit\\Documents\\Census_08_TargetABox.ttl";
		Methods methods = new Methods();
		
		Model model = methods.readModelFromPath(filepath);
		
		String sparql = "PREFIX qb: <http://purl.org/linked-data/cube#>\r\n" + 
				"PREFIX qb4o: <http://purl.org/qb4olap/cubes#>\r\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\r\n" + 
				"SELECT * \r\n" + 
				"WHERE {\r\n" + 
				"?o a qb:Observation .\r\n"
				+ "?o qb:dataSet <http://linked-statistics-bd.org/2011/data#populationByAdm5ResidenceSexAttendSchoolAgeGroup> ."
				+ "}\r\n";
		
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
		
		String resultText = ResultSetFormatter.asText(resultSet);
		methods.writeText("C:\\Users\\Amrit\\Documents\\result.txt", resultText);
		
		methods.printResultSet(resultSet);
	}
}
