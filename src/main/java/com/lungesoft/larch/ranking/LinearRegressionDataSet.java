package com.lungesoft.larch.ranking;

public class LinearRegressionDataSet {
    private double[][] x;
    private double[] y;

    public LinearRegressionDataSet() {
    }

    public LinearRegressionDataSet(double[][] x, double[] y) {
        this.x = x;
        this.y = y;
    }

    public double[][] getX() {
        return x;
    }

    public void setX(double[][] x) {
        this.x = x;
    }

    public double[] getY() {
        return y;
    }

    public void setY(double[] y) {
        this.y = y;
    }
}
