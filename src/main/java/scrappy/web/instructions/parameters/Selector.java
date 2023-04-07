package scrappy.web.instructions.parameters;

public class Selector {
    private String description;
    private String selector;

    public Selector(String description, String selector) {
        this.description = description;
        this.selector = selector;
    }

    public String getDescription() {
        return description;
    }

    public String getSelector() {
        return selector;
    }
}
