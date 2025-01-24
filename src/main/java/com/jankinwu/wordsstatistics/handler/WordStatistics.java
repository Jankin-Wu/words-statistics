package com.jankinwu.wordsstatistics.handler;

import com.jankinwu.wordsstatistics.service.NlpService;
import com.jankinwu.wordsstatistics.utils.FileUtil;
import com.jankinwu.wordsstatistics.utils.FilterUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 统计单词
 * @author: Jankin Wu
 * @date: 2025-01-23 23:14
 **/
@RequiredArgsConstructor
@Service
public class WordStatistics {

    private final NlpService nlpService;

//    @PostConstruct
    public void StatisticalWordPartsOfSpeech() {
        // 输入的文本
        String text = """
            A. Customer and waitress.              B. Guests and hostess.
            C. Husband and wife.                  D. Boss and employee.
            2. A. Watch the program on TV.            B. Meet the man at the cat exhibition.
            C. Call the TV station.                 D. Look for cats at the man.
            3. A. Borrow the typewriter.              B. Visit the woman.
            C. Go home soon.                     D. Read the woman’s paper.
            4. A. The man.                          B. Both.
            C. The woman.                       D. Neither.
            5. A. The books there are too expansive.
            B. She won’t be able to get the book before the class.
            C. The textbook she need isn’t in yet.
            D. She hopes to get a good deal on some second-hand book.
            6. A. Take the bus to the airport.           B. Meet the Browns at the airport.
            C. Make a phone call to the Browns.      D. Accompany the Browns to the airport.
            7. A. The man will have a test
            B. The man will probably go to the movie.
            C. The man will have to sit for a exam.
            D. The woman wishes she could go to the class with the man.
        """;
        String filteredText = FilterUtil.filterDirtyData(text);
        Map<String, Object> analysisResult = nlpService.analyzeWordPartsOfSpeech(filteredText);
       // 打印分析结果
        System.out.println("\n词性比例分析结果:");
        // 打印分析结果
        System.out.println("\n词性比例分析结果:");
        analysisResult.forEach((key, value) -> {
            Map<String, Object> details = (Map<String, Object>) value;
            double count = (double) details.get("count");
            List<String> words = (List<String>) details.get("words");
            System.out.printf("%s: %.2f%%, 单词: %s\n", key, count * 100, words);
        });
    }

    @PostConstruct
    public void countWordFrequency() {
        // 输入的文本
        String text = FileUtil.readTextFile("D:\\Documents\\其他\\test\\上海市宝山区2017届高三上学期教学质量检测（一模）英语试题及答案（word版）.txt");
        // 过滤掉脏数据
        String filteredText = FilterUtil.filterDirtyData(text);
        // 统计单词频率
        Map<String, Integer> wordFrequency = nlpService.countSpecificWordFrequency(filteredText);

        // 过滤掉单词频率映射中特定的Key
        FilterUtil.filterSpecificKeys(wordFrequency);

        // 对单词频率降序排序
        List<Map.Entry<String, Integer>> sortedWordFrequency = sortWordFrequency(wordFrequency);

        // 打印排序后的单词频率
        System.out.println("\n名词、动词、形容词、副词的单词频率统计（按频率降序排列）:");
        sortedWordFrequency.forEach(entry -> System.out.printf("%s: %d\n", entry.getKey(), entry.getValue()));
        File txtFile = new File("D:\\Documents\\其他\\test\\", "统计结果.txt");
        try {
            // 使用 FileOutputStream 写入文件
            try (FileOutputStream fos = new FileOutputStream(txtFile)) {
                // 使用 StringBuilder 构建文件内容
                StringBuilder content = new StringBuilder();
                content.append("名词、动词、形容词、副词的单词频率统计（按频率降序排列）:\n");
                sortedWordFrequency.forEach(entry -> content.append(String.format("%s: %d\n", entry.getKey(), entry.getValue())));
                // 写入文件
                fos.write(content.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按单词出现频率降序排列
     *
     * @param wordFrequency 单词频率的Map
     * @return 排序后的单词频率列表
     */
    public List<Map.Entry<String, Integer>> sortWordFrequency(Map<String, Integer> wordFrequency) {
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(wordFrequency.entrySet());
        // 按频率降序排序
        sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        return sortedList;
    }


}
