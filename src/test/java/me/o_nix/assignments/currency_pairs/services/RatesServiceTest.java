package me.o_nix.assignments.currency_pairs.services;

import com.google.common.collect.Lists;
import me.o_nix.assignments.currency_pairs.dto.PairDetailsResponse;
import me.o_nix.assignments.currency_pairs.repositories.RatesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RatesServiceTest {
  private static final BigDecimal TWO = BigDecimal.valueOf(2);

  @Mock
  private RatesRepository ratesRepository;

  @Spy
  private MoneyService moneyService = new MoneyService();

  @InjectMocks
  private RatesService ratesService;

  @Before
  public void setUp() throws Exception {
    Object[] objects = {LocalDateTime.now(), BigDecimal.ONE, TWO};

    when(ratesRepository.findRatesForTwoCurrenciesBetweenDates(anyString(), anyString(),
        anyObject(), anyObject()))
        .thenReturn(Lists.<Object[]>newArrayList(objects));
  }

  @Test
  public void shoulFetchRates() {
    Currency base = Currency.getInstance("CHF");
    Currency quote = Currency.getInstance("EUR");

    PairDetailsResponse result = ratesService.getRates(base, quote, LocalDateTime.now(), LocalDateTime.now());

    assertNotNull(result);
    assertThat(result.getBase(), equalTo(base.getCurrencyCode()));
    assertThat(result.getQuote(), equalTo(quote.getCurrencyCode()));
    assertThat(result.getDates(), is(not(empty())));
    assertThat(result.getValues(), is(not(empty())));
    assertThat(result.getValues().get(0), equalTo(TWO));
    assertNotNull(result.getDates().get(0));
    assertThat(result.getDates().get(0), not(equalTo(0)));
  }
}