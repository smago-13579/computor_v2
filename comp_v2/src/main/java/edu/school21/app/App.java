package edu.school21.app;

import edu.school21.data.Data;
import edu.school21.service.Service;
import org.apache.maven.shared.utils.StringUtils;

import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Service service = Service.getInstance();
    private static final Data data = Data.getInstance();

    public static void main(String[] args) {
        while (scanner.hasNext()) {
            String form = scanner.nextLine().trim();

            if (StringUtils.isEmpty(form)) {
                continue;
            }

            if (form.equalsIgnoreCase("exit")) {
                System.exit(0);
            }

            if (form.equalsIgnoreCase("history")) {
                data.printHistory();
                continue;
            }

            if (form.equalsIgnoreCase("variables")) {
                data.printVariables();
                continue;
            }

            if (form.equalsIgnoreCase("functions")) {
                data.printFunctions();
                continue;
            }

            try {
                service.perform(form);
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                data.addHistory(e.getMessage());
            }
        }
    }
}
