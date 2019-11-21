package core;

import java.util.LinkedHashMap;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;

import helper.Methods;

public class IRIGenerator {
	public String getIRIValue(String keyAttributeType, String keyAttribute, Model mapModel,
			LinkedHashMap<String, Object> valueMap, Model provModel, LinkedHashMap<String, String> prefixMap) {
		// TODO Auto-generated method stub
		
		if (keyAttributeType.contains("SourceProperty") || keyAttributeType.contains("SourceAttribute")) {
			
			String objectString = "";
			try {
				if (valueMap.containsKey(keyAttribute)) {
					objectString = valueMap.get(keyAttribute).toString();
				} else {
					objectString = valueMap.get(Methods.assignPrefix(prefixMap, keyAttribute)).toString();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				System.out.println(keyAttributeType);
				System.out.println(keyAttribute);
				
				Methods.print(valueMap);
			}
			
			if (Methods.isIRI(objectString)) {
				return getProvValue(objectString);
			} else
				return objectString;
		} else if (keyAttributeType.contains("Expression")) {
			EquationHandler equationHandler = new EquationHandler();
			return equationHandler.handleExpression(keyAttribute, valueMap).toString();
		} else if (keyAttributeType.contains("Incremental")) {
			String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
					+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT DISTINCT ?s WHERE {"
					+ "?s ?p ?o."
					+ "}";
			
			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, provModel);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

			int count = 1;
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subject = querySolution.get("s").toString();
				count++;
			}

			return String.valueOf(count);
		}
		
		return null;
	}
	
	private String getProvValue(String subject) {
		// TODO Auto-generated method stub
		// System.out.println(subject);
		if (subject.contains("#")) {
			String[] parts = subject.split("#");
			if (parts.length == 2) {
				return parts[1].trim();
			} else {
				return subject.trim();
			}
		} else {
			if (subject.contains("http")) {
				String[] parts = subject.split("/");
				return parts[parts.length - 1].trim();
			} else {
				String[] parts = subject.split(":");
				return parts[parts.length - 1].trim();
			}
		}
	}
	
	public String getRangeValue(String targetType, Model targetTBoxModel) {
		// TODO Auto-generated method stub
		String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT ?s ?o WHERE {"
				+ "?s rdfs:range ?o."
				+ "FILTER regex(str(?s), '" + targetType + "').}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, targetTBoxModel);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
		
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("s").toString();

			if (subject.equals(targetType)) {
				return querySolution.get("o").toString();
			}
		}
		return null;
	}
}
