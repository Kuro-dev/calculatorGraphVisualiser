package org.kurodev.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.IntStream;

public class GraphComponent extends JPanel {
    private static final int PADDING = 20;
    private final List<Coordinate> data = new ObservableList<>(ignored -> repaint());
    private int coordRangeX = 10;
    private boolean drawLabels = false;
    private boolean drawLines = true;
    private boolean drawPoints = true;

    public GraphComponent() {
    }

    public void setDrawPoints(boolean drawPoints) {
        this.drawPoints = drawPoints;
        repaint();
    }

    public boolean isDrawingLines() {
        return drawLines;
    }

    public void setDrawLines(boolean drawLines) {
        this.drawLines = drawLines;
        repaint();
    }

    public List<Coordinate> getData() {
        return data;
    }

    public void setRange(int coordRangeX) {
        this.coordRangeX = coordRangeX;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g1 = (Graphics2D) g;
        Point2D zeroPoint = calculateZeroPoint();
        Point2D lowestX = calculateLowerX(zeroPoint);
        Point2D highestX = calculateHigherX(zeroPoint);
        Point2D lowestY = calculateLowerY(zeroPoint);
        Point2D highestY = calculateHigherY(zeroPoint);
        drawGraph(g1, zeroPoint, lowestX, lowestY, highestX, highestY);
        drawData(g1, zeroPoint, lowestX, lowestY);
    }

