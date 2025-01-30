package com.jankinwu.wordsstatistics.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Jankin Wu
 * @description 文件处理工具
 * @date 2025-01-24 17:31
 **/
public class FileUtil {

    public static String processTextFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            return lines.stream()
                    .map(String::trim) // 去除每行开头和结尾的空格
                    .filter(line -> !line.isEmpty()) // 去除空行
                    // 去除以数字开头的行（题目序号）
                    .filter(line -> !line.matches("^[0-9]+\\..*"))
                    // 去除包含中文字符的行
                    .filter(line -> !line.matches(".*[\u4e00-\u9fa5]+.*"))
                    // 去除包含特定中文短语的行
                    .filter(line -> !line.contains("考生注意"))
                    // 去除章节标题和说明性文字
                    .filter(line -> !line.matches("^(Section [A-Z]|Directions:|Questions \\d+ through \\d+|[IVXLCDM]+\\. .+).*"))
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing file";
        }
    }

    public static String readTextFile(String filePath) {
        try {
            // 读取整个文件为一个字符串
            return Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading file";
        }
    }

    public static void getWordsFromText(String filePath) {
        StringBuilder content = new StringBuilder();

        // Step 1: Read the file content
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 2: Use regex to extract English words
        String regex = "\\b[a-zA-Z]+\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        // Step 3: Store the extracted words in a list
        List<String> words = new ArrayList<>();
        while (matcher.find()) {
            words.add(matcher.group());
        }

        // Print the extracted words
        for (String word : words) {
            System.out.println(word);
        }
    }

    public static List<String> extractEnglishWords(String filePath) {
        List<String> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder word = new StringBuilder();
            int currentChar;

            while ((currentChar = br.read()) != -1) {
                if (Character.isLetter((char) currentChar)) {
                    word.append((char) currentChar);
                } else {
                    if (word.length() > 0) {
                        words.add(word.toString());
                        word.setLength(0); // reset the word
                    }
                }
            }

            // Add the last word if exists
            if (word.length() > 0) {
                words.add(word.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return words;
    }

    public static String getStringFromTxt(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void main(String[] args) {
        getWordsFromText("D:\\Documents\\其他\\test\\[OCR]_四级高频词汇（33页）_20250129_1359.txt");
//        List<String> words = extractEnglishWords("D:\\Documents\\其他\\test\\[OCR]_四级高频词汇（33页）_20250129_1359.txt");
//        for (String word : words) {
//            System.out.println(word);
//        }
    }
}
