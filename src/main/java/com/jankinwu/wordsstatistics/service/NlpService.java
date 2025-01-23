package com.jankinwu.wordsstatistics.service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: Jankin Wu
 * @date: 2025-01-23 23:55
 **/
@Service
public class NlpService {

    private final StanfordCoreNLP pipeline;

    public NlpService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        this.pipeline = new StanfordCoreNLP(props);
    }

    /**
     * 分析文本中的名词、动词、形容词和副词的数量及比例，并收集这些单词
     *
     * @param text 要分析的文本
     * @return 各类词性及其比例和具体单词的Map
     */
    public Map<String, Object> analyzeWordPartsOfSpeech(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);

        Map<String, Integer> posCounts = new HashMap<>();
        Map<String, List<String>> posWords = new HashMap<>();
        int totalWords = document.tokens().size();

        // 初始化词性集合
        Set<String> nounTags = Set.of("NN", "NNS", "NNP", "NNPS");
        Set<String> verbTags = Set.of("VB", "VBD", "VBG", "VBN", "VBP", "VBZ");
        Set<String> adjTags = Set.of("JJ", "JJR", "JJS");
        Set<String> advTags = Set.of("RB", "RBR", "RBS");

        // 统计每种词性出现的次数，并收集其单词
        document.tokens().forEach(token -> {
            String pos = token.tag();
            String word = token.word();
            posCounts.put(pos, posCounts.getOrDefault(pos, 0) + 1);

            if (!posWords.containsKey(pos)) {
                posWords.put(pos, new ArrayList<>());
            }
            posWords.get(pos).add(word);
        });

        // 统计每类词性的数量和单词列表
        List<String> nouns = new ArrayList<>();
        List<String> verbs = new ArrayList<>();
        List<String> adjectives = new ArrayList<>();
        List<String> adverbs = new ArrayList<>();

        double nounCount = 0;
        double verbCount = 0;
        double adjCount = 0;
        double advCount = 0;

        for (Map.Entry<String, Integer> entry : posCounts.entrySet()) {
            String pos = entry.getKey();
            int count = entry.getValue();
            if (nounTags.contains(pos)) {
                nounCount += count;
                nouns.addAll(posWords.get(pos));
            } else if (verbTags.contains(pos)) {
                verbCount += count;
                verbs.addAll(posWords.get(pos));
            } else if (adjTags.contains(pos)) {
                adjCount += count;
                adjectives.addAll(posWords.get(pos));
            } else if (advTags.contains(pos)) {
                advCount += count;
                adverbs.addAll(posWords.get(pos));
            }
        }

        // 准备结果
        Map<String, Object> result = new HashMap<>();
        result.put("nouns", Map.of("count", nounCount / totalWords, "words", nouns));
        result.put("verbs", Map.of("count", verbCount / totalWords, "words", verbs));
        result.put("adjectives", Map.of("count", adjCount / totalWords, "words", adjectives));
        result.put("adverbs", Map.of("count", advCount / totalWords, "words", adverbs));
        return result;
    }

    /**
     * 统计文本中名词、动词、形容词、副词的单词频率
     *
     * @param text 要分析的文本
     * @return 单词频率的Map
     */
    public Map<String, Integer> countSpecificWordFrequency(String text) {
        Map<String, Integer> wordFrequency = new HashMap<>();

        // 处理文本并标注词性
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);

        // 定义名词、动词、形容词、副词的POS标签集合
        Set<String> nounTags = Set.of("NN", "NNS", "NNP", "NNPS");
        Set<String> verbTags = Set.of("VB", "VBD", "VBG", "VBN", "VBP", "VBZ");
        Set<String> adjTags = Set.of("JJ", "JJR", "JJS");
        Set<String> advTags = Set.of("RB", "RBR", "RBS");

        // 遍历文本中的每个单词及其词性
        document.tokens().forEach(token -> {
            String word = token.word().toLowerCase(); // 单词统一为小写
            String pos = token.tag(); // 获取词性

            // 仅统计名词、动词、形容词、副词
            if (nounTags.contains(pos) || verbTags.contains(pos) || adjTags.contains(pos) || advTags.contains(pos)) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        });

        return wordFrequency;
    }


}
