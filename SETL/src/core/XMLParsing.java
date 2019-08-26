package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import helper.Methods;

public class XMLParsing {
	Model model = ModelFactory.createDefaultModel();
	Methods fileMethods = new Methods();

	public static void main(String[] args) {
		String sourceFileName = "C:\\Users\\Amrit\\Documents\\transformations\\pizza.xml";
		String targetFileName = "C:\\Users\\Amrit\\Documents\\transformations\\pizza_tbox.ttl";
		String prefix = "http://www.example.com";
		String columnName = "";
		XMLParsing xmlParsing = new XMLParsing();
		xmlParsing.parseXML(sourceFileName, targetFileName, prefix);
	}

	public String parseXML(String sourceFileName, String targetFileName, String prefix) {
		// TODO Auto-generated method stub
		try {
			File inputFile = new File(sourceFileName);
			SAXReader reader = new SAXReader();
			Document document = reader.read(inputFile);

			Element classElement = document.getRootElement();
			List<Element> elementList = classElement.elements();
			for (Element element : elementList) {
				createConcept(prefix + "/" + convert(element.getName()));
				
				List<Element> newElementList = element.elements();
				
				for (Element element2 : newElementList) {
					parseElement(prefix, element, element2);
				}
			}

			return fileMethods.saveModel(model, targetFileName);
		} catch (DocumentException e) {
			e.printStackTrace();
			return "Check the document";
		}
	}

	private void parseElement(String prefix, Element rootElement, Element newElement) {
		// TODO Auto-generated method stub
		// System.out.println("Element Name: " + newElement.getName());

		List<Element> elementList = newElement.elements();
		if (elementList.size() != 0) {
			createConcept(prefix + "/" + convert(newElement.getName()));

			for (Element element : elementList) {
				parseElement(prefix, newElement, element);
			}

			createObjectProperty(prefix, rootElement, newElement);
		} else {
			createDataProperty(prefix, rootElement, newElement);
		}

		/*
		 * if (!newElement.getData().toString().trim().equals("")) {
		 * System.out.println("Data: " + newElement.getData()); }
		 */
		List<Attribute> attributeList = newElement.attributes();

		for (Attribute attribute : attributeList) {
			parseAttribute(attribute);
		}
	}

	private void createDataProperty(String prefix, Element rootElement, Element newElement) {
		// TODO Auto-generated method stub
		Resource classResource = model.createResource("http://www.w3.org/2002/07/owl#DatatypeProperty");
		Resource resource = model.createResource(prefix + "/" + newElement.getName());
		resource.addProperty(RDF.type, classResource);

		Property predicateOne = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#domain");
		Resource domainResource = model.createResource(prefix + "/" + convert(rootElement.getName()));
		resource.addProperty(predicateOne, domainResource);

		Property predicateTwo = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#range");
		Literal literal = model.createTypedLiteral(newElement.getData());
		Resource literalResource = model.createResource(literal.getDatatypeURI());
		resource.addProperty(predicateTwo, literalResource);
	}

	private void createObjectProperty(String prefix, Element rootElement, Element newElement) {
		// TODO Auto-generated method stub
		Resource classResource = model.createResource("http://www.w3.org/2002/07/owl#ObjectProperty");
		Resource resource = model.createResource(prefix + "/" + newElement.getName().toLowerCase());
		resource.addProperty(RDF.type, classResource);

		Property predicateOne = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#domain");
		Resource domainResource = model.createResource(prefix + "/" + rootElement.getName());
		resource.addProperty(predicateOne, domainResource);

		/*List<Element> elementList = newElement.elements();
		for (Element element : elementList) {
			Property predicateTwo = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#range");
			Resource rangeResource = model.createResource(prefix + "#" + element.getName());
			resource.addProperty(predicateTwo, rangeResource);
		}*/
		
		Property predicateTwo = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#range");
		Resource rangeResource = model.createResource(prefix + "/" + convert(newElement.getName()));
		resource.addProperty(predicateTwo, rangeResource);
	}

	private void createConcept(String name) {
		// TODO Auto-generated method stub
		Resource classResource = model.createResource("http://www.w3.org/2002/07/owl#Class");
		Resource newResource = model.createResource(name);
		newResource.addProperty(RDF.type, classResource);
	}

	private String convert(String str) {
		// TODO Auto-generated method stub
		StringBuffer s = new StringBuffer(); 
        char ch = ' '; 
        for (int i = 0; i < str.length(); i++) { 
            if (ch == ' ' && str.charAt(i) != ' ') 
                s.append(Character.toUpperCase(str.charAt(i))); 
            else
                s.append(str.charAt(i)); 
            ch = str.charAt(i); 
        } 
  
        return s.toString().trim(); 
	}

	private static void parseAttribute(Attribute attribute) {
		// TODO Auto-generated method stub
		System.out.println(attribute.getName() + " - " + attribute.getData());
	}
}
