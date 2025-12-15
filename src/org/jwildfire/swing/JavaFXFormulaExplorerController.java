package org.jwildfire.swing;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.nfunk.jep.Node;

public class JavaFXFormulaExplorerController {

    @FXML private TextField formula1Field;
    @FXML private TextField formula2Field;
    @FXML private TextField formula3Field;
    @FXML private TextField xMinField;
    @FXML private TextField xMaxField;
    @FXML private TextField xCountField;
    @FXML private TextArea valuesTextArea;
    @FXML private Canvas formulaCanvas;

    @FXML
    public void initialize() {
        // Set defaults matching original Swing UI
        formula1Field.setText("rect(5*x-7)");
        formula2Field.setText("sin(5*x-7)");
        formula3Field.setText("triangle(5*x-7)*sin(5*x-7)");
        xMinField.setText("0.0");
        xMaxField.setText("2");
        xCountField.setText("500");
    }

    @FXML
    private void onCalculate() {
        try {
            calculateAndDraw();
        } catch (Exception e) {
            e.printStackTrace();
            valuesTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void calculateAndDraw() throws Exception {
        final int MAX_FORMULA_COUNT = 3;
        StringBuilder sb = new StringBuilder();
        double xmin = Double.parseDouble(xMinField.getText());
        double xmax = Double.parseDouble(xMaxField.getText());
        int xCount = Integer.parseInt(xCountField.getText());
        if (xCount < 2) xCount = 2;
        double xstep = (xmax - xmin) / (double) (xCount - 1);

        JEPWrapper parser = new JEPWrapper();
        parser.addVariable("x", 0.0);
        Node[] fNode = new Node[MAX_FORMULA_COUNT];
        int fCount = 0;

        if (!formula1Field.getText().isEmpty()) fNode[fCount++] = parser.parse(formula1Field.getText());
        if (!formula2Field.getText().isEmpty()) fNode[fCount++] = parser.parse(formula2Field.getText());
        if (!formula3Field.getText().isEmpty()) fNode[fCount++] = parser.parse(formula3Field.getText());
        
        if (fCount == 0) fNode[fCount++] = parser.parse("0");

        double[] x = new double[xCount];
        double[][] y = new double[fCount][xCount];
        double xc = xmin;

        for (int i = 0; i < xCount; i++) {
            x[i] = xc;
            parser.setVarValue("x", xc);
            sb.append("  f(").append(Tools.doubleToString(x[i])).append(")  \t");
            for (int j = 0; j < fCount; j++) {
                y[j][i] = (Double) parser.evaluate(fNode[j]);
                sb.append(Tools.doubleToString(y[j][i])).append(" \t");
            }
            sb.append("\n");
            xc += xstep;
        }

        valuesTextArea.setText(sb.toString());
        valuesTextArea.positionCaret(0); // Move caret to top

        draw(x, y);
    }

    private void draw(double[] x, double[][] y) {
        if (formulaCanvas == null || x == null || y == null) return;

        GraphicsContext g = formulaCanvas.getGraphicsContext2D();
        double width = formulaCanvas.getWidth();
        double height = formulaCanvas.getHeight();

        // Clear background
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setStroke(Color.WHITE);
        g.strokeRect(0, 0, width, height);

        int pCount = x.length;
        if (pCount == 0) return;

        // Compute Min/Max
        double xMin = x[0];
        double xMax = x[0];
        double yMin = y[0][0];
        double yMax = y[0][0];

        for (int i = 1; i < pCount; i++) {
            if (x[i] < xMin) xMin = x[i];
            else if (x[i] > xMax) xMax = x[i];
            
            for (int j = 0; j < y.length; j++) {
                if (y[j][i] < yMin) yMin = y[j][i];
                else if (y[j][i] > yMax) yMax = y[j][i];
            }
        }

        if ((xMax - xMin) < MathLib.EPSILON) xMax = xMin + MathLib.EPSILON;
        if ((yMax - yMin) < MathLib.EPSILON) yMax = yMin + MathLib.EPSILON;

        // Visual Area
        double visXMin = xMin - (xMax - xMin) * 0.1;
        double visXMax = xMax + (xMax - xMin) * 0.1;
        double visYMin = yMin - (yMax - yMin) * 0.1;
        double visYMax = yMax + (yMax - yMin) * 0.1;

        double xScale = width / (visXMax - visXMin);
        double yScale = height / (visYMax - visYMin);
        double dxi = visXMin * xScale;
        double dyi = visYMin * yScale;

        // Draw Curves
        Color[] curveColors = { Color.YELLOW, Color.RED, Color.GREEN };
        g.setLineWidth(1.0);

        for (int j = 0; j < y.length; j++) {
            g.setStroke(curveColors[j % curveColors.length]);
            g.beginPath();
            boolean first = true;
            for (int i = 0; i < pCount; i++) {
                double px = (x[i] * xScale) - dxi;
                double py = height - ((y[j][i] * yScale) - dyi);
                if (first) {
                    g.moveTo(px, py);
                    first = false;
                } else {
                    g.lineTo(px, py);
                }
            }
            g.stroke();
        }

        // Draw Ticks
        g.setStroke(Color.WHITE);
        g.setFill(Color.WHITE);
        final int TICK_LENGTH = 7;
        final int LABEL_OFF = 3;
        final int X_TICKS = 3;
        
        // Font setup? JavaFX uses Font API, but default might be ok. 
        // g.setFont(new Font("Arial", 12)); 

        double xStep = calcTickStep(visXMax, visXMin, X_TICKS);
        double tickXMin = calcTickMinMax(visXMin, xStep);
        double tickXMax = calcTickMinMax(visXMax, xStep) + xStep;

        double xt = tickXMin;
        while (xt + xStep < tickXMax + MathLib.EPSILON) {
            double xti = (xt * xScale) - dxi;
            g.strokeLine(xti, 0, xti, TICK_LENGTH);
            g.strokeLine(xti, height, xti, height - TICK_LENGTH);
            
            String label = Tools.doubleToString(xt);
            // Center text: approximate width or use Text object.
            // Simplified centering for now.
            g.fillText(label, xti - 10, height - TICK_LENGTH - LABEL_OFF); 
            xt += xStep;
        }

        final int yTicks = (int) (height / width * X_TICKS + 0.5);
        double yStep = calcTickStep(visYMax, visYMin, yTicks);
        double tickYMin = calcTickMinMax(visYMin, yStep);
        double tickYMax = calcTickMinMax(visYMax, yStep) + yStep;

        double yt = tickYMin;
        while (yt + yStep < tickYMax + MathLib.EPSILON) {
            double yti = height - ((yt * yScale) - dyi);
            g.strokeLine(0, yti, TICK_LENGTH, yti);
            g.strokeLine(width, yti, width - TICK_LENGTH, yti);
            
            String label = Tools.doubleToString(yt);
            g.fillText(label, width - 40, yti + 4); 
            yt += yStep;
        }
    }

    private double calcTickMinMax(double pVisMin, double pStep) {
        double res = 0.0;
        if (pVisMin > 0.0) {
            while (res + pStep < pVisMin) res += pStep;
        } else {
            while (res - pStep > pVisMin) res -= pStep;
        }
        return res;
    }

    private double calcTickStep(double pVisMax, double pVisMin, int pTicks) {
        double res = (pVisMax - pVisMin) / (pTicks + 1);
        if (res < 1.0) {
            double v = res;
            res = 1.0;
            while (v < 1.0) {
                v *= 5.0;
                res /= 5.0;
            }
        } else {
            res = (int) (res + 0.5);
        }
        return res;
    }
}
