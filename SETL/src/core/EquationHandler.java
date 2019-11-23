package core;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EquationHandler {
	
	public Object handleExpression(String expressionString, LinkedHashMap<String, Object> valueHashMap) {
		// TODO Auto-generated method stub
		
		boolean containsKey = checkKey(expressionString);
		if (containsKey) {
			int keyWordPosition = expressionString.indexOf("(");
			String keyWordString = expressionString.substring(0, keyWordPosition);
			
			String newExpressionString = expressionString.substring(keyWordPosition + 1, expressionString.length() - 1);
			
			return handleExpression(keyWordString, newExpressionString.trim(), valueHashMap);
		} else {
			boolean containsSigns = checkSigns(expressionString);

			if (containsSigns) {
				int add = 0, sub = 0, mul = 0, div = 0;
				ArrayList<Object> arrayList = new ArrayList<Object>();

				String characterString = "";
				for (int i = 0; i < expressionString.length(); i++) {
					String character = Character.toString(expressionString.charAt(i));

					if (checkSigns(character)) {
						arrayList.add(characterString);
						arrayList.add(character);
						characterString = "";

						switch (character) {
						case "+":
							add++;
							break;

						case "-":
							sub++;
							break;

						case "*":
							mul++;
							break;

						case "/":
							div++;
							break;

						default:
							break;
						}

					} else {
						characterString += character;
					}
				}
				
				if (characterString.length() != 0) {
					arrayList.add(characterString);
				}
				
				arrayList = solveExpression(arrayList, valueHashMap, "/", div);
				arrayList = solveExpression(arrayList, valueHashMap, "*", mul);
				arrayList = solveExpression(arrayList, valueHashMap, "-", sub);
				arrayList = solveExpression(arrayList, valueHashMap, "+", add);
				
				if (arrayList.size() == 1) {
					return arrayList.get(0);
				}
			} else {
				double value = 0.0;
				
				if (valueHashMap.containsKey(expressionString.trim())) {
					
					try {
						value = Double.parseDouble(valueHashMap.get(expressionString).toString().trim());
						// System.out.println(value);
						
						if (value % 1 == 0) {
							return Math.round(value);
						} else {
							return value;
						}
					} catch (Exception e) {
						// TODO: handle exception
						// System.out.println("it is a string " + expressionString.toString().trim());
						return valueHashMap.get(expressionString).toString().trim();
					}
				} else {
					try {
						value = Double.parseDouble(expressionString.toString().trim());
						// System.out.println(value);
						if (value % 1 == 0) {
							return Math.round(value);
						} else {
							return value;
						}
					} catch (Exception e) {
						// TODO: handle exception
						return expressionString.trim();
					}
				}
			}
		}
		
		return null;
	}

	private Object handleExpression(String keyWordString, String expressionString,
			LinkedHashMap<String, Object> valueHashMap) {
		// TODO Auto-generated method stub
		
		if (keyWordString.toLowerCase().trim().equals("concat")) {
			int commaPosition = getCommaPosition(expressionString, 0);
			
			
			String firstPartString = expressionString.substring(0, commaPosition);
			String secondPartString = expressionString.substring(commaPosition + 1, expressionString.length());
			
			// System.out.println("First Part " + firstPartString);
			// System.out.println("Second Part " + secondPartString);
			
			Object firstObject = handleExpression(firstPartString.trim(), valueHashMap);
			Object secondObject = handleExpression(secondPartString.trim(), valueHashMap);
			
			// System.out.println("First " + firstObject);
			// System.out.println("Second " + secondObject);
			
			return firstObject + "" + secondObject;
		} else if (keyWordString.toLowerCase().trim().equals("replace")) {
			int firstCommaPosition = getCommaPosition(expressionString, 0);
			int secondCommaPosition = getCommaPosition(expressionString, firstCommaPosition + 1);
			
			
			String firstPartString = expressionString.substring(0, firstCommaPosition);
			String secondPartString = expressionString.substring(firstCommaPosition + 1, secondCommaPosition);
			String thirdPartString = expressionString.substring(secondCommaPosition + 1, expressionString.length());
			
			Object firstObject = handleExpression(firstPartString.trim(), valueHashMap);
			Object secondObject = handleExpression(secondPartString.trim(), valueHashMap);
			Object thirdObject = handleExpression(thirdPartString.trim(), valueHashMap);
			
			return firstObject.toString().replace(secondObject.toString(), thirdObject.toString());
		} else if (keyWordString.toLowerCase().trim().equals("split")) {
			int firstCommaPosition = getCommaPosition(expressionString, 0);
			int secondCommaPosition = getCommaPosition(expressionString, firstCommaPosition + 1);
			
			
			String firstPartString = expressionString.substring(0, firstCommaPosition);
			String secondPartString = expressionString.substring(firstCommaPosition + 1, secondCommaPosition);
			String thirdPartString = expressionString.substring(secondCommaPosition + 1, expressionString.length());
			
			Object firstObject = handleExpression(firstPartString.trim(), valueHashMap);
			Object secondObject = handleExpression(secondPartString.trim(), valueHashMap);
			Object thirdObject = handleExpression(thirdPartString.trim(), valueHashMap);
			
			String[] partStrings = firstObject.toString().split(secondObject.toString());
			int position = (int) Math.round((double) thirdObject);
			
			if (position < partStrings.length) {
				return partStrings[position];
			} else {
				return firstObject.toString();
			}
		} else if (keyWordString.toLowerCase().trim().equals("tonumber")) {
			Object firstObject = handleExpression(expressionString, valueHashMap);
			
			try {
				double number = Double.parseDouble(firstObject.toString());
				return number;
			} catch (Exception e) {
				// TODO: handle exception
				return firstObject;
			}
		} else if (keyWordString.toLowerCase().trim().equals("tostring")) {
			Object firstObject = handleExpression(expressionString, valueHashMap);
			return firstObject.toString();
		}
			
		
		return null;
	}

	private int getCommaPosition(String expressionString, int index) {
		// TODO Auto-generated method stub
		if (expressionString.contains(",")) {
			int commaPosition = expressionString.indexOf(",", index);
			String firstPartString = expressionString.substring(0, commaPosition);
			
			if (firstPartString.contains("(")) {
				int firstBracketNumber = 0, secondBracketNumber = 0;
				for (int i = 0; i < firstPartString.length(); i++) {
					String character = Character.toString(expressionString.charAt(i));
					if (character.equals("(")) {
						firstBracketNumber++;
					} else if (character.equals(")")) {
						secondBracketNumber++;
					}
				}
				
				if (firstBracketNumber == secondBracketNumber) {
					return commaPosition;
				} else {
					return getCommaPosition(expressionString, commaPosition + 1);
				}
			} else {
				return commaPosition;
			}
		} else {
			return 0;
		}
	}

	private boolean checkKey(String expressionString) {
		// TODO Auto-generated method stub
		if (expressionString.toLowerCase().startsWith("concat") || expressionString.toLowerCase().startsWith("replace") ||
			expressionString.toLowerCase().startsWith("split") || expressionString.toLowerCase().startsWith("tonumber") || 
			expressionString.toLowerCase().startsWith("tostring") || expressionString.toLowerCase().startsWith("compare")) {
			return true;
		}
		return false;
	}

	private ArrayList<Object> solveExpression(ArrayList<Object> arrayList, LinkedHashMap<String, Object> valueHashMap,
			String characterString, int limit) {
		// TODO Auto-generated method stub
		// System.out.println("solve expression");
		// System.out.println(characterString);
		
		for (int i = 0; i < limit; i++) {
			int position = arrayList.indexOf(characterString);
			String firstElementString = arrayList.get(position - 1).toString().trim();
			String secondElementString = arrayList.get(position + 1).toString().trim();
			
			double first = 0.0;
			double second = 0.0;
			double result = 0.0;

			if (valueHashMap.containsKey(firstElementString)) {
				first = Double.parseDouble(valueHashMap.get(firstElementString).toString());
			} else {
				first = Double.parseDouble(firstElementString.toString());
			}

			if (valueHashMap.containsKey(secondElementString)) {
				second = Double.parseDouble(valueHashMap.get(secondElementString).toString());
			} else {
				second = Double.parseDouble(secondElementString.toString());
			}
			
			if (characterString.equals("/")) {
				if (second != 0.0) {
					result = first / second;
				}
			} else if (characterString.equals("*")) {
				result = first * second;
			} else if (characterString.equals("+")) {
				result = first + second;
			} else if (characterString.equals("-")) {
				result = first - second;
			}
			
			arrayList.set(position - 1, result);
			arrayList.remove(position);
			arrayList.remove(position);
		}
		
		return arrayList;
	}

	private boolean checkSigns(String expressionString) {

		// TODO Auto-generated method stub
		if (expressionString.contains("+") || expressionString.contains("-") || expressionString.contains("*")
				|| expressionString.contains("/")) {
			return true;
		}
		return false;
	}
}
