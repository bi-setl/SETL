package practice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
		String filepath = "C:\\Users\\Amrit\\Documents\\SETL\\Instance\\200916_101525_TargetABox.ttl";
		Methods methods = new Methods();

		Model model = methods.readModelFromPath(filepath);

		Methods.print(model);

//		String sparql = "SELECT * WHERE {?s a <http://extbi.lab.aau.dk/ontolgoy/sdw/mdProperty/BudgetOne>.?s ?p ?o.}";
//
//		Query query = QueryFactory.create(sparql);
//		QueryExecution execution = QueryExecutionFactory.create(query, model);
//		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
//
//		methods.printResultSet(resultSet);
		
//		String timeString = "Fri Nov 13 21:25:27 GMT+06:00 2020";
//		
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault());
//		
//		try {
//			Date date = simpleDateFormat.parse(timeString);
//			System.out.println("Time parsed");
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Time not parsed");
//		}
	}
}
