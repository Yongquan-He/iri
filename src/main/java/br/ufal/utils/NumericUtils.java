package br.ufal.utils;

import java.util.List;

public class NumericUtils {

    public Double textToNumeric(String value) {
        //Double result = 0d;
        StringBuffer result = new StringBuffer(10);
        result.append(0);

        if (value != null) {

            char[] chars = value.toCharArray();

            for (char c : chars) {

                int convCharToInt;// convert from character to integer

                if (Character.isLetter(c)) { // is letter
                    convCharToInt = Character.toLowerCase(c) - 'a' + 1;
                } else if (Character.isDigit(c)) { //is integer number
                    convCharToInt = (int) c;
                } else { //is punctuation or symbol
                    convCharToInt = Character.getNumericValue(c);
                }

                if (convCharToInt < 0) { //ASCII does not supported
                    continue;
                }

                result.append(convCharToInt);
            }
        }

        return Double.parseDouble(result.toString());
    }

    public double[] textToNumeric(String[] values) {
        double[] numbers = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            Double number = textToNumeric(values[i]);
            numbers[i] = number;
        }

        return numbers;
    }

    public double[] textToNumeric(List<String> values) {
        return textToNumeric(values.toArray(new String[]{}));
    }
}
