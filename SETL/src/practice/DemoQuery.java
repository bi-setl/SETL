package practice;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class DemoQuery {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = "C:\\Users\\Amrit\\Documents\\Alternate Files\\PopulationByResAdm5LivposTargetTBox.ttl";

		Model model = ModelFactory.createDefaultModel();
		model.read(filePath);

		String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
				+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX	rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
				+ "SELECT * WHERE { "
				+ "?hier a qb4o:Hierarchy."
				+ "?step a qb4o:HierarchyStep."
				+ "?step qb4o:parentLevel ?parent."
				+ "?step qb4o:childLevel ?child."
				+ "?steptwo a qb4o:HierarchyStep."
				+ "?steptwo qb4o:parentLevel ?child."
				+ "?steptwo qb4o:childLevel ?x."
				+ "}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		ResultSetFormatter.out(resultSet);
	}
}
