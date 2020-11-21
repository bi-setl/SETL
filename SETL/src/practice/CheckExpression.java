package practice;

public class CheckExpression {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String expression = "bont:hasNumericalValue * 1000 / exiobase:hasNumericalValue";
		
		for (int i = 0; i < expression.length(); i++) {
			String character = Character.toString(expression.charAt(i));
			System.out.println(character);
			
			if (character.equals("/")) {
				System.out.println("Found");
			}
		}
	}

}
