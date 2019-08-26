package core;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.resultset.RDFOutput;
import org.apache.jena.sparql.resultset.ResultsFormat;

public class LocalKBExtraction {

	public String fetchInfo(String rdfFilePath, String queryString, String fileSavingPath) {
		// TODO Auto-generated method stub
		String result = "";
		
		Model model = ModelFactory.createDefaultModel();
		model.read(rdfFilePath, null);
		
		if (queryString.contains("Construct") || queryString.contains("CONSTRUCT")
				|| queryString.contains("construct")) {
			Query query = QueryFactory.create(queryString);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
	        Model model2 = execution.execConstruct();
	        
	        try {
				OutputStream outputStream = new FileOutputStream(fileSavingPath);
				model2.write(outputStream, "NT");

				result = "Extraction Successful. File Saved: " + fileSavingPath;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = e.getMessage();
			}
		} else {
			Query query = QueryFactory.create(queryString);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet results = ResultSetFactory.copyResults(execution.execSelect());
			
			Model model2 = RDFOutput.encodeAsModel(results);
			model2.write(System.out);
			
			execution.close();
			
			/*ResultSet set = ResultSetFactory.copyResults(results);
			ResultSetFormatter.out(set);*/

			/*try {
				OutputStream outputStream = new FileOutputStream(fileSavingPath);
				ResultSetFormatter.output(outputStream, results, ResultsFormat.FMT_RDF_NT);
				
				result = "Extraction Successful. File Saved: " + fileSavingPath;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = e.getMessage();
			}*/
			
			// ResultSetFormatter.output(results, ResultsFormat.FMT_RDF_NT);
		}
		
		return result;
	}

}
