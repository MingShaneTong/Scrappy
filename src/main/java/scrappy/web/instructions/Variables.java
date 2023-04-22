package scrappy.web.instructions;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores variables within a scope
 */
public class Variables {
    /**
     * Map of variables from name to value
     */
    public Map<String, String> variables;

    /**
     * Creates a new set of variables
     */
    public Variables() {
        variables = new HashMap<>();
    }

    /**
     * Adds a new variable to scope
     * @param var variable name
     * @param value variable value
     */
    public void put(String var, String value) {
        variables.put(var, value);
    }

    /**
     * Returns a variable value
     * @param var variable name to search
     * @return variable value
     */
    public String get(String var) {
        return variables.get(var);
    }

    /**
     * Creates a duplicate set of variables
     * @param variables variables to copy
     * @return new set of variables
     */
    public static Variables of(Variables variables) {
        Variables var = new Variables();
        var.variables = new HashMap<>(variables.variables);
        return var;
    }
}
