package com.example.refoam.service;

import com.example.refoam.domain.ProductLabel;
import com.example.refoam.domain.Standard;
import org.springframework.stereotype.Component;

@Component
public class StandardEvaluator {
    public ProductLabel evaluate(double screw, double mold, double injpress, double fill) {
        // 특정 값에 따른 라벨 부여
        if (screw > 8.835) return ProductLabel.ERR_POS_021;
        if (injpress > 915) return ProductLabel.ERR_PRS_021;
        if ((mold > 81.5 || mold < 80.5)
                && fill > 6.864) return ProductLabel.ERR_TEMP_021;

        return ProductLabel.OK;
    }
}
