package edu.school21.app;

import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;

import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Lexer lexer = Lexer.getInstance();
    private static final Parser parser = Parser.getInstance();

    public static void main(String[] args) {
        while (scanner.hasNext()) {
            String form = scanner.nextLine().trim();

            if (form.equalsIgnoreCase("exit")) {
                System.exit(0);
            }

            try {
                lexer.processing(form);
                parser.processing(lexer.getTokens());
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
