package com.jankinwu.wordsstatistics.handler;

import com.jankinwu.wordsstatistics.service.NlpService;
import com.jankinwu.wordsstatistics.utils.FilterUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            we live on the island of Hale. it's about four kilometers long and two kilometers wide at its broadest point, and it is joined to the mainland by a causeway (21) _______(call) Stand---a narrow road built across the mouth of the river (22) ________ separates us from the rest of the country.\s
            Most of the time you wouldn’t know we are on an island because the river mouth between us and the mainland is just a vast stretch of tall grasses and brown mud. But when there is high tide and the water rises a half meter or so above the road and nothing can pass (23) _________the tide goes out again a few hours later, then you know it’s an island.
                    We were on our way back (24) _________ the mainland. My older brother, Dominic, had just finished his first in university in a town 150km away. Dominic’s train was due in at five and he’d asked for a lift back from the station. Now, Dad normally hates being disturbed when he (25) __________ (write) (which is just about all the time), and he also hates having to go anywhere, but despite the typical sighs and moans --- why can’t he get a taxi? What’s wrong with the bus? ----I could tell by the flash in the eyes that he was really looking forward to (26) ________ (see) Dominic.
                    So, anyway, Dad and I had driven to the mainland and picked up Dominic from the station. He had been talking non-stop from the moment he’d get in to the car. University this, university that, writers, books, parties, people, money…….. I didn’t like the way he spoke and waved his hands around (27) ________he was some kind of scholar or something. It was embarrassing. It made me feel uncomfortable----that kind of discomfort you feel when someone you like, someone close to you, suddenly starts acting like a complete idiot. And I didn’t like the way he was ignoring me, either. For all the attention I was getting I (28) _________ as well not have been there. I felt a stranger.
                    We were about half across when I saw a boy. My first thought was how odd it was (29) _________(see) someone walking on the Strand. You don’t often see people walking around there. As we drew (30) _______(close) , he became clearer. He was actually a young man rather than a boy.
        """;
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
