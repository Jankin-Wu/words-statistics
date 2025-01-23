package com.jankinwu.wordsstatistics.utils;

import java.util.Map;
import java.util.Set;

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
        String filteredText = input.replaceAll("(?i)\\b[A-D]\\.", "").trim();

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
}
