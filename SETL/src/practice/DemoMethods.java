package practice;

public class DemoMethods {
	public static void main(String[] args) {
		extractKeyWordFromIRI("CONCAT(onto:admUnitFiveId,CONCAT(_,CONCAT(onto:residence,CONCAT(_,onto:ageGroup))))");
		extractKeyWordFromIRI("onto:admUnitFiveId");
		extractKeyWordFromIRI("http://www.onto.org/schema#numberOfPopulation");
	}

	public static String extractKeyWordFromIRI(String expression) {
		// TODO Auto-generated method stub
		expression = expression.replaceAll("[^\\w:\\/.#,]", "");
		expression = expression.replace("_", "");
		expression = expression.replace("CONCAT", "");
		expression = expression.replace("CONTAINS", "");
		expression = expression.replace("SPLIT", "");
		expression = expression.replace("REPLACE", "");
		expression = expression.replace("ToNumber", "");
		expression = expression.replace("ToString", "");
		expression = expression.replace("COMPARE", "");
		
		String parts[] = expression.split(",");
		String key = "";
		
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].length() > 0) {
				key += getLastSegmentOfIRI(parts[i]);
				
				if (i < parts.length - 1) {
					key += "_";
				}
			}
		}
		
		return key;
	}
	
	public static String getLastSegmentOfIRI(String urlName) {
		if (urlName.contains("www") || urlName.contains("http")) {
			if (urlName.contains("#")) {
				String[] parts = urlName.split("#");
				return parts[1];
			} else {
				String[] parts = urlName.split("/");
				return parts[parts.length - 1];
			}
		} else {
			String[] parts = urlName.split(":");
			return parts[1];
		}
	}
}
