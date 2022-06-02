package edu.school21.data;

import edu.school21.tokens.Function;
import edu.school21.tokens.Variable;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private static final Data data = new Data();
    private final List<Variable> variables = new ArrayList<>();
    private final List<Function> functions = new ArrayList<>();
    private final List<String> history = new ArrayList<>();

    private Data() {}

    public static Data getInstance() {
        return data;
    }

    public void addVariable(Variable variable) {
        this.variables.add(variable);
    }

    public void addFunction(Function function) {
        this.functions.add(function);
    }

    public void addHistory(String histrory) {
        this.history.add(histrory);
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public List<String> getHistory() {
        return history;
    }
}
