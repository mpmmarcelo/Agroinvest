package com.shineworks.agroinvest;

import javafx.scene.control.Alert;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MReplacer {

    String fileInput, fileOutput;
    XWPFDocument doc;

    public MReplacer(String fileInput, String fileOutput) {
        this.fileInput = fileInput;
        this.fileOutput = fileOutput;
    }

    public boolean open() {
        try {
            doc = new XWPFDocument(Files.newInputStream(Paths.get(fileInput)));
            return true;
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("MREPLACER ERROR");
            alert.setHeaderText("Não foi possível abrir o arquivo especificado");
            alert.setContentText(fileInput);
            alert.showAndWait();

            ex.printStackTrace();
            return false;
        }
    }

    public void replace(String tag, String text) {
        List<XWPFParagraph> xwpfParagraphList = doc.getParagraphs();
        //Iterate over paragraph list and check for the replaceable text in each paragraph
        for (XWPFParagraph xwpfParagraph : xwpfParagraphList) {
            for (XWPFRun xwpfRun : xwpfParagraph.getRuns()) {
                String docText = xwpfRun.getText(0);
                //replacement and setting position
                if (docText != null && docText.contains(tag)) {
                    docText = docText.replace(tag, text);
                    xwpfRun.setText(docText, 0);
                    return;
                }
            }
        }
    }

    public void replaceAll(String tag, String text) {
        List<XWPFParagraph> xwpfParagraphList = doc.getParagraphs();
        //Iterate over paragraph list and check for the replaceable text in each paragraph
        for (XWPFParagraph xwpfParagraph : xwpfParagraphList) {
            for (XWPFRun xwpfRun : xwpfParagraph.getRuns()) {
                String docText = xwpfRun.getText(0);
                //replacement and setting position
                if (docText != null && docText.contains(tag)) {
                    docText = docText.replaceAll(tag, text);
                    xwpfRun.setText(docText, 0);
                }
            }
        }
    }

    public void replaceOnTable(String tag, String text) {
        for (XWPFTable xwpfTable : doc.getTables()) {
            for (XWPFTableRow xwpfTableRow : xwpfTable.getRows()) {
                for (XWPFTableCell xwpfTableCell : xwpfTableRow.getTableCells()) {
                    for (XWPFParagraph xwpfParagraph : xwpfTableCell.getParagraphs()) {
                        for (XWPFRun xwpfRun : xwpfParagraph.getRuns()) {
                            String docText = xwpfRun.getText(0);
                            //replacement and setting position
                            if (docText != null && docText.contains(tag)) {
                                docText = docText.replace(tag, text);
                                xwpfRun.setText(docText, 0);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void replaceAllOnTable(String tag, String text) {
        for (XWPFTable xwpfTable : doc.getTables()) {
            for (XWPFTableRow xwpfTableRow : xwpfTable.getRows()) {
                for (XWPFTableCell xwpfTableCell : xwpfTableRow.getTableCells()) {
                    for (XWPFParagraph p : xwpfTableCell.getParagraphs()) {
                        for (XWPFRun xwpfRun : p.getRuns()) {
                            String docText = xwpfRun.getText(0);
                            //replacement and setting position
                            docText = docText.replaceAll(tag, text);
                            xwpfRun.setText(docText, 0);
                        }
                    }
                }
            }
        }
    }

    public boolean save() {
        try (FileOutputStream out = new FileOutputStream(fileOutput)) {
            doc.write(out);
            doc.close();
            return true;
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("MREPLACER ERROR");
            alert.setHeaderText("Não foi possível salvar o arquivo especificado");
            alert.setContentText(fileOutput);
            alert.showAndWait();

            e.printStackTrace();
            return false;
        }
    }

    public String getFileInput() {
        return fileInput;
    }

    public void setFileInput(String fileInput) {
        this.fileInput = fileInput;
    }

    public String getFileOutput() {
        return fileOutput;
    }

    public void setFileOutput() {
        this.fileOutput = fileOutput;
    }
}