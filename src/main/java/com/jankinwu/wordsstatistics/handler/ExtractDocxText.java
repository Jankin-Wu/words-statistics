package com.jankinwu.wordsstatistics.handler;


import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTxbxContent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @description: 提取docx中的文本
 * @author: Jankin Wu
 * @date: 2025-01-25 14:48
 **/
public class ExtractDocxText {

    public static void main(String[] args) {
        String inputDir = "D:\\Documents\\个人项目\\wordStatistics\\test"; // 输入目录路径
        String outputDir = "D:\\Documents\\个人项目\\wordStatistics\\test"; // 输出目录路径

        File folder = new File(inputDir);
        processFolder(folder, outputDir);
    }

    private static void processFolder(File folder, String outputDir) {
        if (!folder.isDirectory()) return;

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                // 递归处理子文件夹
                processFolder(file, outputDir);
            } else if (file.isFile() && file.getName().endsWith(".docx")) {
                // 处理.docx文件
                processDocxFile(file, outputDir);
            }
        }
    }

//    private static void processDocxFile(File docxFile, String outputDir) {
//        try (FileInputStream fis = new FileInputStream(docxFile);
//             XWPFDocument document = new XWPFDocument(fis)) {
//
//            StringBuilder textContent = new StringBuilder();
//
//            // 遍历文档中的段落
//            List<XWPFParagraph> paragraphs = document.getParagraphs();
//            for (XWPFParagraph paragraph : paragraphs) {
//                textContent.append(paragraph.getText()).append("\n");
//            }
//
//            // 遍历文档中的表格
//            List<XWPFTable> tables = document.getTables();
//            for (XWPFTable table : tables) {
//                for (XWPFTableRow row : table.getRows()) {
//                    for (XWPFTableCell cell : row.getTableCells()) {
//                        textContent.append(cell.getText().trim()).append("\t");
//                    }
//                    textContent.append("\n");
//                }
//            }
//
//            String txtFileName = docxFile.getName().replace(".docx", ".txt");
//            File txtFile = new File(outputDir, txtFileName);
//
//            try (FileOutputStream fos = new FileOutputStream(txtFile)) {
//                fos.write(textContent.toString().getBytes(StandardCharsets.UTF_8));
//            }
//
//            System.out.println("Converted: " + docxFile.getPath() + " to " + txtFile.getPath());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static void processDocxFile(File docxFile, String outputDir) {
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {

            StringBuilder textContent = new StringBuilder();

            // 遍历文档中的段落
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                textContent.append(paragraph.getText()).append("\n");

                // 处理段落中的文本框
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    for (XWPFRun run : runs) {
                        CTR ctr = run.getCTR();
                        for (CTDrawing drawing : ctr.getDrawingList()) {
                            // 处理Inline类型的Drawing
                            for (CTInline inline : drawing.getInlineList()) {
                                XmlCursor cursor = inline.getGraphic().getGraphicData().newCursor();
                                cursor.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//w:txbxContent");
                                while (cursor.toNextSelection()) {
                                    XmlObject obj = cursor.getObject();
                                    if (obj instanceof CTTxbxContent) {
                                        CTTxbxContent textBoxContent = (CTTxbxContent) obj;
                                        for (CTP ctp : textBoxContent.getPList()) {
                                            XWPFParagraph textBoxParagraph = new XWPFParagraph(ctp, document);
                                            textContent.append(textBoxParagraph.getText()).append("\n");
                                        }
                                    }
                                }
                                cursor.dispose();
                            }

                            // 处理Anchor类型的Drawing（如果有）
                            for (CTAnchor anchor : drawing.getAnchorList()) {
                                XmlCursor cursor = anchor.getGraphic().getGraphicData().newCursor();
                                cursor.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//w:txbxContent");
                                while (cursor.toNextSelection()) {
                                    XmlObject obj = cursor.getObject();
                                    if (obj instanceof CTTxbxContent) {
                                        CTTxbxContent textBoxContent = (CTTxbxContent) obj;
                                        for (CTP ctp : textBoxContent.getPList()) {
                                            XWPFParagraph textBoxParagraph = new XWPFParagraph(ctp, document);
                                            textContent.append(textBoxParagraph.getText()).append("\n");
                                        }
                                    }
                                }
                                cursor.dispose();
                            }
                        }
                    }
                }
            }

            // 遍历文档中的表格
            List<XWPFTable> tables = document.getTables();
            for (XWPFTable table : tables) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        textContent.append(cell.getText().trim()).append("\t");
                    }
                    textContent.append("\n");
                }
            }

            String txtFileName = docxFile.getName().replace(".docx", ".txt");
            File txtFile = new File(outputDir, txtFileName);

            try (FileOutputStream fos = new FileOutputStream(txtFile)) {
                fos.write(textContent.toString().getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("Converted: " + docxFile.getPath() + " to " + txtFile.getPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void processDocxFile(File docxFile, String outputDir) {
//        try (FileInputStream fis = new FileInputStream(docxFile);
//             XWPFDocument document = new XWPFDocument(fis)) {
//
//            StringBuilder textContent = new StringBuilder();
//
//            // 递归提取文档主体元素的文本内容
//            extractFromBodyElements(document.getBodyElements(), textContent);
//
//            String txtFileName = docxFile.getName().replace(".docx", ".txt");
//            File txtFile = new File(outputDir, txtFileName);
//
//            try (FileOutputStream fos = new FileOutputStream(txtFile)) {
//                fos.write(textContent.toString().getBytes(StandardCharsets.UTF_8));
//            }
//
//            System.out.println("Converted: " + docxFile.getPath() + " to " + txtFile.getPath());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void extractFromBodyElements(List<IBodyElement> elements, StringBuilder textContent) {
//        for (IBodyElement element : elements) {
//            if (element instanceof XWPFParagraph) {
//                XWPFParagraph paragraph = (XWPFParagraph) element;
//                extractTextFromParagraph(paragraph, textContent);
//            } else if (element instanceof XWPFTable) {
//                XWPFTable table = (XWPFTable) element;
//                extractTextFromTable(table, textContent);
//            }
//        }
//    }
//
//    private static void extractTextFromParagraph(XWPFParagraph paragraph, StringBuilder textContent) {
//        // 添加段落文本
//        textContent.append(paragraph.getText()).append("\n");
//
//        // 处理段落中的运行
//        for (XWPFRun run : paragraph.getRuns()) {
//            CTR ctr = run.getCTR();
//
//            // 处理绘图元素（drawing）
//            for (CTDrawing drawing : ctr.getDrawingList()) {
//                extractTextFromDrawing(drawing, textContent);
//            }
//
//            // 处理图片元素（pict）
//            for (CTPicture pict : ctr.getPictList()) {
//                extractTextFromPicture(pict, textContent);
//            }
//        }
//    }
//
//    private static void extractTextFromDrawing(CTDrawing drawing, StringBuilder textContent) {
//        XmlCursor cursor = drawing.newCursor();
//        try {
//            // 声明命名空间前缀 'w'
//            String xpath = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//w:txbxContent";
//            cursor.selectPath(xpath);
//            while (cursor.toNextSelection()) {
//                XmlObject obj = cursor.getObject();
//                if (obj instanceof CTTxbxContent) {
//                    CTTxbxContent textBoxContent = (CTTxbxContent) obj;
//                    extractTextFromTextboxContent(textBoxContent, textContent);
//                }
//            }
//        } finally {
//            cursor.dispose();
//        }
//    }
//
//    private static void extractTextFromPicture(CTPicture pict, StringBuilder textContent) {
//        XmlCursor cursor = pict.newCursor();
//        try {
//            // 声明命名空间前缀 'w'
//            String xpath = "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//w:txbxContent";
//            cursor.selectPath(xpath);
//            while (cursor.toNextSelection()) {
//                XmlObject obj = cursor.getObject();
//                if (obj instanceof CTTxbxContent) {
//                    CTTxbxContent textBoxContent = (CTTxbxContent) obj;
//                    extractTextFromTextboxContent(textBoxContent, textContent);
//                }
//            }
//        } finally {
//            cursor.dispose();
//        }
//    }
//
//    private static void extractTextFromTextboxContent(CTTxbxContent textBoxContent, StringBuilder textContent) {
//        for (CTP ctp : textBoxContent.getPList()) {
//            XWPFParagraph paragraph = new XWPFParagraph(ctp, null);
//            extractTextFromParagraph(paragraph, textContent);
//        }
//        for (CTTbl cttbl : textBoxContent.getTblList()) {
//            XWPFTable table = new XWPFTable(cttbl, null);
//            extractTextFromTable(table, textContent);
//        }
//    }
//
//    private static void extractTextFromTable(XWPFTable table, StringBuilder textContent) {
//        for (XWPFTableRow row : table.getRows()) {
//            for (XWPFTableCell cell : row.getTableCells()) {
//                // 递归提取单元格中的内容
//                extractFromBodyElements(cell.getBodyElements(), textContent);
//            }
//            textContent.append("\n");
//        }
//    }



}
