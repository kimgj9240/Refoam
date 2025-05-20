package com.example.refoam.service;

import java.util.Random;
interface ProductStandardConst {
    final double MIN_MELT_TEMPERATURE = 105.900;
    final double MAX_MELT_TEMPERATURE = 107.989;

    final double MIN_MOLD_TEMPERATURE = 81.012;

    final double MAX_MOLD_TEMPERATURE = 81.918;

    final double MIN_TIME_TO_FILL = 6.206;

    final double MAX_TIME_TO_FILL= 7.177;

    public static final double MIN_PLASTICIZING_TIME = 2.77;

    public static final double MAX_PLASTICIZING_TIME = 4.14;

    public static final double MIN_CYCLE_TIME = 74.78;

    public static final double MAX_CYCLE_TIME = 75.81;

    public static final double MIN_CLOSING_FORCE = 883.3;

    public static final double MAX_CLOSING_FORCE = 921.9;

    public static final double MIN_CLAMPING_FORCE_PEAK = 892.7;

    public static final double MAX_CLAMPING_FORCE_PEAK = 936.6;

    public static final double MIN_TORQUE_PEAK= 108.6;

    public static final double MAX_TORQUE_PEAK= 126.7;

    public static final double MIN_TORQUE_MEAN= 80.2;

    public static final double MAX_TORQUE_MEAN= 109.7;

    public static final double MIN_BACK_PRESSURE_PEAK= 144.801;

    public static final double MAX_BACK_PRESSURE_PEAK= 150.206;

    public static final double MIN_INJ_PRESSURE_PEAK= 846.65;

    public static final double MAX_INJ_PRESSURE_PEAK= 925.71;

    public static final double MIN_SCREW_POS_END_HOLD= 8.441;

    public static final double MAX_SCREW_POS_END_HOLD= 9.037;

    public static final double MIN_SHOT_VOLUME= 18.511;

    public static final double MAX_SHOT_VOLUME= 19.229;

}
public class ProductStandardValue implements ProductStandardConst{
    public double getRandomValue(double min, double max){
        Random random = new Random();
        return random.nextDouble(min,max);
    }
}

