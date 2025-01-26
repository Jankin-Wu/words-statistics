package com.jankinwu.wordsstatistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 单词频率
 * @author: Jankin Wu
 * @date: 2025-01-24 21:40
 **/
@AllArgsConstructor
@Data
public class WordFrequencyDTO {

    private String word;
    private String partOfSpeech;
    private int frequency;
}
