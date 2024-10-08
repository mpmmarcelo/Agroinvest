package com.shineworks.agroinvest;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.function.UnaryOperator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

public class MNumberField extends TextField {
    public static final String CAD_PRO = "########-##";
    public static final String CPF = "###.###.###-##";
    public static final String CNPJ = "##.###.###/####-##";
    public static final String INSC_ESTAD = "###.#####-##";
    public static final String TELEFONE = "(##)#####-####";
    public static final String DATA = "##/##/####";

    public MNumberField() {
        UnaryOperator<Change> simpleNumberFilter = (change) -> {
            String text = change.getText();
            if (text.length() == 1 && !text.matches("\\d")) {
                change.setText("");
            }

            return change;
        };
        this.setTextFormatter(new TextFormatter(simpleNumberFilter));
    }

    public void installMask(String mask) {
        UnaryOperator<Change> numberFilter = (change) -> {
            int pos = change.getControlCaretPosition();
            String text = change.getText();
            if (!text.isEmpty() && text.length() <= 1) {
                if (pos != mask.length() && text.matches("\\d*")) {
                    if ('#' == mask.charAt(pos)) {
                        return change;
                    } else if (change.isReplaced()) {
                        return change;
                    } else {
                        change.setText(mask.charAt(pos) + text);
                        change.setCaretPosition(change.getCaretPosition() + 1);
                        change.setAnchor(change.getCaretPosition());
                        return change;
                    }
                } else {
                    change.setText("");
                    return change;
                }
            } else {
                return change;
            }
        };
        this.setTextFormatter(new TextFormatter(numberFilter));
    }

    public void applyMask(String mask) {
        String text = this.getPlainText();

        for(int i = 0; i < text.length(); ++i) {
            mask = mask.replaceFirst("#", text.charAt(i) + "");
        }

        this.setText(mask);
    }

    public static String applyMask(String text, String pattern) {
        String mask = pattern;

        for(int i = 0; i < text.length(); ++i) {
            mask = mask.replaceFirst("#", text.charAt(i) + "");
        }

        return mask;
    }

    public void autoApplyMask() {
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                switch(this.getPlainText().length()) {
                    case 10:
                        this.applyMask("###.#####-##");
                        break;
                    case 11:
                        this.applyMask("###.###.###-##");
                        break;
                    case 12:
                    case 13:
                    default:
                        //MDialog.createWarningAlert("Valor inserido no campo destinatário não confere com Inscrição Estadual, CPF ou CNPJ.");
                        break;
                    case 14:
                        this.applyMask("##.###.###/####-##");
                }
            }

        });
    }

    public String getPlainText() {
        return this.getText().replaceAll("[^\\d]", "");
    }
}
