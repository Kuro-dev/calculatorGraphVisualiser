package org.kurodev;

import org.kurodev.calculator.maths.FormulaParser;
import org.kurodev.components.Coordinate;
import org.kurodev.components.GraphComponent;
import org.kurodev.components.ObservableTextField;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class MainWindow {
    private static final Color ERROR_COLOR = Color.RED.brighter();
    private static final Color SUCCESS_COLOR = Color.WHITE;
    private final JPanel topBar = new JPanel();
    private final JPanel bottomBar = new JPanel();
    private final GraphComponent graph = new GraphComponent();
    private final JFrame window;
    private final JLabel label = new JLabel("f(x)=");
    private final ObservableTextField input = new ObservableTextField();
    private double stepSize = 0;
    private double range = 5;

    public MainWindow(String title) {
        window = new JFrame(title);
        window.setLayout(new BorderLayout());
        window.add(topBar, BorderLayout.NORTH);
        window.add(graph, BorderLayout.CENTER);
        window.add(bottomBar, BorderLayout.SOUTH);
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.LINE_AXIS));
        label.setLabelFor(input);
    }

    public void init() {
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        input.setOnChange(this::handleTextInput);

        var showLabels = new JCheckBox("Show labels", graph.isDrawingLabels());
        showLabels.addActionListener(e -> graph.setDrawLabels(!graph.isDrawingLabels()));

        var drawLines = new JCheckBox("Draw lines", graph.isDrawingLines());
        drawLines.addActionListener(e -> graph.setDrawLines(!graph.isDrawingLines()));

        var drawPoints = new JCheckBox("Draw Points", graph.isDrawingPoints());
        drawPoints.addActionListener(e -> graph.setDrawPoints(!graph.isDrawingPoints()));
        var stepSize = new JSlider(1, 10, 4);
        this.stepSize = 1 / (double) stepSize.getValue();

        var stepLabel = new JLabel(String.format("Step size: %.3f", this.stepSize));
        stepLabel.setLabelFor(stepSize);
        stepSize.addChangeListener(e -> {
            stepLabel.setText(String.format("Step size: %.3f", this.stepSize));
            this.stepSize = 1 / (double) stepSize.getValue();
            handleTextInput(input.getText());
        });
        var rangeSlider = new JSlider(1, 10, (int) range);
        var rangeLabel = new JLabel("Range: " + range);
        rangeLabel.setLabelFor(stepSize);
        rangeSlider.addChangeListener(e -> {
            rangeLabel.setText("Max range: " + range);
            range = rangeSlider.getValue();
            handleTextInput(input.getText());
        });

        bottomBar.add(rangeLabel);
        bottomBar.add(rangeSlider);
        bottomBar.add(stepLabel);
        bottomBar.add(stepSize);

        topBar.add(label);
        topBar.add(input);
        topBar.add(showLabels);
        topBar.add(drawLines);
        topBar.add(drawPoints);
        window.setSize(800, 600);
    }


    private void handleTextInput(String text) {
        if (text != null && !text.isBlank()) {
            FormulaParser parser = new FormulaParser();
            graph.getData().clear();
            for (double graphX = -range; graphX <= range; graphX = graphX + stepSize) {
                parser.getVariables().put("x", BigDecimal.valueOf(graphX));
                var result = parser.calculate(text);
                if (result.isNumber()) {
                    this.input.setBackground(SUCCESS_COLOR);
                    double graphY = result.getResult().doubleValue();
                    Coordinate point = new Coordinate(graphX, graphY, String.format("( %.2f | %.2f)", graphX, graphY));
                    //System.out.println(point.label());
                    graph.getData().add(point);
                } else {
                    this.input.setBackground(ERROR_COLOR);
                }
            }
        }
    }

    public void show() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
        window.setLocation(x, y);
        window.setVisible(true);
    }
}
