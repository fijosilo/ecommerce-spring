package com.fijosilo.ecommerce;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyValueMap {

    /**
     * Converts a string eg: {key=value, otherKey=otherValue} to a HashMap<String, String>
     *
     * @param expression A string containing the keys and values eg: {key=value, otherKey=otherValue}.
     * @return A HashMap<String, String>.
     */
    public static HashMap<String, String> fromString(String expression) {
        // remove { and }
        expression = expression.charAt(0) == '{' && expression.charAt(expression.length()-1) == '}' ?
                expression.substring(1, expression.length()-1) : expression;
        // create the return hashmap
        HashMap<String, String> response = new HashMap<>();
        // find keys/values
        Matcher matcher = Pattern.compile("(?!,)([^=]*)=([^,]*)").matcher(expression);
        while (matcher.find()) {
            // add key/value to the response hashmap
            String key = matcher.group(1).strip();
            String value = matcher.group(2).strip();
            response.put(key, value);
        }
        // return the response hashmap
        return response;
    }

}
