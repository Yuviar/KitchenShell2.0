/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.util;

import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author MI TA
 */
public class InputFilter {
     public enum FilterType {
        ONLY_NUMBERS,
        ONLY_LETTERS,
        LETTERS_NUMBERS_SPACE,
        LETTERS_NUMBERS_DOT_COMMA
    }

    public static void setInputFilter(JTextField textField, FilterType type) {
        String allowedRegex;

        switch (type) {
            case ONLY_NUMBERS:
                allowedRegex = "[0-9]*";
                break;
            case ONLY_LETTERS:
                allowedRegex = "[a-z A-Z]*";
                break;
            case LETTERS_NUMBERS_SPACE:
                allowedRegex = "[a-zA-Z0-9 ]*";
                break;
            case LETTERS_NUMBERS_DOT_COMMA:
                allowedRegex = "[a-zA-Z0-9., ]*";
                break;
            default:
                allowedRegex = ".*"; // fallback: no restriction
        }

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches(allowedRegex)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches(allowedRegex)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}
