package core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helper.Methods;

public class ExpressionHandler {

//	public Object handleExpression(String expression, Object value) {
//		// TODO Auto-generated method stub
//		System.out.println("Use equation Handler instead");
//		System.out.println("Expression: " + expression);
//		
//		ArrayList<Object> arrayList = new ArrayList<>();
//
//		String word = "";
//		
//		
//
//		for (int i = 0; i < expression.length(); i++) {
//			String character = Character.toString(expression.charAt(i));
//			String character2 = "";
//			
//			if (i < expression.length() - 1) {
//				character2 = Character.toString(expression.charAt(i + 1));
//			}
//
//			if (character.matches("[a-zA-Z0-9://#\\.\\\\\\_]")) {
//				if (character.equals("/") && character2.equals(" ")) {
//					checkCharacter(arrayList, word, character);
//				} else {
//					word = word + character;
//				}
//			} else if (character.matches("[\\(]")) {
//				if (!word.equals("")) {
//					arrayList.add(word);
//				}
//
//				arrayList.add(character);
//				word = "";
//			} else if (character.matches("[,]")) {
//				if (!word.equals("")) {
//					arrayList.add(word);
//				}
//
//				arrayList.add(character);
//				word = "";
//			} else if (character.matches("[\\)]")) {
//				if (!word.equals("")) {
//					arrayList.add(word);
//				}
//				
//				
//				arrayList = decodeExpression(arrayList, value);
//				
//				System.out.println("After decoding");
//				
//				for (Object object : arrayList) {
//					System.out.println(object);
//				}
//				
//				word = "";
//			} else if (character.equals("+") || character.equals("-") || character.equals("*")
//					|| character.equals("/")) {
//				checkCharacter(arrayList, word, character);
//			}
//		}
//
//		if (!word.equals("")) {
//			arrayList.add(word);
//			return crackExpression(arrayList, value);
//		}
//
//		if (arrayList.size() == 1) {
//			if (String.valueOf(arrayList.get(0)).trim().equals(expression.trim())) {
//				return value;
//			} else {
//				return arrayList.get(0);
//			}
//		} else {
//			return value;
//		}
//	}
//
//	private Object crackExpression(ArrayList<Object> arrayList, Object value) {
//		// TODO Auto-generated method stub
//		if (arrayList.size() == 1) {
//			return value;
//		} else {
//			int add = 0, sub = 0, mul = 0, div = 0;
//			for (Object object : arrayList) {
//				String string = String.valueOf(object);
//
//				if (string.equals("*")) {
//					mul++;
//				} else if (string.equals("/")) {
//					div++;
//				} else if (string.equals("+")) {
//					add++;
//				} else if (string.equals("-")) {
//					sub++;
//				}
//			}
//
//			for (int i = 0; i < mul; i++) {
//				int position = -1;
//				for (int j = 0; j < arrayList.size(); j++) {
//					String string = String.valueOf(arrayList.get(j));
//					if (string.equals("*")) {
//						position = j;
//						break;
//					}
//				}
//
//				String result = "";
//				
//				String firstValue = String.valueOf(arrayList.get(position - 1));
//				String secondValue = String.valueOf(arrayList.get(position + 1));
//
//				firstValue = assignValue(firstValue, value);
//				secondValue = assignValue(secondValue, value);
//
//				double first = Double.parseDouble(firstValue.trim());
//				double second = Double.parseDouble(secondValue.trim());
//
//				result = String.valueOf(first * second);
//
//				ArrayList<Object> objects = new ArrayList<>();
//
//				for (int j = 0; j < position - 1; j++) {
//					objects.add(arrayList.get(j));
//				}
//
//				objects.add(result);
//
//				for (int j = position + 2; j < arrayList.size(); j++) {
//					objects.add(arrayList.get(j));
//				}
//
//				arrayList = objects;
//			}
//
//			for (int i = 0; i < div; i++) {
//				int position = -1;
//				for (int j = 0; j < arrayList.size(); j++) {
//					String string = String.valueOf(arrayList.get(j));
//					if (string.equals("/")) {
//						position = j;
//						break;
//					}
//				}
//
//				String result = "";
//				String firstValue = String.valueOf(arrayList.get(position - 1));
//				String secondValue = String.valueOf(arrayList.get(position + 1));
//
//				firstValue = assignValue(firstValue, value);
//				secondValue = assignValue(secondValue, value);
//				
//				double first = Double.parseDouble(firstValue.trim());
//				double second = Double.parseDouble(secondValue.trim());
//
//				result = String.valueOf(first / second);
//
//				ArrayList<Object> objects = new ArrayList<>();
//
//				for (int j = 0; j < position - 1; j++) {
//					objects.add(arrayList.get(j));
//				}
//
//				objects.add(result);
//
//				for (int j = position + 2; j < arrayList.size(); j++) {
//					objects.add(arrayList.get(j));
//				}
//
//				arrayList = objects;
//			}
//
//			for (int i = 0; i < add; i++) {
//				int position = -1;
//				for (int j = 0; j < arrayList.size(); j++) {
//					String string = String.valueOf(arrayList.get(j));
//					if (string.equals("+")) {
//						position = j;
//						break;
//					}
//				}
//
//				String result = "";
//				String firstValue = String.valueOf(arrayList.get(position - 1));
//				String secondValue = String.valueOf(arrayList.get(position + 1));
//
//				firstValue = assignValue(firstValue, value);
//				secondValue = assignValue(secondValue, value);
//
//				/*
//				 * double first = Double.parseDouble(firstValue.trim()); double second =
//				 * Double.parseDouble(secondValue.trim());
//				 */
//
//				double first = Double.parseDouble(firstValue.trim());
//				double second = Double.parseDouble(secondValue.trim());
//
//				result = String.valueOf(first + second);
//
//				ArrayList<Object> objects = new ArrayList<>();
//
//				for (int j = 0; j < position - 1; j++) {
//					objects.add(arrayList.get(j));
//				}
//
//				objects.add(result);
//
//				for (int j = position + 2; j < arrayList.size(); j++) {
//					objects.add(arrayList.get(j));
//				}
//
//				arrayList = objects;
//			}
//
//			for (int i = 0; i < sub; i++) {
//				int position = -1;
//				for (int j = 0; j < arrayList.size(); j++) {
//					String string = String.valueOf(arrayList.get(j));
//					if (string.equals("-")) {
//						position = j;
//						break;
//					}
//				}
//
//				String result = "";
//				String firstValue = String.valueOf(arrayList.get(position - 1));
//				String secondValue = String.valueOf(arrayList.get(position + 1));
//
//				firstValue = assignValue(firstValue, value);
//				secondValue = assignValue(secondValue, value);
//
//				double first = Double.parseDouble(firstValue.trim());
//				double second = Double.parseDouble(secondValue.trim());
//
//				result = String.valueOf(first - second);
//
//				ArrayList<Object> objects = new ArrayList<>();
//
//				for (int j = 0; j < position - 1; j++) {
//					objects.add(arrayList.get(j));
//				}
//
//				objects.add(result);
//
//				for (int j = position + 2; j < arrayList.size(); j++) {
//					objects.add(arrayList.get(j));
//				}
//
//				arrayList = objects;
//			}
//
//			if (arrayList.size() == 1) {
//				return arrayList.get(0);
//			} else {
//				return value;
//			}
//		}
//	}
//
//	private ArrayList<Object> decodeExpression(ArrayList<Object> arrayList,
//			Object value) {
//		// TODO Auto-generated method stub
//		
//		
//		
//		System.out.println("Decoding Expression");
//		System.out.println("Size: " + arrayList.size());
//		for (Object object : arrayList) {
//			System.out.println(object);
//		}
//		
//		if (arrayList.size() != 0) {
//			int startIndex = -1;
//			for (int i = arrayList.size() - 1; i > 0; i--) {
//				if (arrayList.get(i).equals("(")) {
//					startIndex = i - 1;
//					break;
//				}
//			}
//			
//			String result = "";
//
//			if (startIndex < 0) {
//				String expression = "";
//				for (int i = startIndex + 2; i < arrayList.size(); i++) {
//					expression = expression + arrayList.get(i);
//				}
//
//				Object object = handleExpression(expression, value);
//				
//				System.out.println("Object: " + object);
//				
//				double number = 0;
//				try {
//					number = Double.parseDouble(object.toString());
//				} catch (Exception e) {
//					// TODO: handle exception
//					System.out.println(e);
//					for (int i = arrayList.size() - 1; i > startIndex; i--) {
//						arrayList.remove(i);
//					}
//
//					arrayList.set(startIndex, object);
//					return arrayList;
//				}
//
//				for (int i = arrayList.size() - 1; i > startIndex; i--) {
//					arrayList.remove(i);
//				}
//
//				if (arrayList.size() > 0) {
//					arrayList.set(startIndex + 1, number);
//				} else {
//					arrayList.add(number);
//				}
//				return arrayList;
//			} else {
//				String operation = (String) arrayList.get(startIndex);
//
//				if (operation.toLowerCase().equals("CONCAT".toLowerCase())) {
//					
//					String first = (String) arrayList.get(startIndex + 2);
//					String second = (String) arrayList.get(startIndex + 4);
//
//					if (first.contains("http") || first.contains(":") || first.contains("www")) {
//						if (value instanceof LinkedHashMap) {
//							LinkedHashMap<String, String> linkedHashMap = (LinkedHashMap<String, String>) value;
//							for (Map.Entry<String, String> map : linkedHashMap.entrySet()) {
//			                    String property = map.getKey();
//			                    String propertyValue = map.getValue();
//
//			                    if (first.contains(property)) {
//			                    	result += propertyValue;
//			                    	break;
//								}
//			                }
//						} else {
//							result += value;
//						}
//					} else {
//						result += first;
//					} 
//					
//					if (second.contains("http") || second.contains(":") || first.contains("www")) {
//						if (value instanceof LinkedHashMap) {
//							LinkedHashMap<String, String> linkedHashMap = (LinkedHashMap<String, String>) value;
//							for (Map.Entry<String, String> map : linkedHashMap.entrySet()) {
//			                    String property = map.getKey();
//			                    String propertyValue = map.getValue();
//
//			                    if (second.contains(property)) {
//			                    	result += propertyValue;
//			                    	break;
//								}
//			                }
//						} else {
//							result += value;
//						}
//					} else {
//						result += second;
//					}
//				} else if (operation.toLowerCase().equals("Replace".toLowerCase())) {
//					String key = (String) arrayList.get(startIndex + 2);
//					String first = (String) arrayList.get(startIndex + 4);
//					String second = (String) arrayList.get(startIndex + 6);
//					
//					if (value instanceof LinkedHashMap) {
//						LinkedHashMap<String, String> linkedHashMap = (LinkedHashMap<String, String>) value;
//						for (Map.Entry<String, String> map : linkedHashMap.entrySet()) {
//		                    String property = map.getKey();
//		                    String propertyValue = map.getValue();
//
//		                    if (key.contains(property)) {
//		                    	result = String.valueOf(propertyValue).replaceAll(first, second);
//		                    	break;
//							}
//		                }
//					} else {
//						result = String.valueOf(value).replaceAll(first, second);
//					}
//
//				} else if (operation.toLowerCase().equals("split".toLowerCase())) {
//					for (int i = 0; i < arrayList.size(); i++) {
//						System.out.println("Index " + i + " = " + arrayList.get(i));
//					}
//					
//					String key = (String) arrayList.get(startIndex + 2);
//					String first = (String) arrayList.get(startIndex + 4);
//
//					String[] parts = new String[] {};
//					int position = 6;
//					
//					if (value instanceof LinkedHashMap) {
//						LinkedHashMap<String, String> linkedHashMap = (LinkedHashMap<String, String>) value;
//						for (Map.Entry<String, String> map : linkedHashMap.entrySet()) {
//		                    String property = map.getKey();
//		                    String propertyValue = map.getValue();
//
//		                    if (key.contains(property)) {
//		                    	value = propertyValue;
//		                    	break;
//							}
//		                }
//					}
//					
//					if (!first.equals(",")) {
//						parts = String.valueOf(value).split(first);
//					} else {
//						position = 5;
//						parts = String.valueOf(value).split("\\s+");
//					}
//
//					for (int i = position; i < arrayList.size(); i += 2) {
//						String string = String.valueOf(arrayList.get(i)).trim();
//
//						try {
//							int index = Integer.parseInt(string);
//							result = result + parts[index];
//						} catch (Exception e) {
//							// TODO: handle exception
//							return arrayList;
//						}
//					}
//				} else if (operation.toLowerCase().equals("ToNumber".toLowerCase())) {
//					String expression = "";
//					for (int i = startIndex + 2; i < arrayList.size(); i++) {
//						expression = expression + arrayList.get(i);
//					}
//
//					Object object = handleExpression(expression, value);
//					int number = 0;
//					try {
//						result = String.valueOf(object);
//						Pattern pattern = Pattern.compile("[0-9]+");
//
//						Matcher matcher = pattern.matcher(result);
//						if (matcher.find()) {
//							number = Integer.parseInt(matcher.group(0));
//						}
//					} catch (Exception e) {
//						// TODO: handle exception
//						System.out.println(e);
//						for (int i = arrayList.size() - 1; i > startIndex; i--) {
//							arrayList.remove(i);
//						}
//
//						arrayList.set(startIndex, value);
//						return arrayList;
//					}
//
//					for (int i = arrayList.size() - 1; i > startIndex; i--) {
//						arrayList.remove(i);
//					}
//
//					arrayList.set(startIndex, number);
//					return arrayList;
//				} else if (operation.toLowerCase().equals("ToString".toLowerCase())) {
//					String expression = "";
//					for (int i = startIndex + 2; i < arrayList.size(); i++) {
//						expression = expression + arrayList.get(i);
//					}
//
//					Object object = handleExpression(expression, value);
//					result = String.valueOf(object);
//				}
//			}
//
//			for (int i = arrayList.size() - 1; i > startIndex; i--) {
//				arrayList.remove(i);
//			}
//
//			arrayList.set(startIndex, result);
//		}
//		
//		System.out.println("We are here");
//		for (Object object : arrayList) {
//			System.out.println(object);
//		}
//
//		return arrayList;
//	}
//	
//	public String assignValue(String firstValue, Object value) {
//		if (firstValue.contains("http") || firstValue.contains(":")) {
//			if (value instanceof LinkedHashMap) {
//				LinkedHashMap<String, Object> valueMap = (LinkedHashMap<String, Object>) value;
//				
//				if (valueMap.containsKey(firstValue)) {
//					Object firstObjectValue = valueMap.get(firstValue);
//					firstValue = firstObjectValue.toString();
//				} else {
//					System.out.println("Value is not in the map");
//					firstValue = "";
//				}
//			} else {
//				firstValue = value.toString();
//			}
//		}
//		
//		return firstValue;
//	}
//	
//	public void checkCharacter(ArrayList<Object> arrayList, String word, String character) {
//		boolean status = false;
//		for (Object object : arrayList) {
//			if (String.valueOf(object).toLowerCase().equals("split".toLowerCase())) {
//				status = true;
//			}
//		}
//
//		if (!status) {
//			if (!word.equals("")) {
//				arrayList.add(word);
//			}
//
//			arrayList.add(character);
//			word = "";
//		} else {
//			word = word + character;
//		}
//	}
}
