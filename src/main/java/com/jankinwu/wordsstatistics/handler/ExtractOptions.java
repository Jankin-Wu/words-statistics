package com.jankinwu.wordsstatistics.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @description:
 * @author: Jankin Wu
 * @date: 2025-01-24 22:17
 **/
public class ExtractOptions {
    public static void main(String[] args) {
        String directoryPath = "D:\\Documents\\个人项目\\wordStatistics\\模拟试卷TXT"; // 请将此路径替换为你的目标目录路径
        extractOptionsFromDirectory(directoryPath);
    }

    public static void extractOptionsFromDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        // Check if the directory exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            // List all files in the directory
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

            if (files != null) {
                for (File file : files) {
                    System.out.println("Extracting from file: " + file.getName());
                    extractOptionsFromFile(file);
                }
            }
        } else {
            System.err.println("The specified path is not a directory or does not exist.");
        }
    }

    public static void extractOptionsFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isGrammarSection = false;
            boolean isSectionB = false;

            while ((line = br.readLine()) != null) {
                // Check for the start of II. Grammar and Vocabulary
                if (line.contains("II. Grammar and Vocabulary")) {
                    isGrammarSection = true;
                }

                // Check for the start of Section B under Grammar and Vocabulary
                if (isGrammarSection && line.contains("Section B")) {
                    isSectionB = true;
                }

                // If we are in Section B under Grammar and Vocabulary, look for options A to K
                if (isSectionB) {
                    if (line.matches(".*\\b[A-K]\\.\\s.*")) {
                        System.out.println(line.trim());
                    }

                    // Stop reading further once we reach the end of Section B
                    if (line.contains("Section A") || line.contains("III. Reading Comprehension")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + file.getName() + " - " + e.getMessage());
        }
    }
}
