package model;

import java.util.ArrayList;

public class RMLFile {

	
	// return triplemaps grenerated from tableConfiguratioins
	public ArrayList<TripleMap> getTripleMapsTC(ArrayList<DBTable> allTableConfigurations, String baseURL) {

		
		ArrayList<TripleMap> tripleMaps = new ArrayList<>();
		TripleMap currentTripleMap;

		int numOfDBTable = allTableConfigurations.size();
		
		//System.out.println("Num of Tables: " + numOfDBTable);

		for (int i = 0; i < numOfDBTable; i++) {

//			if(i>3)
//				break;
//			
			
			DBTable dbTableConfiguration = allTableConfigurations.get(i);

			// values for TripleMap
			String tableName = dbTableConfiguration.getTableName();
			String tripleMapName = "<#TriplesMap" + (i+1)+">";

			// values for tripleMaps
			currentTripleMap = new TripleMap(tableName, tripleMapName);

			// values for SubjectMap
			String subjectTermMapType = "template";
			String subjectTermMapValue = "";
			ArrayList<String> subjectClasses = new ArrayList<>();

			int numOfPrimaryKeys = dbTableConfiguration.getPrimaryKeys().size();
			
			if(numOfPrimaryKeys == 1){
				
				String pkName = dbTableConfiguration.getPrimaryKeys().get(0);
				subjectTermMapValue += "\"" + baseURL + tableName + "/" + "{" + pkName + "}\"";
			}
			else if(numOfPrimaryKeys > 1){
				
				for (int loopIndex = 0; loopIndex<numOfPrimaryKeys; loopIndex++) {
					String PK = dbTableConfiguration.getPrimaryKeys().get(loopIndex);
					if(loopIndex == 0)
						subjectTermMapValue += "\"" + baseURL + tableName + "/" + PK +"="+"{" + PK + "}";
					else
						subjectTermMapValue += "/"+ PK +"="+"{"+PK+"}";
				}
				subjectTermMapValue+="\"";
				
			}else {
				
				//no primary keys
				
			}
			
		
			for (String PK : dbTableConfiguration.getPrimaryKeys()) {
				subjectClasses.add("base:" + tableName);
				break;
			}

			SubjectMap subjectMap = new SubjectMap(subjectTermMapType, subjectTermMapValue, subjectClasses);
			currentTripleMap.setSubjectMap(subjectMap);

			// Generating values for PredicateObjectMaps

			// Generating values for ObjectMap

			ArrayList<String> dataColumnNames = dbTableConfiguration.getDataColumns();
			int numOfDataColumns = dataColumnNames.size();
			ArrayList<Boolean> dataColumnTypeURL = dbTableConfiguration.getDataColumnsIsURL();
			ArrayList<String> dataColumnValues = dbTableConfiguration.getDataColumnValues();

			for (int j = 0; j < numOfDataColumns; j++) {

				PredicateObjectMap predicateObjectMap;
				PredicateMap predicateMap = new PredicateMap();
				ObjectMap objectMap = new ObjectMap();

				String objectTermMapType;
				String objectTermMapValue;
				String objetcTermType; // rr:Literal, IRI, BlankNode

				if (dataColumnTypeURL.get(j)) {

					// column value is url
					String predicateTermMapType = "";
					String predicateTermMapValue = "base:" + dataColumnNames.get(j);

					predicateMap.setTermMapvalue(predicateTermMapValue);
					predicateMap.setTermpMapType(predicateTermMapType);

					objectTermMapType = "template";
					objectTermMapValue = "\"" + dataColumnValues.get(j) + "{" + dataColumnNames.get(j) + "}" + "\"";
					objetcTermType = "IRI";

					objectMap.setTermMapType(objectTermMapType);
					objectMap.setTermMapValue(objectTermMapValue);
					objectMap.setTermType(objectTermMapType);

				} else {

					String predicateTermMapType = "constant";
					String predicateTermMapValue = "base:" + dataColumnNames.get(j);
					
					predicateMap.setTermMapvalue(predicateTermMapValue);
					predicateMap.setTermpMapType(predicateTermMapType);

					objectTermMapType = "column";
					objectTermMapValue = "\"" + dataColumnNames.get(j) + "\"";
					objetcTermType = "Literal";
					
					objectMap.setTermMapType(objectTermMapType);
					objectMap.setTermMapValue(objectTermMapValue);
					objectMap.setTermType(objectTermMapType);

				}
				predicateObjectMap = new PredicateObjectMap(predicateMap, objectMap);

				currentTripleMap.getPredicateObjectMaps().add(predicateObjectMap);

			}

			// Generating values for REFObjectMap

			for (ForeignKey FK : dbTableConfiguration.getForeignKeys()) {

				PredicateObjectMap predicateObjectMap;
				PredicateMap predicateMap = new PredicateMap();
				ReferencingObjectMap referencingObjectMap = new ReferencingObjectMap();

				
				ArrayList<String> sourceColumnNames = FK.getColumnNames();
				ArrayList<String> targetColumnNames = FK.getTargetTableColumnNames();
				String targetTableName  = FK.getTableName();
				
 				int numOfForeignKeyColumn = sourceColumnNames.size();
 				
 				
 				for(int k=0; k<numOfForeignKeyColumn; k++){
 					
 					// column value is url
 					String predicateTermMapType = "";
 					String predicateTermMapValue = "base:" + sourceColumnNames.get(k);
 					
 					predicateMap.setTermMapvalue(predicateTermMapValue);
 					predicateMap.setTermpMapType(predicateTermMapType);
 					
 					int tripleMapNumber = getTripleMapNumber(allTableConfigurations, targetTableName);
 					String refObjectParentTripleMapName ="<#TriplesMap" + tripleMapNumber+">";
 					
 					String joinConditionChild = "\""+sourceColumnNames.get(k)+"\"";
 					String joinConditionParent = "\""+targetColumnNames.get(k)+"\"";
 					
 					ArrayList<JoinCondition> refObjectJoinConditions = new ArrayList<>();
 					JoinCondition joinCondition = new JoinCondition(joinConditionChild, joinConditionParent);
 					refObjectJoinConditions.add(joinCondition);
 					
 					referencingObjectMap.setParentTripleMap(refObjectParentTripleMapName);
 					referencingObjectMap.setJoinConditions(refObjectJoinConditions);
 					
 					predicateObjectMap = new PredicateObjectMap(predicateMap, referencingObjectMap);
 					
 					currentTripleMap.getPredicateObjectMaps().add(predicateObjectMap);
 				}

			}
			
			tripleMaps.add(currentTripleMap);
		}

		return tripleMaps;
	}

