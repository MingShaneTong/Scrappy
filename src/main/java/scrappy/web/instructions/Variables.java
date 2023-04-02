package scrappy.web.instructions;

import java.util.HashMap;
import java.util.Map;

public class Variables {
    public Map<String, String> variables;

    public Variables() {
        variables = new HashMap<String, String>();
    }

    public void put(String var, String value) {
        variables.put(var, value);
    }

    public String get(String var) {
        return variables.get(var);
    }

    public static Variables of(Variables variables) {
        Variables var = new Variables();
        var.variables = variables.variables;
        return var;
    }
}
