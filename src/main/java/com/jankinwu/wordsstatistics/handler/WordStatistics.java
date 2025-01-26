package com.jankinwu.wordsstatistics.handler;

import com.jankinwu.wordsstatistics.dto.WordFrequencyDTO;
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
import java.util.HashMap;
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
        String text = FileUtil.readTextFile("D:\\Documents\\个人项目\\wordStatistics\\小猫钓鱼.txt");
        // 过滤掉脏数据
        String filteredText = FilterUtil.filterDirtyData(text);
        // 统计单词频率，并返回包含单词、词性、频率的列表
        List<WordFrequencyDTO> wordFrequencies = nlpService.countWordFrequency(filteredText);

        // 将列表转换为 Map<String, Integer> 以过滤特定的键
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        wordFrequencies.forEach(wf -> wordFrequencyMap.put(wf.getWord(), wf.getFrequency()));

        // 过滤掉单词频率映射中特定的Key
        FilterUtil.filterSpecificKeys(wordFrequencyMap);

        // 排序WordFrequency列表
        List<WordFrequencyDTO> sortedWordFrequencies = wordFrequencies.stream()
                .filter(wf -> wordFrequencyMap.containsKey(wf.getWord())) // 过滤被移除的单词
                .sorted((wf1, wf2) -> Integer.compare(wf2.getFrequency(), wf1.getFrequency()))
                .toList();

        // 打印排序后的单词频率
        System.out.println("\n名词、动词、形容词、副词的单词频率统计（按频率降序排列）:");
        sortedWordFrequencies.forEach(wf -> System.out.printf("%s %s: %d\n", wf.getWord(), getPosAbbreviation(wf.getPartOfSpeech()), wf.getFrequency()));

        File txtFile = new File("D:\\Documents\\个人项目\\wordStatistics\\", "小猫钓鱼统计结果.txt");
        try {
            // 使用 FileOutputStream 写入文件
            try (FileOutputStream fos = new FileOutputStream(txtFile)) {
                // 使用 StringBuilder 构建文件内容
                StringBuilder content = new StringBuilder();
                content.append("名词、动词、形容词、副词的单词频率统计（按频率降序排列）:\n");
                sortedWordFrequencies.forEach(wf -> content.append(String.format("%s %s: %d\n", wf.getWord(), getPosAbbreviation(wf.getPartOfSpeech()), wf.getFrequency())));
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


    public String getPosAbbreviation(String pos) {
        switch (pos) {
            case "NN":
            case "NNS":
            case "NNP":
            case "NNPS":
                return "n."; // 名词
            case "VB":
            case "VBD":
            case "VBG":
            case "VBN":
            case "VBP":
            case "VBZ":
                return "v."; // 动词
            case "JJ":
            case "JJR":
            case "JJS":
                return "adj."; // 形容词
            case "RB":
            case "RBR":
            case "RBS":
                return "adv."; // 副词
            default:
                return pos; // 如果没有匹配项，返回原始标签
        }
    }


}
