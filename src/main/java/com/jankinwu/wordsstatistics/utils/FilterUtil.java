package com.jankinwu.wordsstatistics.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 过滤器工具类
 * @author: Jankin Wu
 * @date: 2025-01-24 00:01
 **/
public class FilterUtil {

    /**
     * 过滤文本中类似 A. B. C. D. 的标记，并去掉系动词（如 is, am, are, was, were, isn't 等）
     *
     * @param input 原始文本
     * @return 过滤后的文本
     */
    public static String filterDirtyData(String input) {
        if (input == null || input.isEmpty()) {
            return input; // 如果输入为空，直接返回
        }

        // 1. 移除 "A.", "B.", "C.", "D." 等标记
        String filteredText = input.replaceAll("(?i)\\b[A-Z]\\.", "").trim();

        // 2. 移除系动词（is, am, are, was, were, isn't, aren't, etc.）
        filteredText = filteredText.replaceAll("\\b(is|am|are|was|were|isn't|aren't|wasn't|weren't|be|being|been|isn’t|aren’t|wasn’t|weren’t|won’t)\\b", "").trim();

        // 3. 移除数字（包括整数和小数）
        filteredText = filteredText.replaceAll("\\b\\d+(\\.\\d+)?\\b", "").trim();

        return filteredText;
    }

    /**
     * 过滤掉单词频率映射中特定的Key
     *
     * @param map 单词频率的Map
     */
    public static void filterSpecificKeys(Map<String, Integer> map) {
        Set<String> keysToRemove = Set.of("n’t", "'s");
        keysToRemove.forEach(map::remove);
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
}
