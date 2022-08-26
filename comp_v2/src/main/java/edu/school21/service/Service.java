package edu.school21.service;

import edu.school21.actions.Assignment;
import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;
import edu.school21.data.Data;
import edu.school21.tokens.Printable;

public class Service {
    private static final Service service = new Service();
    private final Lexer lexer = Lexer.getInstance();
    private final Parser parser = Parser.getInstance();
    private final Data data = Data.getInstance();
    private final Assignment assignment = Assignment.getInstance();

    private Service() {}

    public static Service getInstance() {
        return service;
    }

    public void perform(String form) {
        lexer.processing(form);
        parser.processing(lexer.getTokens());
        assignment.assign(parser.getLeft(), parser.getRight());
        print(assignment.getToken());
    }

    public void print(Printable token) {
        System.out.println(token.getValueToString());
    }
}
