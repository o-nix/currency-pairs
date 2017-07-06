package me.o_nix.assignments.currency_pairs.services;

import com.google.common.collect.Lists;
import me.o_nix.assignments.currency_pairs.dto.PairDetailsResponse;
import me.o_nix.assignments.currency_pairs.entities.Rate;
import me.o_nix.assignments.currency_pairs.repositories.RatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RatesService {
  public static final Currency BASE_CURRENCY = Currency.getInstance("EUR");

  @Autowired
  private RatesRepository ratesRepository;

  @Autowired
  private MoneyService moneyService;

  public boolean containsData() {
    return ratesRepository.count() > 0;
  }

  @Transactional
  public void saveRate(LocalDateTime measureDateTime, Map<String, BigDecimal> values) {
    Rate rate = new Rate();

    rate.setMeasureTime(measureDateTime);
    rate.setRates(values);

    Assert.state(values.entrySet().stream()
        .anyMatch(e -> Objects.equals(BASE_CURRENCY.getCurrencyCode(), e.getKey())
            && e.getValue().equals(BigDecimal.ONE)),
        String.format("%s is not the base currency!", BASE_CURRENCY)
    );

    values.entrySet().forEach(e ->
        e.setValue(moneyService.round(e.getValue())));

    ratesRepository.save(rate);
  }

  @Transactional(readOnly = true)
  public PairDetailsResponse getRates(Currency base, Currency quote, LocalDateTime d1, LocalDateTime d2) {
    String baseCode = base.getCurrencyCode();
    String quoteCode = quote.getCurrencyCode();

    List<Date> dates = Lists.newArrayList();
    List<BigDecimal> rates = Lists.newArrayList();

    for (Object[] values : ratesRepository.findRatesForTwoCurrenciesBetweenDates(baseCode,
        quoteCode, d1, d2)) {
      LocalDateTime date = (LocalDateTime) values[0];
      BigDecimal baseRate = (BigDecimal) values[1];
      BigDecimal quoteRate = (BigDecimal) values[2];
      BigDecimal fraction = moneyService.calculateCurrenciesRelativeValue(baseRate, quoteRate);

      dates.add(Date.from(date.toInstant(ZoneOffset.UTC)));
      rates.add(fraction);
    }

    PairDetailsResponse result = new PairDetailsResponse();

    result.setBase(baseCode);
    result.setQuote(quoteCode);
    result.setDates(dates);
    result.setValues(rates);

    return result;
  }

  @Transactional(readOnly = true)
  public List<String> getAvailableCurrencies() {
    return ratesRepository.findAllAvailableCurrencies();
  }
}