	// return table index for tripleMap number
	public int getTripleMapNumber(ArrayList<DBTable> allTableConfigurations, String tableName) {

		int size = allTableConfigurations.size();

		for (int j = 0; j < size; j++) {
			if (allTableConfigurations.get(j).getTableName().equals(tableName))
				return j+1;
		}
		return -1;
	}

	
	public String getRMLFile(ArrayList<TripleMap> tripleMaps, String baseURL){
		
		String rmlString = "@prefix rr: <http://www.w3.org/ns/r2rml#>.\n";
		rmlString+="@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n";
		rmlString+="@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n";
		rmlString+="@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n";
		rmlString += "@prefix base: <"+baseURL+">.\n";
		
		
		for(TripleMap tripleMap: tripleMaps){
			
			SubjectMap subjectMap = tripleMap.getSubjectMap();
			ArrayList<PredicateObjectMap> predicateObjectMaps = tripleMap.getPredicateObjectMaps();
			
			
			rmlString+=tripleMap.getTripleMapName()+"\n\n";
			rmlString+="\trr:logicalTable [ rr:tableName \""+ tripleMap.getTableName()+"\" ];\n\n";
			
			rmlString+="\trr:subjectMap [ \n";
			rmlString+="\t\trr:"+subjectMap.getTermMapType()+" "+subjectMap.getTermMapValue()+";\n";
			if(subjectMap.getClasses().size()>0){
				rmlString+="\t\trr:class ";
				for(String classValue: subjectMap.getClasses()){
					rmlString+=classValue + " ";
				}
			}
			rmlString+=";\n\t];\n\n";
		
			
			int numOfPredicateObjectMap = predicateObjectMaps.size();
			
			for(int i=0; i<numOfPredicateObjectMap; i++){
				
				PredicateObjectMap predicateObjectMap = predicateObjectMaps.get(i);
				
				PredicateMap predicateMap = predicateObjectMap.getPredicateMap();
				
				
				rmlString+="\trr:predicateObjectMap [\n";
				rmlString+="\t\trr:predicateMap "+predicateMap.getTermMapvalue()+";\n";
				if(predicateObjectMap.isReferencing()){
					
					ReferencingObjectMap referencingObjectMap = predicateObjectMap.getReferencingObjectMap();
					rmlString+="\t\trr:objectMap [\n";
					rmlString += "\t\t\trr:parentTriplesMap " + referencingObjectMap.parentTripleMapName+";\n";
					
					for(JoinCondition joinCondition: referencingObjectMap.getJoinConditions()){
						
						rmlString+="\t\t\trr:joinCondition [\n\n";
						rmlString+="\t\t\t\trr:child " + joinCondition.getChild()+";\n";
						rmlString+="\t\t\t\trr:parent " + joinCondition.getParent()+";\n";
						rmlString+="\t\t\t];\n";	
					}
					
					rmlString+="\t\t];\n";
					
				}else{
					
					ObjectMap objectMap = predicateObjectMap.getObjectMap();
					
					rmlString+="\t\trr:objectMap [ rr:" +objectMap.getTermMapType()+" "+objectMap.getTermMapValue()+" ];\n"; 
				}
				
				if(i==numOfPredicateObjectMap-1){
					rmlString+="\t].\n\n";
				}else{
					rmlString+="\t];\n";
				}
				
			}

		}
		
		return rmlString;
	}
}
