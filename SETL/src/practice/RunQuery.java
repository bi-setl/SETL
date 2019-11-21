package practice;

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
		String filepath = "level.ttl";
		Methods methods = new Methods();

		Model model = methods.readModelFromPath(filepath);
		model.add(methods.readModelFromPath("fact.ttl"));

		// Methods.print(model);

		String sparql = "PREFIX qb: <http://purl.org/linked-data/cube#>\r\n" + 
				"PREFIX qb4o: <http://purl.org/qb4olap/cubes#>\r\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\r\n" + 
				"SELECT * \r\n" + 
				"WHERE {\r\n" + 
				"?o a qb:Observation .\r\n" + 
				"?o qb:dataSet <http://linked-statistics-bd.org/2011/data#HouseholdByAdm5ResHousingTenancy> .\r\n" + 
				"?o <http://linked-statistics-bd.org/2011/mdProperty#numberOfHousehold> ?m1 .\r\n" + 
				"?o <http://linked-statistics-bd.org/2011/mdProperty#housingTenancy> ?housingTenancyDim_housingTenancy .\r\n" + 
				"?housingTenancyDim_housingTenancy qb4o:memberOf <http://linked-statistics-bd.org/2011/mdProperty#housingTenancy> .\r\n" + 
				"?housingTenancyDim_housingTenancy <http://linked-statistics-bd.org/2011/mdAttribute#inHousingTenancy> ?housingTenancyDim_housingTenancyAll .\r\n" +
				"}\r\n";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		methods.printResultSet(resultSet);
		/*
		 * 
		 * "?housingTenancyDim_housingTenancy <http://linked-statistics-bd.org/2011/mdAttribute#inHousingTenancy> ?housingTenancyDim_housingTenancyAll .\r\n"
		 * +
		 * "?housingTenancyDim_housingTenancyAll qb4o:memberOf <http://linked-statistics-bd.org/2011/mdProperty#housingTenancyAll> .\r\n"
		 * +
		 */

	}
}
