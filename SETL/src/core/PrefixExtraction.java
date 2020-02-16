package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrefixExtraction {
	public static LinkedHashMap<String, String> prefixMap;

//	public static void main(String[] args) {
//		PrefixExtraction prefixExtraction = new PrefixExtraction();
//		prefixExtraction.extractPrefix("C:\\Users\\Amrit\\Documents\\test\\test.ttl");
//	}

	public PrefixExtraction() {
		initAll();
		addPredefinedPrefix();
	}

	private void addPredefinedPrefix() {
		// TODO Auto-generated method stub
		prefixMap.put("owl:", "http://www.w3.org/2002/07/owl#");
		prefixMap.put("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefixMap.put("skos:", "http://www.w3.org/2004/02/skos/core#");
		prefixMap.put("qb:", "http://purl.org/linked-data/cube#");
		prefixMap.put("xsd:", "http://www.w3.org/2001/XMLSchema#");
		prefixMap.put("qb4o:", "http://purl.org/qb4olap/cubes#");
		prefixMap.put("dct:", "http://purl.org/dc/terms/");
		prefixMap.put("rdfs:", "http://www.w3.org/2000/01/rdf-schema#");
		prefixMap.put("wgs:", "http://www.w3c.org/2003/01/geo/wgs84_pos#");
		prefixMap.put("xml:", "http://www.w3.org/XML/1998/namespace");
		prefixMap.put("aowl:", "http://bblfish.net/work/atom-owl/2006-06-06/");
		prefixMap.put("foaf:", "http://xmlns.com/foaf/0.1/");
		prefixMap.put("time:", "http://www.w3.org/2006/time#");
		prefixMap.put("dbpcat:", "http://dbpedia.org/resource/Category:");
		prefixMap.put("dbpedia:", "http://dbpedia.org/resource/");
		prefixMap.put("virtrdf:", "http://www.openlinksw.com/schemas/virtrdf#");
		prefixMap.put("geonames:", "http://www.geonames.org/ontology#");
		prefixMap.put("map:", "http://www.map.org/example#");
		prefixMap.put("onto:", "http://www.onto.org/schema#");
	}

	private void initAll() {
		// TODO Auto-generated method stub
		prefixMap = new LinkedHashMap<>();
	}

	public void extractPrefix(String filePath) {
		File file = new File(filePath);
		BufferedReader bufferedReader;

		try {
			bufferedReader = new BufferedReader(new FileReader(file));

			String textString = "", s;
			int count = 0;

			StringBuilder stringBuilder = new StringBuilder(textString);
			while ((s = bufferedReader.readLine()) != null) {
				stringBuilder.append(s);

				if (count == 500) {
					break;
				}
				count++;
			}

			String regEx = "(@prefix\\s*)([^:]+:)(\\s+)(<)([^\\s+^>]+)";
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = pattern.matcher(stringBuilder.toString());

			while (matcher.find()) {
				String prefix = matcher.group(2).trim();
				String iri = matcher.group(5).trim();

				prefixMap.put(prefix, iri);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
	}

	public String assignIRI(String prefix) {
		if (prefix.contains("http") || prefix.contains("www")) {
			return prefix;
		} else {
			String[] segments = prefix.split(":");
			if (segments.length == 2) {
				String firstSegment = segments[0] + ":";
				return prefixMap.get(firstSegment) + segments[1];
			} else {
				return prefix;
			}
		}
	}

	public String assignPrefix(String iri) {
//		System.out.println("IRI: " + iri);
		
		if (iri.contains("#")) {
			String[] segments = iri.split("#");
			if (segments.length == 2) {
				String firstSegment = segments[0].trim() + "/";

				for (Map.Entry<String, String> map : prefixMap.entrySet()) {
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

			for (Map.Entry<String, String> map : prefixMap.entrySet()) {
				String key = map.getKey();
				String value = map.getValue();

				if (firstSegment.equals(value.trim())) {
					return key + lastSegment;
				}
			}

			return iri;
		}
	}
}
