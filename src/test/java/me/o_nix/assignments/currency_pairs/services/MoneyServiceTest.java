package me.o_nix.assignments.currency_pairs.services;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class MoneyServiceTest {
  private MoneyService moneyService = new MoneyService();

  @Test
  public void shouldNotFallWithArithmeticExceptions() throws Exception {
    BigDecimal actual = moneyService.calculateCurrenciesRelativeValue(BigDecimal.ZERO, BigDecimal.ZERO);

    assertEquals(BigDecimal.ZERO, actual);
  }

  @Test
  public void shouldCalculateProperly() throws Exception {
    BigDecimal expectedValue = new BigDecimal("2");
    BigDecimal actual = moneyService.calculateCurrenciesRelativeValue(BigDecimal.ONE, expectedValue);

    assertEquals(expectedValue, actual);
  }
}