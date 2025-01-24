package com.jankinwu.wordsstatistics.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 皖刚
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

    public static String processTextContent(String textContent) {
        try (BufferedReader reader = new BufferedReader(new StringReader(textContent))) {
            List<String> lines = reader.lines().toList();
            return lines.stream()
                    .map(String::trim) // 去除每行开头和结尾的空格
                    .map(line -> line.replaceAll("_+", " ")) // 将连续下划线替换为空格
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
            return "Error processing content";
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
}
