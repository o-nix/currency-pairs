package me.o_nix.assignments.currency_pairs.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MoneyService {
  private static final int DEFAULT_ROUNDING = BigDecimal.ROUND_HALF_EVEN;

  public BigDecimal calculateCurrenciesRelativeValue(BigDecimal value1, BigDecimal value2) {
    if (value1.equals(BigDecimal.ZERO))
      return BigDecimal.ZERO;

    return value2.divide(value1, BigDecimal.ROUND_HALF_EVEN);
  }

  public BigDecimal round(BigDecimal value) {
    return value.setScale(7, DEFAULT_ROUNDING);
  }
}
