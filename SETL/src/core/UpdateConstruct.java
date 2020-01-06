package core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.ResourceUtils;
import helper.Methods;
import model.PropertyConstruct;
import model.SubjectConstruct;

public class UpdateConstruct {
	private Model provModel;

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		String basePathString = "I:\\Documents\\SETL\\construct\\tiny\\";
//		// String sourceTypeString = "http://extbi.lab.aau.dk/ontolgoy/subsidy#Recipient";
//		String sourceTypeString = "http://extbi.lab.aau.dk/ontolgoy/subsidy#City";
//		// String propertyString = "http://extbi.lab.aau.dk/ontolgoy/subsidy/recipientname";
//		String propertyString = "http://extbi.lab.aau.dk/ontolgoy/subsidy/cityName";
//		String changedValueString = "Newton";
//
//		// String dimensionConstruct = "http://extbi.lab.aau.dk/ontolgoy/sdw/mdProperty#Recipient";
//		String dimensionConstruct =
//		"http://extbi.lab.aau.dk/ontolgoy/sdw/mdProperty#City";
//		String newSourceData = basePathString + "newSourceABox.ttl";
//		String oldSourceData = basePathString + "sourceABox.ttl";
//		String sourceTBox = basePathString + "recipientsourceTBox.ttl";
//		String targetTBox = basePathString + "subsidy.ttl";
//		String targetABox = basePathString + "targetABox.ttl";
//		String prefix = "http://example.com/sdw/";
//		String mapper = basePathString + "map_current.ttl";
//		String provGraph = basePathString + "prov.ttl";
//		String resultFile = basePathString + "new_result_temp.ttl";
//
//		/*
//		 * for (int i = 1; i < 5; i++) { System.out.println("*** Phase " + i +
//		 * " started ***");
//		 * 
//		 * String provGraphSecond = basePathString + "prov" + i + ".ttl";
//		 * 
//		 * Methods.createNewFile(provGraphSecond);
//		 * 
//		 * Methods.startProcessingTime(); changeTuple(oldSourceData, sourceTypeString,
//		 * propertyString, changedValueString, newSourceData, i);
//		 * 
//		 * System.out.println("Data changed"); Methods.endProcessingTime();
//		 * 
//		 * String updateType = "Type1"; String resultFileSecond = basePathString +
//		 * "result_" + i + "_target_" + updateType + ".ttl";
//		 * 
//		 * Methods.startProcessingTime();
//		 * 
//		 * UpdateConstruct updateConstruct = new UpdateConstruct(); String resultString
//		 * = updateConstruct.updateDimensionConstruct(dimensionConstruct, newSourceData,
//		 * oldSourceData, sourceTBox, targetTBox, targetABox, prefix, mapper,
//		 * provGraphSecond, resultFileSecond, updateType); System.out.println(updateType
//		 * + " = " + resultString);
//		 * 
//		 * Methods.endProcessingTime();
//		 * 
//		 * updateType = "Type3"; resultFileSecond = basePathString + "result_" + i +
//		 * "_target_" + updateType + ".ttl";
//		 * 
//		 * Methods.startProcessingTime();
//		 * 
//		 * resultString = updateConstruct.updateDimensionConstruct(dimensionConstruct,
//		 * newSourceData, oldSourceData, sourceTBox, targetTBox, targetABox, prefix,
//		 * mapper, provGraphSecond, resultFileSecond, updateType);
//		 * System.out.println(updateType + " = " + resultString);
//		 * 
//		 * Methods.endProcessingTime();
//		 * 
//		 * updateType = "Type2"; resultFileSecond = basePathString + "result_" + i +
//		 * "_target_" + updateType + ".ttl";
//		 * 
//		 * Methods.startProcessingTime();
//		 * 
//		 * resultString = updateConstruct.updateDimensionConstruct(dimensionConstruct,
//		 * newSourceData, oldSourceData, sourceTBox, targetTBox, targetABox, prefix,
//		 * mapper, provGraphSecond, resultFileSecond, updateType);
//		 * System.out.println(updateType + " = " + resultString);
//		 * 
//		 * Methods.endProcessingTime();
//		 * 
//		 * 
//		 * String updateType = "Type2"; String resultFileSecond = basePathString +
//		 * "result_" + i + "_target_" + updateType + ".ttl";
//		 * 
//		 * Methods.startProcessingTime();
//		 * 
//		 * String resultString = new
//		 * UpdateConstruct().updateDimensionConstruct(dimensionConstruct, newSourceData,
//		 * oldSourceData, sourceTBox, targetTBox, targetABox, prefix, mapper,
//		 * provGraphSecond, resultFileSecond, updateType); System.out.println(updateType
//		 * + " = " + resultString);
//		 * 
//		 * Methods.endProcessingTime();
//		 * 
//		 * System.out.println("*** Phase " + i + " completed ***");
//		 * 
//		 * 
//		 * System.out.println(); System.out.println(); }
//		 * 
//		 * System.out.println("Finished");
//		 */
//
//	
//		Methods.createNewFile(provGraph);
//
//		changeTuple(oldSourceData, sourceTypeString, propertyString, changedValueString, newSourceData, 1);
//
//		Methods.startProcessingTime();
//
//		String updateType = "Type2";
//
//		UpdateConstruct updateConstruct = new UpdateConstruct();
//		String resultString = updateConstruct.updateDimensionConstruct(dimensionConstruct, newSourceData,
//				oldSourceData, sourceTBox, targetTBox, targetABox, prefix, mapper, provGraph, resultFile,
//				updateType);
//		System.out.println(resultString);
//
//		Methods.endProcessingTime();
//		System.out.println("Done");
//
////		Long totalDifference = 0L;
////		changeTuple(oldSourceData, sourceTypeString, propertyString, changedValueString, newSourceData, 4);
////		
////		for (int j = 0; j < 10; j++) {
////			Methods.startProcessingTime();
////			Long startTimeLong = Methods.getTime();
////			
////			String provGraphSecond = basePathString + "prov" + (j + 1) + ".ttl";
////			Methods.createNewFile(provGraphSecond);
////			
////			System.out.println(provGraphSecond);
////			
////			String updateType = "Type1";
////			
////			UpdateConstruct updateConstruct = new UpdateConstruct();
////			String resultString = updateConstruct.updateDimensionConstruct(dimensionConstruct, newSourceData,
////					oldSourceData, sourceTBox, targetTBox, targetABox, prefix, mapper, provGraphSecond, resultFile,
////					updateType);
////			System.out.println(resultString);
////			
////			Long endTimeLong = Methods.getTime();
////			totalDifference += endTimeLong - startTimeLong;
////			
////			Methods.endProcessingTime();
////		}
////		
////		totalDifference = totalDifference / 10;
////		
////		String timeStringOne = "Required Time for processing: " + Methods.getTimeInSeconds(totalDifference);
////
////		System.out.println(timeStringOne);
//	}
//
//	
	public String updateDimensionConstruct(String dimensionConstruct, String newSourceData, String oldSourceData,
			String sourceTBox, String targetTBox, String targetABox, String prefix, String mapper, String provGraph,
			String resultFile, String updateType) {
		// TODO Auto-generated method stub

		prefix = Methods.validatePrefix(prefix);

		Model newSourceModel = Methods.readModelFromPath(newSourceData);
		Model oldSourceModel = Methods.readModelFromPath(oldSourceData);

		Model sourceModel = newSourceModel.difference(oldSourceModel);
		newSourceModel.close();
		oldSourceModel.close();

		Model mapModel = Methods.readModelFromPath(mapper);
		Model targetModel = Methods.readModelFromPath(targetABox);
		Model targetTBoxModel = Methods.readModelFromPath(targetTBox);
		provModel = Methods.readModelFromPath(provGraph);

		Model completeModel = ModelFactory.createDefaultModel();
		completeModel.add(sourceModel);
		completeModel.add(mapModel);
		completeModel.add(targetTBoxModel);
		completeModel.add(provModel);

		String sparqlString = "PREFIX map: <http://www.map.org/example#>"
				+ "PREFIX qb4o: <http://purl.org/qb4olap/cubes#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "SELECT ?s ?t ?o ?u ?prange ?prov ?ttype ?range "
				+ "WHERE "
				+ "{" + "?s ?p ?o."
				+ "?mapper map:sourceProperty ?p."
				+ "?mapper map:targetProperty ?t."
				+ "?t qb4o:updateType ?u."
				+ "?t rdfs:range ?prange. "
				+ "OPTIONAL {?s owl:sameAs ?prov.}"
				+ "?mapper map:ConceptMapper ?concept."				
				+ "?concept map:targetConcept ?ttype."
				+ "OPTIONAL {?ttype rdfs:range ?range.}"
				+ "}";

		ResultSet resultSet = Methods.executeQuery(completeModel, sparqlString);
//		Methods.print(resultSet);

		LinkedHashMap<String, SubjectConstruct> subjectConstructMap = new LinkedHashMap<String, SubjectConstruct>();
		// LinkedHashMap<String, String> subjectLinkMap = new LinkedHashMap<String,
		// String>();

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			String subjectString = querySolution.get("s").toString();
			String propertyString = querySolution.get("t").toString();
			RDFNode valueNode = querySolution.get("o");
			String updateTypeString = querySolution.get("u").toString();
			String typeString = querySolution.get("ttype").toString();
			String pRangeString = querySolution.get("prange").toString();

			if (!updateType.toLowerCase().contains("update")) {
				updateTypeString = updateType;
			}
			
//			System.out.println(updateTypeString);

			if (subjectConstructMap.containsKey(subjectString)) {
				SubjectConstruct subjectConstruct = subjectConstructMap.get(subjectString);

				PropertyConstruct propertyConstruct = subjectConstruct.getPropertyMap().get(propertyString);
				if (!propertyConstruct.getValueList().contains(valueNode)) {
					propertyConstruct.getValueList().add(valueNode);
				}

				subjectConstruct.getPropertyMap().put(propertyString, propertyConstruct);
				subjectConstructMap.put(subjectString, subjectConstruct);
			} else {
				SubjectConstruct subjectConstruct = new SubjectConstruct();
				subjectConstruct.setSubjectString(subjectString);
				subjectConstruct.setTypeString(typeString);

				String provString = "";
				if (querySolution.get("prov") != null) {
					provString = querySolution.get("prov").toString();
					subjectConstruct.setProvString(provString);
					subjectConstruct.setHasProvIRI(true);
				}

				String rangeString = "";
				if (querySolution.get("range") != null) {
					rangeString = querySolution.get("range").toString();
					subjectConstruct.setRangeString(rangeString);
					subjectConstruct.setHasRange(true);
				}

				PropertyConstruct propertyConstruct = new PropertyConstruct();
				propertyConstruct.setPropertyString(propertyString);
				propertyConstruct.setRangeString(pRangeString);
				propertyConstruct.setUpdateTypeString(updateTypeString);
				propertyConstruct.getValueList().add(valueNode);

				subjectConstruct.getPropertyMap().put(propertyString, propertyConstruct);
				subjectConstructMap.put(subjectString, subjectConstruct);
				// subjectLinkMap.put(Methods.getLastSegmentOfIRI(subjectString),
				// subjectString);
			}
		}

//		System.out.println("Subject Map: " + subjectConstructMap.size());

