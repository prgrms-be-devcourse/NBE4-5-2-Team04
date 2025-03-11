package com.project2.domain.rank.enums;

import java.time.LocalDateTime;

public enum RankingPeriod {
    ONE_MONTH(1),
    THREE_MONTHS(3),
    SIX_MONTHS(6);

    private final int months;

    RankingPeriod(int months) {
        this.months = months;
    }

    public LocalDateTime getStartDate() {
        return LocalDateTime.now().minusMonths(months);
    }
}