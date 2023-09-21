package utils;

public class EvaluateInequality {
    public static boolean evaluateInequality(String inequality) {
        // Split the inequality string into operands and operator.
        String[] tokens = inequality.split(" ");
        String operand1 = tokens[0];
        String operator = tokens[1];
        String operand2 = tokens[2];

        // Convert the operands to the appropriate type.
        double operand1Double = Double.parseDouble(operand1);
        double operand2Double = Double.parseDouble(operand2);

        // Evaluate the inequality based on the operator.
        return switch (operator) {
            case ">" -> operand1Double > operand2Double;
            case ">=" -> operand1Double >= operand2Double;
            case "<" -> operand1Double < operand2Double;
            case "<=" -> operand1Double <= operand2Double;
            case "==" -> operand1Double == operand2Double;
            case "!=" -> operand1Double != operand2Double;
            default -> throw new IllegalArgumentException("Invalid inequality operator: " + operator);
        };
    }
}
