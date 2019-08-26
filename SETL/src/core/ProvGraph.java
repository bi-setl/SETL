package core;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

import helper.Methods;

public class ProvGraph {
	Methods fileMethods;
	Model model;
	
	private String provIRI = "http://www.prov.com/prov/";
	private String setlIRI = "http://www.setl.com/setl/";
	
	
	public ProvGraph(String filePath) {
		// TODO Auto-generated constructor stub
		fileMethods = new Methods();
		model = fileMethods.readModelFromPath(filePath);
	}
	
	public String createProvIRI(String provValue, String sourceType, String targetType, String targetTBoxFile) {
		// TODO Auto-generated method stub
		/*System.out.println("Prov value: " + provValue);
		System.out.println("Source type: " + sourceType);
		System.out.println("Target type: " + targetType);*/
		
		Methods fileMethods = new Methods();
		if (provValue == null) {
			return null;
		} else {
			provValue = fileMethods.encodeString(provValue.trim());
			Object rangeValue = getRangeValue(targetType, targetTBoxFile);
			
			String provIRIString = "";
			if (rangeValue == null) {
				provIRIString = targetType + "#" + provValue;
			} else {
				provIRIString = rangeValue + "#" + provValue;
			}
			// String provIRIString = targetType + "#" + provValue;
			// System.out.println("ProvIRIString: " + provIRIString);

			Resource resource = model.createResource(provIRIString);
			Resource classResource = ResourceFactory.createResource(provIRI + "Entity");
			resource.addProperty(RDF.type, classResource);

			classResource = ResourceFactory.createResource(setlIRI + "GenerateBy");
			Property property = model.createProperty(provIRI + "wasGeneratedBy");
			resource.addProperty(property, classResource);

			property = model.createProperty(provIRI + "wasDerivedFrom");
			resource.addProperty(property, model.createResource()
					.addProperty(RDF.type, ResourceFactory.createResource(provIRI + "Entity"))
					.addProperty(model.createProperty(provIRI + "type"), ResourceFactory.createResource(targetType))
					.addProperty(model.createProperty(provIRI + "value"), model.createLiteral(provValue))
					.addProperty(model.createProperty(provIRI + "isDefinedBy"), getFirstSegment(targetType)));
			
			if (sourceType.trim().length() > 0) {
				resource.addProperty(model.createProperty(provIRI + "hadPrimarySource"), getFirstSegment(sourceType));
			}

			return provIRIString;
		}
	}