		/*
		 * for (String subjectString : subjectConstructMap.keySet()) { SubjectConstruct
		 * construct = subjectConstructMap.get(subjectString);
		 * 
		 * System.out.println("Property Map: " + construct.getPropertyMap().size());
		 * 
		 * for (String propertyString : construct.getPropertyMap().keySet()) {
		 * PropertyConstruct construct2 =
		 * construct.getPropertyMap().get(propertyString);
		 * System.out.println("Value Map: " + construct2.getValueList().size()); } }
		 */

		ArrayList<String> checkedList = new ArrayList<String>();

		for (String subjectString : subjectConstructMap.keySet()) {
//			System.out.println(subjectString);
			SubjectConstruct subjectConstruct = subjectConstructMap.get(subjectString);

			Resource tempResource = targetModel.createResource(subjectString);
			Property tempProperty = targetModel.createProperty("http://purl.org/qb4olap/cubes#memberOf");
			Statement tempStatement = tempResource.getProperty(tempProperty);
			if (!tempStatement.getObject().toString().equals(dimensionConstruct)) {
				continue;
			}

			String iriString = "";

			if (subjectConstruct.isHasProvIRI()) {
				iriString = subjectConstruct.getProvString();
			} else {
				if (subjectConstruct.isHasRange()) {
					iriString = subjectConstruct.getRangeString() + "#" + Methods.getLastSegmentOfIRI(subjectString);
				} else {
					iriString = Methods.createSlashTypeString(subjectConstruct.getTypeString()) + "#"
							+ Methods.getLastSegmentOfIRI(subjectString);
				}
			}

			LinkedHashMap<String, PropertyConstruct> propertyMap = subjectConstruct.getPropertyMap();

			for (String propertyString : propertyMap.keySet()) {
				PropertyConstruct propertyConstruct = propertyMap.get(propertyString);

				if (propertyConstruct.getUpdateTypeString().contains("Type1")) {
					Resource resource = targetModel.createResource(iriString);
					Property property = targetModel.createProperty(propertyString);
					resource.removeAll(property);
//					System.out.println(propertyConstruct.getRangeString());

					for (RDFNode rdfNode : propertyConstruct.getValueList()) {
//						System.out.println(1);
						if (propertyConstruct.getRangeString().contains("http://www.w3.org/2001/XMLSchema#")) {
							resource.addProperty(property, rdfNode);
						} else {
							String value = Methods.formatURL(rdfNode.toString());
							String propertyValueIRI = propertyConstruct.getRangeString() + "#" + value;
							resource.addProperty(property, targetModel.createResource(propertyValueIRI));
						}
					}
				} else if (propertyConstruct.getUpdateTypeString().contains("Type3")) {
					Resource resource = targetModel.createResource(iriString);
					Property property = targetModel.createProperty(propertyString);
					Property oldProperty = targetModel.createProperty(propertyString + "_oldValue");

					StmtIterator stmtIterator = resource.listProperties(property);

					ArrayList<RDFNode> previousRdfNodes = new ArrayList<RDFNode>();
					while (stmtIterator.hasNext()) {
						Statement statement = stmtIterator.nextStatement();
						previousRdfNodes.add(statement.getObject());
					}
					stmtIterator.close();

					for (RDFNode rdfNode : previousRdfNodes) {
						resource.addProperty(oldProperty, rdfNode);
					}

					resource.removeAll(property);

					for (RDFNode rdfNode : propertyConstruct.getValueList()) {
//						System.out.println(1);
						if (propertyConstruct.getRangeString().contains("http://www.w3.org/2001/XMLSchema#")) {
							resource.addProperty(property, rdfNode);
						} else {
							String value = Methods.formatURL(rdfNode.toString());
							String propertyValueIRI = propertyConstruct.getRangeString() + "#" + value;
							resource.addProperty(property, targetModel.createResource(propertyValueIRI));
						}
					}
				} else if (propertyConstruct.getUpdateTypeString().contains("Type2")) {
					String todayString = getTodayString();
					String newResourceString = iriString + "_" + todayString;

					Resource oldResource = targetModel.createResource(iriString);
					Resource newResource = targetModel.createResource(newResourceString);
					
					StmtIterator iterator1 = targetModel.listStatements(null, null, oldResource);
					
					ArrayList<String> dependenceList = new ArrayList<String>();
					
					Model tempModel = ModelFactory.createDefaultModel();
					
					while (iterator1.hasNext()) {
						Statement statement = iterator1.nextStatement();
						
						Resource childOldResource = statement.getSubject();
						
						String childNewResourceString = childOldResource.toString() + "_" + todayString;
						Resource childNewResource = tempModel.createResource(childNewResourceString);
						
						if (!dependenceList.contains(childOldResource.toString())) {
							StmtIterator stmtIterator = childOldResource.listProperties();
							boolean hasDate = false;
							String dateValue = "";
							
							while (stmtIterator.hasNext()) {
								Statement statement2 = stmtIterator.nextStatement();
								childNewResource.addProperty(statement2.getPredicate(), statement2.getObject());
								
								if (statement2.getPredicate().toString().contains("startDate")) {
									hasDate = true;
									dateValue = statement2.getObject().toString();
								}
							}
							
							addDateProperties(prefix, targetModel, tempModel, childOldResource.toString(), todayString,
									childNewResourceString, childOldResource, childNewResource, hasDate, dateValue);
							
							dependenceList.add(childOldResource.toString());
						}
						
						childNewResource.removeAll(statement.getPredicate());
						StmtIterator iterator2 = childOldResource.listProperties(statement.getPredicate());
						while (iterator2.hasNext()) {
							Statement statement3 = iterator2.nextStatement();
							
							if (statement3.getObject().toString().equals(statement.getObject().toString())) {
								childNewResource.addProperty(statement3.getPredicate(), newResource);
							} else {
								childNewResource.addProperty(statement3.getPredicate(), statement3.getObject());
							}
						}
					}
					
//					Methods.print(tempModel);
					targetModel.add(tempModel);

					if (!checkedList.contains(subjectString)) {

//						System.out.println(newResourceString);

						StmtIterator stmtIterator = oldResource.listProperties();
						boolean hasDate = false;
						String dateValue = "";
						while (stmtIterator.hasNext()) {
							Statement statement = stmtIterator.nextStatement();
							newResource.addProperty(statement.getPredicate(), statement.getObject());

							if (statement.getPredicate().toString().contains("startDate")) {
								hasDate = true;
								dateValue = statement.getObject().toString();
							}
						}

						addDateProperties(prefix, targetModel, subjectString, todayString, newResourceString, oldResource,
								newResource, hasDate, dateValue);

						checkedList.add(subjectString);
					}

					Property property = targetModel.createProperty(propertyString);
					newResource.removeAll(property);

					for (RDFNode rdfNode : propertyConstruct.getValueList()) {
//						System.out.println(1);
						if (propertyConstruct.getRangeString().contains("http://www.w3.org/2001/XMLSchema#")) {
							newResource.addProperty(property, rdfNode);
						} else {
							String value = Methods.formatURL(rdfNode.toString());
							String propertyValueIRI = propertyConstruct.getRangeString() + "#" + value;
							newResource.addProperty(property, targetModel.createResource(propertyValueIRI));
						}
					}
				}
			}
		}

