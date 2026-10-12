package com.example.idcard.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 统计结果：总数、男女数量与占比、年龄段分布。
 */
public record StatsResponse(
        long total,
        long maleCount,
        long femaleCount,
        long otherCount,
        BigDecimal maleRatio,
        BigDecimal femaleRatio,
        List<AgeGroup> ageGroups
) {

    public record AgeGroup(String label, long count) {
    }
}