	public Object getRangeValue(String targetProperty, String targetTBoxFile) {
		// TODO Auto-generated method stub
		Model model = fileMethods.readModelFromPath(targetTBoxFile);

		// System.out.println("Target Property: " + targetProperty);
		
		if (model == null) {
			return "Problem in the targetTBox File";
		} else {
			String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
					+ "SELECT ?s ?o WHERE {"
					+ "?s rdfs:range ?o."
					+ "}";

			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());
			
			// fileMethods.printResultSet(resultSet);

			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subject = querySolution.get("s").toString();
				
				// System.out.println(subject);

				if (subject.equals(targetProperty)) {
					// System.out.println("Matched: " + targetProperty + " = " + subject);
					RDFNode rdfNode = querySolution.get("o");
					return rdfNode;
				}
			}
			return null;
		}
	}
	
	private String getFirstSegment(String string) {
		// TODO Auto-generated method stub
		if (string.contains("#")) {
			String[] segments = string.split("#");
			if (segments.length == 2) {
				return segments[0].trim();
			} else {
				return string;
			}
		} else {
			String temp = "";
			String[] segments = string.split("/");
			String lastPart = "/" + segments[segments.length - 1];
			temp = string.replace(lastPart, "");
			return temp;
		}
	}
	
	public String lookUpProvGraph(String provValue, String target) {
		// TODO Auto-generated method stub
		String sparql = "PREFIX prov: <http://www.prov.com/prov/>\r\n" + "SELECT ?S ?Y WHERE { ?S ?P ?O. "
				+ "?O prov:type ?Y." + "FILTER regex(str(?S), '" + provValue + "').}";

		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		// ResultSetFormatter.out(resultSet);

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = String.valueOf(querySolution.get("S"));
			String type = String.valueOf(querySolution.get("Y"));

			if (subject.contains(provValue)) {
				if (type.equals(target)) {
					return subject;
				}
			}
		}
		return null;
	}
	
	public String createProvIRI(String provValue, String sourceClass, String targetClass, String head,
			String mapperFile, String source, String sourceAboxFile) {
		// TODO Auto-generated method stub
		String iriValue = "";
		Model model2 = fileMethods.readModelFromPath(mapperFile);

		String sparql2 = "PREFIX map: <http://www.map.org/example#>\r\n"
				+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT * WHERE {"
				+ "?head a map:ConceptMapper. " + "?head map:keyAttributeType ?type. " + "}";

		Query query2 = QueryFactory.create(sparql2);
		QueryExecution execution2 = QueryExecutionFactory.create(query2, model2);
		ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());
		
		// fileMethods.printResultSet(resultSet2);

		while (resultSet2.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet2.next();
			String keyAttributeType = querySolution.get("type").toString();
			String concept = querySolution.get("head").toString();

			// System.out.println(keyAttributeType);
			
			if (concept.equals(head)) {
				iriValue = getIRIValue(keyAttributeType, head, mapperFile, source, sourceAboxFile);
			}
		}

		if (iriValue == null) {
			iriValue = fileMethods.encodeString(provValue);
		} else {
			iriValue = fileMethods.encodeString(iriValue);
		}
		String provIRIString = targetClass + "#" + iriValue;

		Resource resource = model.createResource(provIRIString);
		Resource classResource = ResourceFactory.createResource(provIRI + "Entity");
		resource.addProperty(RDF.type, classResource);

		classResource = ResourceFactory.createResource(setlIRI + "GenerateBy");
		Property property = model.createProperty(provIRI + "wasGeneratedBy");
		resource.addProperty(property, classResource);

		property = model.createProperty(provIRI + "wasDerivedFrom");
		resource.addProperty(property, model.createResource()
				.addProperty(RDF.type, ResourceFactory.createResource(provIRI + "Entity"))
				.addProperty(model.createProperty(provIRI + "type"), ResourceFactory.createResource(targetClass))
				.addProperty(model.createProperty(provIRI + "value"), model.createLiteral(provValue))
				.addProperty(model.createProperty(provIRI + "isDefinedBy"), getFirstSegment(targetClass))
				.addProperty(model.createProperty(provIRI + "hadPrimarySource"), getFirstSegment(sourceClass)));

		return provIRIString;
	}

	public String getIRIValue(String keyAttributeType, String head, String mapperFile, String source,
			String sourceAboxFile) {
		// TODO Auto-generated method stub
		if (keyAttributeType.contains("Direct")) {
			// String iriValue = "";
			Model model2 = fileMethods.readModelFromPath(mapperFile);
			model2.add(fileMethods.readModelFromPath(sourceAboxFile));

			String sparql2 = "PREFIX map: <http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE {"
					+ "?head a map:ConceptMapper. "
					+ "?head map:sourceConcept ?type. "
					+ "?head map:keyAttribute ?value. "
					+ "?subject a ?type. "
					+ "?subject ?value ?object."
					+ "}";

			Query query2 = QueryFactory.create(sparql2);
			QueryExecution execution2 = QueryExecutionFactory.create(query2, model2);
			ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());

			// fileMethods.printResultSet(resultSet2);
			
			while (resultSet2.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet2.next();
				String concept = querySolution.get("head").toString();
				
				if (concept.equals(head)) {
					// String key = querySolution.get("value").toString();
					String value = querySolution.get("object").toString();
					String subject = querySolution.get("subject").toString();

					if (subject.equals(source)) {
						return value;
					}
				}
			}
		} else if (keyAttributeType.contains("Expression")) {
			// String iriValue = "";
			Model model2 = fileMethods.readModelFromPath(mapperFile);
			model2.add(fileMethods.readModelFromPath(sourceAboxFile));

			String sparql2 = "PREFIX map: <http://www.map.org/example#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n"
					+ "SELECT * WHERE {"
					+ "?head a map:ConceptMapper. "
					+ "?head map:sourceConcept ?type. "
					+ "?head map:keyAttributeType ?key. "
					+ "?head map:keyAttribute ?value. "
					+ "?subject a ?type. "
					+ "?subject ?p ?object. "
					+ "}";

			Query query2 = QueryFactory.create(sparql2);
			QueryExecution execution2 = QueryExecutionFactory.create(query2, model2);
			ResultSet resultSet2 = ResultSetFactory.copyResults(execution2.execSelect());
			
			// fileMethods.printResultSet(resultSet2);

			while (resultSet2.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet2.next();
				String concept = querySolution.get("head").toString();

				if (concept.equals(head)) {
					String key = querySolution.get("value").toString();
					String value = querySolution.get("object").toString();
					String subject = querySolution.get("subject").toString();
					String predicate = querySolution.get("p").toString();
					
					if (key.contains(":")) {
						fileMethods.extractAllPrefixes(sourceAboxFile);
						predicate = fileMethods.assignPrefix(predicate);
					}
					
					if (subject.equals(source)) {
						if (key.contains(predicate)) {
							ExpressionHandler expressionHandler = new ExpressionHandler();
							return expressionHandler.handleExpression(key, value).toString();
						}
					}
				}
			}
		} else if (keyAttributeType.contains("Incremental")) {
			String sparql = "PREFIX qb:	<http://purl.org/linked-data/cube#>\r\n"
					+ "PREFIX	owl:	<http://www.w3.org/2002/07/owl#>\r\n"
					+ "PREFIX	qb4o:	<http://purl.org/qb4olap/cubes#>\r\n" + "SELECT DISTINCT ?s WHERE {" + "?s ?p ?o."
					+ "}";
			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

			// ResultSetFormatter.out(ResultSetFactory.copyResults(resultSet));

			int count = 1;
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subject = querySolution.get("s").toString();
				count++;
			}

			return String.valueOf(count);
		} else if (keyAttributeType.contains("Automatic")) {
			return null;
		}
		return null;
	}
}
