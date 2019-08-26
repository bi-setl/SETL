package demo;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;

import helper.Methods;

public class RunQuery {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filepath = "C:\\Users\\Amrit\\Documents\\Census_11_TargetABox_01.ttl";
		Methods methods = new Methods();
		
		Model model = methods.readModelFromPath(filepath);
		
		String sparql = "PREFIX qb: <http://purl.org/linked-data/cube#>\r\n" + 
				"PREFIX qb4o: <http://purl.org/qb4olap/cubes#>\r\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\r\n" + 
				"SELECT ?fieldOfActivityDim_fieldOfActivity (SUM(<http://www.w3.org/2001/XMLSchema#long>(?m1)) as ?numberOfPopulation_sum) \r\n" + 
				"WHERE {\r\n" + 
				"?o a qb:Observation .\r\n" + 
				"?o qb:dataSet <http://linked-statistics-bd.org/2011/data#populationByAdm5ResidenceSexFieldActivity> .\r\n" + 
				"?o <http://linked-statistics-bd.org/2011/mdProperty#numberOfPopulation> ?m1 .\r\n" + 
				"?o <http://linked-statistics-bd.org/2011/mdProperty#fieldOfActivity> ?fieldOfActivityDim_fieldOfActivity .\r\n" + 
				"}\r\n" + 
				"GROUP BY ?fieldOfActivityDim_fieldOfActivity\r\n" + 
				"ORDER BY ?fieldOfActivityDim_fieldOfActivity";
		
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
		
		methods.printResultSet(resultSet);
	}
}
