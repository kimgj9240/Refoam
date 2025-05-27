package com.example.refoam.service;

import java.util.List;
import java.util.Random;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.round;

interface ProductStandardConst {
    double MIN_MELT_TEMPERATURE = 106.027;
    double MAX_MELT_TEMPERATURE = 107.173;

    double MIN_MOLD_TEMPERATURE = 80.462;
    double MAX_MOLD_TEMPERATURE = 81.883;

//    double MIN_TIME_TO_FILL = 6.864;
//    double MAX_TIME_TO_FILL= 10.972;

    double MIN_PLASTICIZING_TIME = 3.16;
    double MAX_PLASTICIZING_TIME = 3.7;

    double MIN_CYCLE_TIME = 74.78;
    double MAX_CYCLE_TIME = 75.78;

    double MIN_CLOSING_FORCE = 900;
    double MAX_CLOSING_FORCE = 905.5;

    double MIN_CLAMPING_FORCE_PEAK = 915.6;
    double MAX_CLAMPING_FORCE_PEAK = 920.8;

    double MIN_TORQUE_PEAK= 113;
    double MAX_TORQUE_PEAK= 120.5;

    double MIN_TORQUE_MEAN= 88;
    double MAX_TORQUE_MEAN= 112;

    double MIN_BACK_PRESSURE_PEAK= 145;
    double MAX_BACK_PRESSURE_PEAK= 149;

    double MIN_INJ_PRESSURE_PEAK= 887;
    double MAX_INJ_PRESSURE_PEAK= 924; // 940 - linear 기준 불량품 30개중 1~5개

    double MIN_SCREW_POS_END_HOLD= 8.69;
    double MAX_SCREW_POS_END_HOLD= 8.99;

    double MIN_SHOT_VOLUME= 18.71;
    double MAX_SHOT_VOLUME= 18.77;

}
public class ProductStandardValue implements ProductStandardConst{
    public double getRandomValue(double min, double max){
        Random random = new Random();
        return round(random.nextDouble(min,max),3);
    }

    // fill만 리스트 중에서 랜덤 생성
    public double getRandomFill() {
        List<Double> fillValues = List.of(6.864, 6.968, 7.124, 7.228, 10.972);
        return fillValues.get(new Random().nextInt(fillValues.size()));
    }
}