		Methods.saveModel(provModel, provGraph);
		return Methods.saveModel(targetModel, resultFile);
	}

	private void addDateProperties(String prefix, Model targetModel, String subjectString, String todayString,
			String newResourceString, Resource oldResource, Resource newResource, boolean hasDate, String dateValue) {

		String defaultStartString = "01-01-1111";
		String defaultEndString = "01-01-9999";
		String nextDayString = getNextDayString();
		
		Property startDateProperty = targetModel.createProperty(prefix + "startDate");
		Property endDateProperty = targetModel.createProperty(prefix + "endDate");
		Property statusProperty = targetModel.createProperty(prefix + "status");

		if (hasDate) {
			oldResource.removeAll(startDateProperty);
			oldResource.removeAll(endDateProperty);
			oldResource.removeAll(statusProperty);

			oldResource.addProperty(startDateProperty, dateValue);
		} else {
			oldResource.addProperty(startDateProperty, defaultStartString);
		}

		oldResource.addProperty(endDateProperty, todayString);
		oldResource.addProperty(statusProperty, "Expired");

		newResource.removeAll(startDateProperty);
		newResource.removeAll(endDateProperty);
		newResource.removeAll(statusProperty);

		newResource.addProperty(startDateProperty, nextDayString);
		newResource.addProperty(endDateProperty, defaultEndString);
		newResource.addProperty(statusProperty, "Current");

		Resource provResource = provModel.createResource(subjectString);
		Property provProperty = provModel.createProperty("http://www.w3.org/2002/07/owl#sameAs");

		provResource.removeAll(provProperty);
		provResource.addProperty(provProperty, provModel.createResource(newResourceString));
	}
	
	private void addDateProperties(String prefix, Model targetModel, Model tempModel, String subjectString, String todayString,
			String newResourceString, Resource oldResource, Resource newResource, boolean hasDate, String dateValue) {

		String defaultStartString = "01-01-1111";
		String defaultEndString = "01-01-9999";
		String nextDayString = getNextDayString();
		
		Property startDateProperty = targetModel.createProperty(prefix + "startDate");
		Property endDateProperty = targetModel.createProperty(prefix + "endDate");
		Property statusProperty = targetModel.createProperty(prefix + "status");
		
		Property tempStartDateProperty = tempModel.createProperty(prefix + "startDate");
		Property tempEndDateProperty = tempModel.createProperty(prefix + "endDate");
		Property tempStatusProperty = tempModel.createProperty(prefix + "status");

		if (hasDate) {
			oldResource.removeAll(startDateProperty);
			oldResource.removeAll(endDateProperty);
			oldResource.removeAll(statusProperty);

			oldResource.addProperty(startDateProperty, dateValue);
		} else {
			oldResource.addProperty(startDateProperty, defaultStartString);
		}

		oldResource.addProperty(endDateProperty, todayString);
		oldResource.addProperty(statusProperty, "Expired");

		newResource.removeAll(tempStartDateProperty);
		newResource.removeAll(tempEndDateProperty);
		newResource.removeAll(tempStatusProperty);

		newResource.addProperty(tempStartDateProperty, nextDayString);
		newResource.addProperty(tempEndDateProperty, defaultEndString);
		newResource.addProperty(tempStatusProperty, "Current");

		Resource provResource = provModel.createResource(subjectString);
		Property provProperty = provModel.createProperty("http://www.w3.org/2002/07/owl#sameAs");

		provResource.removeAll(provProperty);
		provResource.addProperty(provProperty, provModel.createResource(newResourceString));
	}

	private String getNextDayString() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		return dateFormat.format(calendar.getTime());
	}

	private String getTodayString() {
		Calendar calendar = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		return dateFormat.format(calendar.getTime());
	}

	private static void changeTuple(String oldSourceABoxFile, String dimensionConstruct, String propertyString,
			String changeString, String newSourceABoxFile, int iteration) {
		// TODO Auto-generated method stub
		int limit = 0;
		if (dimensionConstruct.contains("Recipient")) {
			limit = (int) Math.pow(10.0, iteration * 1.0);
		} else {
			limit = 10 * iteration;
		}

		limit = 10;

		Model model = Methods.readModelFromPath(oldSourceABoxFile);
		String sparql = "SELECT DISTINCT ?s WHERE {" + "?s a <" + dimensionConstruct + ">." + "?s <" + propertyString
				+ "> ?o" + "} LIMIT " + limit;

		System.out.println("Sparql: " + sparql);

		ResultSet resultSet = Methods.executeQuery(model, sparql);
		Methods.print(resultSet);

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			String subject = querySolution.get("s").toString();

			Resource resource = model.createResource(subject);

			Property property = model.createProperty(propertyString);

			resource.removeAll(property);

			Literal literal = model.createLiteral(changeString);
			resource.addProperty(property, literal);
		}

		Methods.saveModel(model, newSourceABoxFile);
	}

	public void changeTupleWithLimit(String oldSourceABoxFile, String dimensionConstruct, String propertyString,
			String changeString, String newSourceABoxFile, int limit) {
		// TODO Auto-generated method stub

		Model model = Methods.readModelFromPath(oldSourceABoxFile);
		String sparql = "SELECT DISTINCT ?s WHERE {" + "?s a <" + dimensionConstruct + ">." + "?s <" + propertyString
				+ "> ?o" + "} LIMIT " + limit;

		System.out.println("Sparql: " + sparql);

		ResultSet resultSet = Methods.executeQuery(model, sparql);
//		Methods.print(resultSet);

		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			String subject = querySolution.get("s").toString();

			Resource resource = model.createResource(subject);

			Property property = model.createProperty(propertyString);

			resource.removeAll(property);

			Literal literal = model.createLiteral(changeString);
			resource.addProperty(property, literal);
		}

		Methods.saveModel(model, newSourceABoxFile);
	}
}