    public boolean isDrawingLabels() {
        return drawLabels;
    }

    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
        repaint();
    }

    private void drawData(Graphics2D g1, Point2D zeroPoint, Point2D lowestX, Point2D lowestY) {
        int circleDiameter = 5;
        int circleXOffset = circleDiameter / 2;
        int distX = computeDistX(lowestX, zeroPoint); //distance on screen in pixels between coordinate points on X axis
        int distY = computeDistY(lowestY, zeroPoint); //distance on screen in pixels between coordinate points on Y axis
        Color color = g1.getColor();
        Coordinate last = null;
        for (Coordinate coordinate : data) {
            int x = (int) (zeroPoint.getX() + distX * coordinate.x()); //X coordinate on screen
            int y = (int) (zeroPoint.getY() - distY * coordinate.y()); //y coordinate on screen
            if (drawLines && last != null) {
                g1.drawLine((int) last.x(), (int) last.y(), x, y);
            }
            if (drawLines)
                last = new Coordinate(x, y);
            if (drawLabels && !coordinate.label().isBlank()) {
                int offsetX = coordinate.x() > 0 ? 5 : -15;
                int offsetY = coordinate.y() > 0 ? -5 : 15;
                g1.setColor(Color.BLACK);
                g1.drawString(coordinate.label(), x + offsetX, y + offsetY);
            }
            g1.setColor(Color.BLUE);
            if (drawPoints)
                g1.fillOval(x - circleXOffset, y - circleXOffset, circleDiameter, circleDiameter);

        }
        g1.setColor(color);
    }

    private int computeDistX(Point2D lowestX, Point2D highestX) {
        return (int) (lowestX.distance(highestX) / coordRangeX);
    }

    private int computeDistY(Point2D lowestY, Point2D highestY) {
        return (int) (lowestY.distance(highestY) / computeCoordRangeY());
    }

    private void drawGraph(Graphics2D graphics2D, Point2D zeroPoint, Point2D lowestX, Point2D lowestY, Point2D highestX, Point2D highestY) {

        drawLine(graphics2D, lowestX, highestX); // X line (where y is 0)
        drawLine(graphics2D, highestY, lowestY); // y line (where x is 0)
        drawLabelsHorizontal(graphics2D, zeroPoint, lowestX, highestX);
        drawLabelsVertical(graphics2D, zeroPoint, lowestY, highestY);
    }

    private int computeCoordRangeY() {
        int max = data.stream().flatMapToInt(coordinate -> IntStream.of((int) coordinate.y())).max().orElse(10);
        int min = data.stream().flatMapToInt(coordinate -> IntStream.of((int) coordinate.y())).min().orElse(-10);
        boolean maxHigher = max - Math.abs(min) >= 0;
        int out;
        if (maxHigher) {
            if (max < 10) {
                out = 10;
            } else {
                out = max + 5;
            }
        } else {
            if (min > -10) {
                out = 10;
            } else {
                out = min - 5;
            }
        }
        return Math.abs(out);

    }

    /**
     * the label goes off screen for the last few digits on the scale. This needs to be fixed
     * TODO(23/11/2022) figure something out
     */
    private void drawLabelsVertical(Graphics2D g1, Point2D zeroPoint, Point2D lowestY, Point2D highestY) {
        int coordRangeY = computeCoordRangeY();
        final int markerLength = 3;
        double rangeMin = lowestY.distance(zeroPoint);
        double distMin = rangeMin / (coordRangeY);
        double temp = highestY.getY();
        int xLevel = (int) zeroPoint.getX();
        for (int i = coordRangeY; i > 0; i--) {
            int yLevel = (int) temp;
            g1.drawLine(xLevel + markerLength, yLevel, xLevel - markerLength, yLevel);
            if (drawLabel(coordRangeY, i))
                g1.drawString(String.valueOf(i), xLevel - 20, yLevel + 5);
            temp += distMin;
        }
        double rangeMax = highestY.distance(zeroPoint);
        double distMax = rangeMax / (coordRangeY);
        for (int i = 1; i < coordRangeY; i++) {
            temp += distMax;
            int yLevel = (int) temp;
            g1.drawLine(xLevel + markerLength, yLevel, xLevel - markerLength, yLevel);
            if (drawLabel(coordRangeY, i))
                g1.drawString("-" + i, xLevel - 20, yLevel + 5);
        }
    }

    private boolean drawLabel(int range, int index) {
        if (range < 20)
            return true;
        else if (range < 30)
            return (index & 1) == 0;
        else
            return index % 5 == 0;
    }

    private void drawLabelsHorizontal(Graphics2D g1, Point2D zeroPoint, Point2D lowestX, Point2D highestX) {
        final int markerLength = 3;
        double rangeMin = lowestX.distance(zeroPoint);
        double distMin = rangeMin / (coordRangeX);
        double temp = lowestX.getX();
        int yLevel = (int) zeroPoint.getY();
        for (int i = coordRangeX; i > 0; i--) {
            int xLevel = (int) temp;
            g1.drawLine(xLevel, yLevel + markerLength, xLevel, yLevel - markerLength);
            g1.drawString("-" + i, xLevel - 3, yLevel + 15);
            temp += distMin;
        }
        double rangeMax = highestX.distance(zeroPoint);
        double distMax = rangeMax / (coordRangeX);
        for (int i = 0; i <= coordRangeX; i++) {
            int xLevel = (int) temp;
            g1.drawLine(xLevel, yLevel + markerLength, xLevel, yLevel - markerLength);
            g1.drawString(String.valueOf(i), xLevel - 3, yLevel + 15);
            temp += distMax;
        }
    }


    private void drawLine(Graphics2D g, Point2D a, Point2D b) {
        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
    }

    private Point2D calculateLowerY(Point2D zeroPoint) {
        double y = (double) getParent().getHeight() - PADDING;
        return new Point2D.Double(zeroPoint.getX(), y);
    }

    private Point2D calculateHigherY(Point2D zeroPoint) {
        return new Point2D.Double(zeroPoint.getX(), PADDING);
    }

    private Point2D calculateHigherX(Point2D zeroPoint) {
        double x = (double) getParent().getWidth() - PADDING;
        return new Point2D.Double(x, zeroPoint.getY());
    }

    private Point2D calculateLowerX(Point2D zeroPoint) {
        return new Point2D.Double(PADDING, zeroPoint.getY());
    }

    private Point2D calculateZeroPoint() {
        double centerWidth = (double) getParent().getWidth() / 2;
        double centerHeight = (double) getParent().getHeight() / 2;
        return new Point2D.Double(centerWidth, centerHeight);
    }

    public boolean isDrawingPoints() {
        return drawPoints;
    }
}
