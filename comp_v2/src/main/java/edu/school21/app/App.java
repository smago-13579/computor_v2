package edu.school21.app;

import edu.school21.service.Service;
import org.apache.maven.shared.utils.StringUtils;

import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Service service = Service.getInstance();

    public static void main(String[] args) {
        while (scanner.hasNext()) {
            String form = scanner.nextLine().trim();

            if (StringUtils.isEmpty(form)) {
                continue;
            }

            if (form.equalsIgnoreCase("exit")) {
                System.exit(0);
            }

            try {
                service.perform(form);
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
