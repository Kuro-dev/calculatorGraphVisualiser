package org.kurodev.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

public class ObservableTextField extends JTextField implements DocumentListener {
    private Consumer<String> onChange;

    public ObservableTextField(Consumer<String> onChange) {
        this();

        this.onChange = onChange;
    }

    public ObservableTextField() {
        getDocument().addDocumentListener(this);
    }

    public void setOnChange(Consumer<String> onChange) {
        this.onChange = onChange;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (onChange != null) {
            onChange.accept(getText());
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (onChange != null) {
            onChange.accept(getText());
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if (onChange != null) {
            onChange.accept(getText());
        }
    }
}
