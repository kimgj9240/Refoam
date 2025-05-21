package com.example.refoam.service;

import java.util.Random;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.round;

interface ProductStandardConst {
    double MIN_MELT_TEMPERATURE = 105.900;
    double MAX_MELT_TEMPERATURE = 107.989;

    double MIN_MOLD_TEMPERATURE = 81.012;

    double MAX_MOLD_TEMPERATURE = 81.918;

    double MIN_TIME_TO_FILL = 6.206;

    double MAX_TIME_TO_FILL= 7.177;

    double MIN_PLASTICIZING_TIME = 2.77;

    double MAX_PLASTICIZING_TIME = 4.14;

    double MIN_CYCLE_TIME = 74.78;

    double MAX_CYCLE_TIME = 75.81;

    double MIN_CLOSING_FORCE = 883.3;

    double MAX_CLOSING_FORCE = 921.9;

    double MIN_CLAMPING_FORCE_PEAK = 892.7;

    double MAX_CLAMPING_FORCE_PEAK = 936.6;

    double MIN_TORQUE_PEAK= 108.6;

    double MAX_TORQUE_PEAK= 126.7;

    double MIN_TORQUE_MEAN= 80.2;

    double MAX_TORQUE_MEAN= 109.7;

    double MIN_BACK_PRESSURE_PEAK= 144.801;

    double MAX_BACK_PRESSURE_PEAK= 150.206;

    double MIN_INJ_PRESSURE_PEAK= 846.65;

    double MAX_INJ_PRESSURE_PEAK= 925.71;

    double MIN_SCREW_POS_END_HOLD= 8.441;

    double MAX_SCREW_POS_END_HOLD= 9.037;

    double MIN_SHOT_VOLUME= 18.511;

    double MAX_SHOT_VOLUME= 19.229;

}
public class ProductStandardValue implements ProductStandardConst{
    public double getRandomValue(double min, double max){
        Random random = new Random();
        return round(random.nextDouble(min,max),3);
    }
}