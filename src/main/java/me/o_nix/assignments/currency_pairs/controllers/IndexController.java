package me.o_nix.assignments.currency_pairs.controllers;

import lombok.extern.slf4j.Slf4j;
import me.o_nix.assignments.currency_pairs.dto.JsonError;
import me.o_nix.assignments.currency_pairs.dto.PairDetailsResponse;
import me.o_nix.assignments.currency_pairs.services.RatesService;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@RestController
@RequestMapping(IndexController.API_URL_PREFIX)
@Slf4j
public class IndexController {
  protected static final String API_URL_PREFIX = "/api";
  public static final String API_PAIR_SUFFIX = "pair";
  public static final String API_PAIR_URL = API_URL_PREFIX + "/" + API_PAIR_SUFFIX;
  public static final String API_CURRENCIES_SUFFIX = "currencies";

  @Autowired
  private RatesService ratesService;

  @RequestMapping(value = API_PAIR_SUFFIX + "/{base}/{quote}")
  public PairDetailsResponse getRatesForDates(@PathVariable Currency base,
      @PathVariable Currency quote,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    if (from == null) {
      from = LocalDate.now()
          .minusMonths(1);
    }

    if (to == null) {
      to = LocalDate.now();
    }

    LocalDateTime fromDateTime = from.atStartOfDay();
    LocalDateTime toDateTime = to.atStartOfDay();

    return ratesService.getRates(base, quote, fromDateTime, toDateTime);
  }

  @RequestMapping(value = API_CURRENCIES_SUFFIX)
  public List<String> getAvailablePairNames() {
    return ratesService.getAvailableCurrencies();
  }

  @ExceptionHandler(TypeMismatchException.class)
  public ResponseEntity<JsonError> handleTypesException(TypeMismatchException e) {
    log.warn("Cannot convert user-provided values", e);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new JsonError("Wrong parameters"));
  }
}
