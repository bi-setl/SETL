package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

import helper.Methods;

public class ABoxToTBox {
	private static final String TEMP_ABOX_TBOX_FILE_TTL = "temp_abox_tbox_file.ttl";
	Model classModel;
	ArrayList<String> classes;
	private Methods methods;

	public ABoxToTBox() {
		super();
		classModel = ModelFactory.createDefaultModel();
		classes = new ArrayList<>();
		methods = new Methods();

		File file = new File(TEMP_ABOX_TBOX_FILE_TTL);
		try {
			file.createNewFile();
			String data = methods.getPrefixStrings();
			try (PrintWriter out = new PrintWriter(file)) {
				out.println(data);
				classModel.read(TEMP_ABOX_TBOX_FILE_TTL);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String assignPrefix(String iri) {
		if (iri.contains("#")) {
			String[] segments = iri.split("#");
			if (segments.length == 2) {
				String firstSegment = segments[0].trim() + "#";

				for (Map.Entry<String, String> map : methods.getAllPredefinedPrefixes().entrySet()) {
					String key = map.getKey();
					String value = map.getValue();

					if (firstSegment.equals(value.trim())) {
						return key + segments[1];
					}
				}

				return iri;
			} else {
				return iri;
			}
		} else {
			String[] segments = iri.split("/");
			String lastSegment = segments[segments.length - 1];

			String firstSegment = "";
			if (iri.endsWith(lastSegment)) {
				firstSegment = iri.replace(lastSegment, "");
			}

			for (Map.Entry<String, String> map : methods.getAllPredefinedPrefixes().entrySet()) {
				String key = map.getKey();
				String value = map.getValue();

				if (firstSegment.equals(value.trim())) {
					return key + lastSegment;
				}
			}

			return iri;
		}
	}

	public String assignIRI(String prefix) {
		String[] segments = prefix.split(":");
		if (segments.length == 2) {
			String firstSegment = segments[0] + ":";
			return methods.getAllPredefinedPrefixes().get(firstSegment) + segments[1];
		} else {
			return prefix;
		}
	}

	public String generateTBox(String filePath, String targetPath) {
		try {
			// TODO Auto-generated method stub
			Model model = ModelFactory.createDefaultModel();
			model.read(filePath);
			getUniqueClasses(model);
			for (int i = 0; i < classes.size(); i++) {
				for (int j = i + 1; j < classes.size(); j++) {
					getClassMembers(model, classes.get(i), classes.get(j));
				}
			}
			getUniqueProperties(model);
			// classModel.write(System.out, "TTL");
			try {
				OutputStream outputStream = new FileOutputStream(targetPath);
				String[] parts = targetPath.split("\\.");
				classModel.write(outputStream, parts[parts.length - 1].toUpperCase());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return "Successful.\nFile saved to: " + targetPath;
		} catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}
	}

	private void getUniqueProperties(Model model) {
		// TODO Auto-generated method stub
		String sparql = "SELECT DISTINCT ?p WHERE { ?s ?p ?o.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			RDFNode property = querySolution.get("p");
			// System.out.println(property.toString());
			if (!property.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
				setPropertyValues(model, property);
			}
		}
	}

	private void setPropertyValues(Model model, RDFNode propertyName) {
		// TODO Auto-generated method stub
		String sparql = "SELECT * { ?s ?p ?o. FILTER regex(str(?p), '" + propertyName.toString() + "')}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			RDFNode subject = querySolution.get("s");
			RDFNode property = querySolution.get("p");
			RDFNode object = querySolution.get("o");
			
			// System.out.println(object);
			if (object.isResource()) {
				addObjectProperty(property.toString());
				String objectType = getClassType(model, object);
				createProperty(property.toString(), objectType, "rdfs:range");
			} else {
				addDataProperty(property.toString());
				// System.out.println("There " + object.asLiteral().getDatatypeURI());
				createProperty(property.toString(), object.asLiteral().getDatatypeURI(), "rdfs:range");
			}

			String subjectType = getClassType(model, subject);
			createProperty(property.toString(), subjectType, "rdfs:domain");
		}
	}

	private String getClassType(Model model, RDFNode subject) {
		// TODO Auto-generated method stub
		String sparql = "SELECT * { ?s a ?o. FILTER regex(str(?s), '" + subject.toString() + "')}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String type = querySolution.get("o").toString();
			return type;
		}
		return null;
	}

