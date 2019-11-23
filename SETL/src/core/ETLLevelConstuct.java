package core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.ResourceUtils;

import helper.Methods;

public class ETLLevelConstuct {
	private String provBaseIRI = "http://www.prov.com/prov/";
	Methods methods = new Methods();

	// String demoTarget = "C:\\Users\\Amrit\\Documents\\etl\\demo_target.ttl";
	// String demoProvGraph =
	// "C:\\Users\\Amrit\\Documents\\etl\\demo_provGraph.ttl";

	/*
	 * public static void main(String[] args) { String basePathString =
	 * "C:\\Users\\Amrit\\Documents\\DimensionalConstruct\\";
	 * 
	 * String dimensionalConstruct =
	 * "http://linked-statistics-bd.org/2011/mdProperty#householdSize"; String
	 * newSourceData = basePathString + "source_abox_new.ttl"; String oldSourceData
	 * = basePathString + "source_abox_old.ttl"; String sourceTBox = basePathString
	 * + "source_tbox.ttl"; String targetTBox = basePathString + "bd_tbox.ttl";
	 * String targetABox = basePathString + "targetABox.ttl"; String prefix =
	 * basePathString + "http://example.com/sdw/"; String mapper = basePathString +
	 * "map.ttl"; String provGraph = basePathString + "prov.ttl"; String resultFile
	 * = basePathString + "result_target_3.ttl";
	 * 
	 * ETLLevelConstuct etlLevelConstuct = new ETLLevelConstuct(); String
	 * resultString = etlLevelConstuct.updateLevelConstruct(dimensionalConstruct,
	 * newSourceData, oldSourceData, sourceTBox, targetTBox, targetABox, prefix,
	 * mapper, provGraph, resultFile);
	 * 
	 * System.out.println(resultString); }
	 */

	public String updateLevelConstruct(String dimensionalConstruct, String newSourceData, String oldSourceData,
			String sourceTBox, String targetTBox, String targetABox, String prefix, String mapper, String provGraph,
			String resultFile) {
		// TODO Auto-generated method stub

		Model newSourceModel = readModelFromFilePath(newSourceData);

		if (newSourceModel == null) {
			return "Error in new Source ABox File";
		}
		Model oldSourceModel = readModelFromFilePath(oldSourceData);

		if (oldSourceModel == null) {
			return "Error in old Source ABox File";
		}

		Model sourceModel = newSourceModel.difference(oldSourceModel);

		ArrayList<String> distinctChangedSourceSubjects = getDistinctSubjects(sourceModel);

		ArrayList<String> distinctChangedSourceProperties = getDistinctProperties(sourceModel);

		LinkedHashMap<String, String> correspondingTargetProperties = getTargetProperties(mapper,
				distinctChangedSourceProperties);

		LinkedHashMap<String, LinkedHashMap<String, RDFNode>> correspondingChangedValues = getCorrespondingSourceValues(
				sourceModel, distinctChangedSourceSubjects, correspondingTargetProperties);
		LinkedHashMap<String, String> correspondingUpdateType = getUpdateType(targetTBox,
				correspondingTargetProperties);

		LinkedHashMap<String, String> correspondingProvIRIMap = getProvIRI(provGraph, distinctChangedSourceSubjects);

		return updateTargetABox(targetABox, dimensionalConstruct, prefix, provGraph, correspondingChangedValues,
				correspondingUpdateType, correspondingProvIRIMap, resultFile);

		/*
		 * for (String string : distinctChangedSourceProperties) {
		 * System.out.println(string); }
		 */

		/*
		 * for (Entry<String, String> entry : correspondingUpdateType.entrySet()) {
		 * String key = entry.getKey(); String value = entry.getValue();
		 * 
		 * System.out.println(key + " - " + value); }
		 */
	}

