package edu.school21.data;

import edu.school21.exceptions.FunctionNotFoundException;
import edu.school21.exceptions.VariableNotFoundException;
import edu.school21.tokens.Function;
import edu.school21.tokens.Token;
import edu.school21.tokens.Variable;
import edu.school21.types.Type;

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

    public void updateToken(Token token) {
        if (token.getType() == Type.FUNCTION) {
            Function func = getFunction(((Function)token).getName());

            if (func != null) {
                functions.remove(func);
            }
            addFunction((Function)token);
        }

        if (token.getType() == Type.VARIABLE) {
            Variable var = getVariable(token.getToken());

            if (var != null) {
                variables.remove(var);
            }
            addVariable((Variable)token);
        }
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

    public Variable getVariable(String form) {
        for (Variable variable : this.variables) {
            if (variable.getToken().equalsIgnoreCase(form)) {
                return variable;
            }
        }
        return null;
    }

    public Function getFunction(String form) {
        for (Function function : this.functions) {
            if (function.getName().equalsIgnoreCase(form)) {
                return function;
            }
        }
        return null;
    }
}
