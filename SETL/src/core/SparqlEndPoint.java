package core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.sparql.resultset.RDFOutput;

public class SparqlEndPoint {
	public String fetchInfo(String url, String queryString, String fileSavingPath) {
		// TODO Auto-generated method stub
		String result = "";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(url, query);

		ResultSet results = ResultSetFactory.copyResults(qexec.execSelect());
		// ResultSetFormatter.out(System.out, results, query);

		qexec.close();

		try {
			OutputStream outputStream = new FileOutputStream(fileSavingPath);
			RDFOutput.outputAsRDF(outputStream, "NT", results);
			
			result = "Extraction Successful. File Saved: " + fileSavingPath;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage();
		}
		
		return result;
	}
}
