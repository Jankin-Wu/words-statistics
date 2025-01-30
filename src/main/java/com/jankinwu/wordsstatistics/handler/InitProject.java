package com.jankinwu.wordsstatistics.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Jankin Wu
 * @description
 * @date 2025-01-29 22:13
 **/
@Service
@RequiredArgsConstructor
public class InitProject {

    private final WordStatistics wordStatistics;

    @PostConstruct
    public void init() throws IOException {
        wordStatistics.countWordFrequencyInMergedFiles("D:\\Documents\\其他\\test\\上海高考英语TXT");
    }
}
