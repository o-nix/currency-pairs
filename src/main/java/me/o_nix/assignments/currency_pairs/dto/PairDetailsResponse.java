package me.o_nix.assignments.currency_pairs.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PairDetailsResponse {
  private String base;
  private String quote;

  private List<Date> dates;
  private List<BigDecimal> values;
}