	private void getClassMembers(Model model, String string, String string2) {
		// TODO Auto-generated method stub
		String sparql = "SELECT DISTINCT ?s { ?s a ?o. ?s a ?p FILTER regex(str(?o), '" + string
				+ "'). FILTER regex(str(?p), '" + string2 + "')}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		int count = 0;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String string3 = querySolution.get("s").toString();
			count++;
		}

		if (count == 0) {
			createProperty(string, string2, "owl:disjointWith");
		} else {
			addClassProperties(model, string, string2);
		}
	}

	/*
	 * private void setClassProperties(Model model, String string) { // TODO
	 * Auto-generated method stub String sparql =
	 * "SELECT * { ?s a ?o. ?s ?p ?q. FILTER regex(str(?o), '" + string + "')}";
	 * Query query = QueryFactory.create(sparql); QueryExecution execution =
	 * QueryExecutionFactory.create(query, model); ResultSet resultSet =
	 * ResultSetFactory.copyResults(execution.execSelect());
	 * 
	 * while (resultSet.hasNext()) { QuerySolution querySolution = (QuerySolution)
	 * resultSet.next(); RDFNode property = querySolution.get("p"); RDFNode object =
	 * querySolution.get("q");
	 * 
	 * System.out.println(object);
	 * 
	 * if (object.isResource()) { getClassType(model, string, property, object); }
	 * else { createProperty(string, object.asLiteral().getDatatypeURI(),
	 * property.toString()); }
	 * 
	 * } }
	 */

	/*
	 * private void getClassType(Model model, String string, RDFNode property,
	 * RDFNode object) { // TODO Auto-generated method stub String sparql =
	 * "SELECT * { ?s a ?o. FILTER regex(str(?s), '" + object.toString() + "')}";
	 * Query query = QueryFactory.create(sparql); QueryExecution execution =
	 * QueryExecutionFactory.create(query, model); ResultSet resultSet =
	 * ResultSetFactory.copyResults(execution.execSelect());
	 * 
	 * while (resultSet.hasNext()) { QuerySolution querySolution = (QuerySolution)
	 * resultSet.next(); String type = querySolution.get("o").toString();
	 * 
	 * createProperty(string, type, property.toString()); } }
	 */

	private void createProperty(String string, String object, String property) {
		// TODO Auto-generated method stub
		// String propertyName = assignIRI(property);

		Resource resource = classModel.getResource(string);
		Property predicate = classModel.createProperty(assignIRI(property));

		if (object == null) {
			resource.addProperty(predicate, classModel.createResource(assignIRI("xsd:anyURI")));
		} else {
			resource.addProperty(predicate, classModel.createResource(object));
		}
	}

	private void addClassProperties(Model model, String string, String string2) {
		// TODO Auto-generated method stub
		int num1 = getClassMembers(model, string);
		int num2 = getClassMembers(model, string2);

		if (num1 == num2) {
			createProperty(string, string2, "owl:equivalentClass");
		} else if (num1 > num2) {
			createProperty(string2, string, "owl:subClassOf");
		} else if (num2 > num1) {
			createProperty(string, string2, "owl:subClassOf");
		}
	}

	private int getClassMembers(Model model, String string) {
		// TODO Auto-generated method stub
		String sparql = "SELECT * { ?s a ?o. FILTER regex(str(?o), '" + string + "')}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		int count = 0;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String string3 = querySolution.get("s").toString();
			count++;
		}
		return count;
	}

	private void getUniqueClasses(Model model) {
		// TODO Auto-generated method stub
		String sparql = "SELECT DISTINCT ?o { ?s a ?o. }";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subject = querySolution.get("o").toString();
			classes.add(subject);
			addClassResource(subject);
		}
	}

	public boolean addClassResource(String name) {
		// TODO Auto-generated method stub
		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#Class");
		Resource newResource = classModel.createResource(name);
		newResource.addProperty(RDF.type, classResource);
		return true;
	}

	public boolean addDataProperty(String name) {
		// TODO Auto-generated method stub
		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#DatatypeProperty");
		Resource resource = ResourceFactory.createResource(name);
		if (classModel.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = classModel.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public boolean addObjectProperty(String name) {
		// TODO Auto-generated method stub
		Resource classResource = ResourceFactory.createResource("http://www.w3.org/2002/07/owl#ObjectProperty");
		Resource resource = ResourceFactory.createResource(name);
		if (classModel.containsResource(resource)) {
			return false;
		} else {
			Resource newResource = classModel.createResource(name);
			newResource.addProperty(RDF.type, classResource);
			return true;
		}
	}

	public Model getClassModel() {
		return classModel;
	}

	public void setClassModel(Model classModel) {
		this.classModel = classModel;
	}
}
