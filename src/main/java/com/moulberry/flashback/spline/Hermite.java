package com.moulberry.flashback.spline;

import net.minecraft.util.Mth;
import org.apache.commons.math3.analysis.interpolation.HermiteInterpolator;
import org.joml.Vector3d;

import java.util.Map;

public class Hermite {

    public static Vector3d position(Map<Integer, Vector3d> map, float amount) {
        HermiteInterpolator hermiteInterpolatorX = new HermiteInterpolator();
        HermiteInterpolator hermiteInterpolatorY = new HermiteInterpolator();
        HermiteInterpolator hermiteInterpolatorZ = new HermiteInterpolator();

        double[] array = new double[1];
        for (Map.Entry<Integer, Vector3d> entry : map.entrySet()) {
            array[0] = entry.getValue().x;
            hermiteInterpolatorX.addSamplePoint(entry.getKey(), array);

            array[0] = entry.getValue().y;
            hermiteInterpolatorY.addSamplePoint(entry.getKey(), array);

            array[0] = entry.getValue().z;
            hermiteInterpolatorZ.addSamplePoint(entry.getKey(), array);
        }

        return new Vector3d(
            hermiteInterpolatorX.value(amount)[0],
            hermiteInterpolatorY.value(amount)[0],
            hermiteInterpolatorZ.value(amount)[0]
        );
    }

    public static double value(Map<Integer, Double> map, float amount) {
        HermiteInterpolator hermiteInterpolator = new HermiteInterpolator();

        double[] array = new double[1];
        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            array[0] = entry.getValue();
            hermiteInterpolator.addSamplePoint(entry.getKey(), array);
        }

        return hermiteInterpolator.value(amount)[0];
    }

    public static double degrees(Map<Integer, Double> map, float amount) {
        HermiteInterpolator hermiteInterpolator = new HermiteInterpolator();

        double lastAngle = 0.0;

        double[] array = new double[1];
        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            double angle = lastAngle + Mth.wrapDegrees(entry.getValue() - lastAngle);
            array[0] = angle;

            hermiteInterpolator.addSamplePoint(entry.getKey(), array);

            lastAngle = angle;
        }

        return Mth.wrapDegrees(hermiteInterpolator.value(amount)[0]);
    }

}