	private String updateTargetABox(String targetABox, String dimensionalConstruct, String prefix, String provGraph,
			LinkedHashMap<String, LinkedHashMap<String, RDFNode>> correspondingChangedValues,
			LinkedHashMap<String, String> correspondingUpdateType,
			LinkedHashMap<String, String> correspondingProvIRIMap, String resultFile) {
		// TODO Auto-generated method stub
		Model model = readModelFromFilePath(targetABox);

		for (String prov_subject : correspondingChangedValues.keySet()) {
			LinkedHashMap<String, RDFNode> propertiesAndValue = correspondingChangedValues.get(prov_subject);

			String sparql = "PREFIX cube: <http://purl.org/qb4olap/cubes#>"
					+ "SELECT * WHERE {?S ?P ?O. ?S cube:memberOf ?T. " + "FILTER regex(str(?S), '"
					+ getResourceValue(prov_subject) + "').}";

			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

			/*
			 * 
			 * ResultSet set = ResultSetFactory.copyResults(resultSet);
			 * ResultSetFormatter.out(set);
			 * 
			 */

			boolean isTypeTwo = false, hasDate = false;
			if (correspondingUpdateType.containsValue("http://purl.org/qb4olap/cubes#Type2")) {
				isTypeTwo = true;
			}

			String currentSubject = null, startDate = null;

			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subject = querySolution.get("S").toString();
				String predicate = querySolution.get("P").toString();
				RDFNode object = querySolution.get("O");
				String type = querySolution.get("T").toString();

				if (type.equals(dimensionalConstruct)) {
					
					if (getResourceValue(subject).equals(getResourceValue(prov_subject))) {
						
						if (isTypeTwo) {
							currentSubject = subject;
							Calendar calendar = Calendar.getInstance();
							DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
							String todayString = dateFormat.format(calendar.getTime());

							Resource newResource = model.createResource(subject + "_" + todayString);
							Property property = model.createProperty(predicate);

							if (propertiesAndValue.containsKey(predicate)) {
								RDFNode changedValue = propertiesAndValue.get(predicate);
								newResource.removeAll(property);
								newResource.addProperty(property, changedValue);
							} else {
								newResource.addProperty(property, object);
							}

							if (predicate.contains("startDate")) {
								hasDate = true;
								startDate = object.toString();
							}
						} else {
							if (propertiesAndValue.containsKey(predicate)) {
								RDFNode changedValue = propertiesAndValue.get(predicate);
								String updateType = correspondingUpdateType.get(predicate);

								if (updateType.contains("Type1")) {
									Resource resource = model.createResource(subject);
									Property property = model.createProperty(predicate);
									resource.removeAll(property);
									resource.addProperty(property, changedValue);
								} else if (updateType.contains("Type3")) {
									Resource resource = model.createResource(subject);

									Property property = model.createProperty(predicate);
									Property oldProperty = model.createProperty(predicate + "_oldValue");

									resource.removeAll(property);
									resource.addProperty(property, changedValue);
									resource.addProperty(oldProperty, object);
								}
							}
						}
					}
				}
			}

			if (isTypeTwo) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

				Calendar calendar = Calendar.getInstance();
				String todayString = dateFormat.format(calendar.getTime());
				String defaultStartString = "01/01/1111";
				String defaultEndString = "01/01/9999";

				calendar.add(Calendar.DATE, 1);
				String nextDayString = dateFormat.format(calendar.getTime());

				Property startDateProperty = model.createProperty(prefix + "startDate");
				Property endDateProperty = model.createProperty(prefix + "endDate");
				Property statusProperty = model.createProperty(prefix + "status");

				Resource oldResource = model.createResource(currentSubject);

				if (hasDate) {
					oldResource.removeAll(startDateProperty);
					oldResource.removeAll(endDateProperty);
					oldResource.removeAll(statusProperty);

					oldResource.addProperty(startDateProperty, startDate);
					oldResource.addProperty(endDateProperty, todayString);
					oldResource.addProperty(statusProperty, "Expired");
				} else {
					oldResource.addProperty(startDateProperty, defaultStartString);
					oldResource.addProperty(endDateProperty, todayString);
					oldResource.addProperty(statusProperty, "Expired");
				}

				Resource newResource = model.createResource(currentSubject + "_" + todayString);
				newResource.addProperty(startDateProperty, nextDayString);
				newResource.addProperty(endDateProperty, defaultEndString);
				newResource.addProperty(statusProperty, "Current");

				changeProvIRI(currentSubject, currentSubject + "_" + todayString, provGraph);
				ArrayList<String> changedSubjects = getCorrespondingChangedSubjects(currentSubject, model);

				for (String string : changedSubjects) {
					if (!string.equals(currentSubject)) {
						sparql = "SELECT * WHERE {?S ?P ?O. FILTER regex(str(?S), '" + string + "').}";
						query = QueryFactory.create(sparql);
						execution = QueryExecutionFactory.create(query, model);
						resultSet = ResultSetFactory.copyResults(execution.execSelect());

						boolean hasDateSubject = false;
						String startDateSubject = null;

						while (resultSet.hasNext()) {
							QuerySolution querySolution = (QuerySolution) resultSet.next();
							String subjectString = querySolution.get("S").toString();
							String predicateString = querySolution.get("P").toString();
							RDFNode object = querySolution.get("O");

							if (predicateString.contains("startDate")) {
								hasDateSubject = true;
								startDateSubject = object.toString();
							}

							if (subjectString.equals(string)) {
								Resource duplicateResource = model.createResource(subjectString + "_" + todayString);
								Property property = model.createProperty(predicateString);
								duplicateResource.addProperty(property, object);
							}
						}

						Resource oldResourceSubject = model.createResource(string);

						if (hasDateSubject) {
							oldResourceSubject.removeAll(startDateProperty);
							oldResourceSubject.removeAll(endDateProperty);
							oldResourceSubject.removeAll(statusProperty);

							oldResourceSubject.addProperty(startDateProperty, startDateSubject);
							oldResourceSubject.addProperty(endDateProperty, todayString);
							oldResourceSubject.addProperty(statusProperty, "Expired");
						} else {
							oldResource.addProperty(startDateProperty, defaultStartString);
							oldResource.addProperty(endDateProperty, todayString);
							oldResource.addProperty(statusProperty, "Expired");
						}

						Resource duplicateResource = model.createResource(string + "_" + todayString);
						duplicateResource.addProperty(startDateProperty, nextDayString);
						duplicateResource.addProperty(endDateProperty, defaultEndString);
						duplicateResource.addProperty(statusProperty, "Current");

						changeProvIRI(string, string + "_" + todayString, provGraph);
					}
				}
			}
		}

		return saveModel(model, resultFile);
	}

	private ArrayList<String> getCorrespondingChangedSubjects(String previousSubject, Model model) {
		// TODO Auto-generated method stub
		ArrayList<String> changedSubjects = new ArrayList<>();

		String sparql = "SELECT ?S ?O WHERE {?S ?P ?O. FILTER regex(str(?O), '" + previousSubject + "').}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			String subjectString = querySolution.get("S").toString();
			RDFNode object = querySolution.get("O");

			if (object.toString().equals(previousSubject)) {
				if (!changedSubjects.contains(subjectString)) {
					changedSubjects.add(subjectString);
				}
			}
		}

		return changedSubjects;
	}

	private void changeProvIRI(String previousSubject, String newSubject, String provGraph) {
		// TODO Auto-generated method stub
		Model model = readModelFromFilePath(provGraph);

		Resource resource = model.getResource(previousSubject);
		ResourceUtils.renameResource(resource, newSubject);

		Resource newResource = model.getResource(newSubject);
		Property property = model.createProperty(provBaseIRI + "wasRevisionOf");
		newResource.removeAll(property);
		newResource.addProperty(property, model.createResource(previousSubject));

		saveModel(model, provGraph);
	}

	private LinkedHashMap<String, LinkedHashMap<String, RDFNode>> getCorrespondingSourceValues(Model model,
			ArrayList<String> distinctChangedSourceSubjects,
			LinkedHashMap<String, String> correspondingTargetProperties) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, LinkedHashMap<String, RDFNode>> linkedHashMap = new LinkedHashMap<>();
		for (String string : distinctChangedSourceSubjects) {
			String sparql = "SELECT * WHERE {?S ?P ?O. FILTER regex(str(?S), '" + string + "').}";

			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

			/*
			 * ResultSet set = ResultSetFactory.copyResults(resultSet);
			 * ResultSetFormatter.out(set);
			 */

			LinkedHashMap<String, RDFNode> hashMap = new LinkedHashMap<>();
			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subject = querySolution.get("S").toString();
				String predicate = querySolution.get("P").toString();
				RDFNode object = querySolution.get("O");

				if (string.equals(subject)) {
					if (correspondingTargetProperties.containsKey(predicate)) {
						hashMap.put(correspondingTargetProperties.get(predicate), object);
					}
				}
			}

			linkedHashMap.put(string, hashMap);

			execution.close();
		}
		return linkedHashMap;
	}

	private LinkedHashMap<String, String> getTargetProperties(String modelPath, ArrayList<String> arrayList) {
		// TODO Auto-generated method stub
		Model model = readModelFromFilePath(modelPath);
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

		for (String string : arrayList) {
			String sparql = "PREFIX map: <http://www.map.org/example#>"
					+ "SELECT ?P ?O WHERE {?R a map:PropertyMapper. ?R map:sourceProperty ?P. ?R map:targetProperty ?O. FILTER regex(str(?P), '"
					+ string + "').}";
			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subject = querySolution.get("P").toString();
				String object = querySolution.get("O").toString();

				if (subject.equals(string)) {
					linkedHashMap.put(string, object);
				}
			}

			execution.close();
		}
		return linkedHashMap;
	}

	private LinkedHashMap<String, String> getUpdateType(String modelPath, LinkedHashMap<String, String> hashMap) {
		// TODO Auto-generated method stub
		Model model = readModelFromFilePath(modelPath);
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
		for (Entry<String, String> entry : hashMap.entrySet()) {
			String value = entry.getValue();

			String sparql = "PREFIX cube: <http://purl.org/qb4olap/cubes#>"
					+ "SELECT ?S ?O WHERE {?S ?P ?O. ?S cube:updateType ?O.  FILTER regex(str(?S), '" + value + "').}";
			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				String subject = querySolution.get("S").toString();
				String object = querySolution.get("O").toString();

				if (subject.equals(value)) {
					linkedHashMap.put(value, object);
				}
			}

			execution.close();
		}
		return linkedHashMap;
	}

	private LinkedHashMap<String, String> getProvIRI(String modelPath, ArrayList<String> arrayList) {
		Model model = readModelFromFilePath(modelPath);
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
		for (String string : arrayList) {
			String sparql = "SELECT DISTINCT ?S WHERE {?S ?P ?O. FILTER regex(str(?S), '" + getResourceValue(string)
					+ "').}";
			Query query = QueryFactory.create(sparql);
			QueryExecution execution = QueryExecutionFactory.create(query, model);
			ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

			while (resultSet.hasNext()) {
				QuerySolution querySolution = (QuerySolution) resultSet.next();
				linkedHashMap.put(string, querySolution.get("S").toString());
			}

			execution.close();
		}
		return linkedHashMap;
	}

	private ArrayList<String> getDistinctProperties(Model model) {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<>();

		String sparql = "SELECT DISTINCT ?P WHERE {?S ?P ?O.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			arrayList.add(querySolution.get("P").toString());
		}

		execution.close();
		return arrayList;
	}

	private ArrayList<String> getDistinctSubjects(Model model) {
		// TODO Auto-generated method stub
		ArrayList<String> arrayList = new ArrayList<>();

		String sparql = "SELECT DISTINCT ?S WHERE {?S ?P ?O.}";
		Query query = QueryFactory.create(sparql);
		QueryExecution execution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = ResultSetFactory.copyResults(execution.execSelect());

		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			arrayList.add(querySolution.get("S").toString());
		}

		execution.close();
		return arrayList;
	}

	private String saveModel(Model model, String path) {
		// TODO Auto-generated method stub
		try {
			OutputStream outputStream = new FileOutputStream(path);
			String[] parts = path.split("\\.");
			model.write(outputStream, parts[parts.length - 1].toUpperCase());

			return "File saved: " + path;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in saving File";
		}
	}

	private void printModel(Model model) {
		// TODO Auto-generated method stub
		model.write(System.out, "TTL");
	}

	private Model readModelFromFilePath(String newSourceData) {
		// TODO Auto-generated method stub
		return ModelFactory.createDefaultModel().read(newSourceData);
	}

	private String getResourceValue(String subject) {
		// TODO Auto-generated method stub
		if (subject.contains("#")) {
			String[] segments = subject.split("#");
			if (segments.length == 2) {
				return segments[1].trim();
			} else {
				return subject;
			}
		} else if (subject.contains("_")) {
			String[] segments = subject.split("_");
			String part1 = segments[0];
			part1 = getResourceValue(part1);

			for (int i = 1; i < segments.length; i++) {
				part1 = part1 + segments[i];
			}

			return part1;
		} else {
			String[] segments = subject.split("/");
			return segments[segments.length - 1];
		}
	}
}
