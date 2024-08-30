package io.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Main {

    private static final Set<String> RESERVED_WORDS = new HashSet<>(Arrays.asList(
            "int", "float", "if", "else", "while", "return", "void", "for", "break", "continue", "char", "double"
    ));

    private static final Set<String> OPERATORS = new HashSet<>(Arrays.asList(
            "+", "-", "*", "/", "=", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "!", "++", "--"
    ));

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        System.out.print("Ingresa la ruta del archivo .txt: ");
        var filePath = scanner.nextLine();

        try (var reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            var lineNumber = 1;
            var insideBlockComment = false;

            System.out.println(" ");
            System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s%n", "Cadena", "Identificador", "Operador", "Constante", "Reservada", "Línea");
            System.out.println("--------------------------------------------------------------------------------------");

            while ((line = reader.readLine()) != null) {
                // Eliminar comentarios de bloque
                if (insideBlockComment) {
                    if (line.contains("*/")) {
                        insideBlockComment = false;
                        line = line.substring(line.indexOf("*/") + 2);
                    } else {
                        lineNumber++;
                        continue;
                    }
                }

                // Eliminar comentarios de línea y posibles comentarios de bloque
                if (line.contains("//")) {
                    line = line.substring(0, line.indexOf("//"));
                }

                if (line.contains("/*")) {
                    insideBlockComment = true;
                    line = line.substring(0, line.indexOf("/*"));
                }

                // Tokenizar y clasificar
                tokenizeAndClassify(line, lineNumber);

                lineNumber++;
            }

            System.out.println(" ");

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private static void tokenizeAndClassify(String line, int lineNumber) {
        // Expresión regular para tokenizar considerando operadores, delimitadores, etc.
        var regex = "[a-zA-Z_][a-zA-Z0-9_]*|\\d+|==|!=|<=|>=|&&|\\|\\||\\+\\+|--|[+\\-*/=<>!]|[{}();,]";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(line);

        while (matcher.find()) {
            var token = matcher.group();

            if (RESERVED_WORDS.contains(token)) {
                printRow(token, "No", "No", "No","Sí", lineNumber);
            } else if (OPERATORS.contains(token)) {
                printRow(token, "No", "Sí", "No", "No", lineNumber);
            } else if (token.matches("\\d+")) {
                printRow(token, "No", "No", "Sí", "No", lineNumber);
            } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                printRow(token, "Sí", "No", "No", "No", lineNumber);
            } else {
                printRow(token, "No", "No", "No", "No", lineNumber);
            }
        }
    }

    private static void printRow(String token, String isIdentifier, String isOperator, String isConstant, String isReserved, int lineNumber) {
        System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s%n", token, isIdentifier, isOperator, isConstant, isReserved, lineNumber);
    }

}



