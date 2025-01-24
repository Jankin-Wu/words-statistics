package com.jankinwu.wordsstatistics.handler;

import com.jankinwu.wordsstatistics.utils.FileUtil;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author 皖刚
 * @description 提取文本
 * @date 2025-01-24 16:47
 **/
public class ExtractText {

    public static void main(String[] args) {
        String inputDir = "D:\\Documents\\其他\\test"; // 输入目录路径
        String outputDir = "D:\\Documents\\其他\\test"; // 输出目录路径

        File folder = new File(inputDir);
        processFolder(folder, outputDir);
    }

    private static void processFolder(File folder, String outputDir) {
        if (!folder.isDirectory()) return;

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                // 递归处理子文件夹
                processFolder(file, outputDir);
            } else if (file.isFile() && file.getName().endsWith(".doc")) {
                // 处理.doc文件
                processDocFile(file, outputDir);
            }
        }
    }

    private static void processDocFile(File docFile, String outputDir) {
        try (FileInputStream fis = new FileInputStream(docFile);
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {

            String text = extractor.getText();
            String txtFileName = docFile.getName().replace(".doc", ".txt");
            File txtFile = new File(outputDir, txtFileName);
            String filerText = FileUtil.processTextContent(text);
            try (FileOutputStream fos = new FileOutputStream(txtFile)) {
                fos.write(filerText.getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("Converted: " + docFile.getPath() + " to " + txtFile.getPath());
//            System.out.println(filerText);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
        }
    }
}
