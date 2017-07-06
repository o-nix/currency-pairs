package me.o_nix.assignments.currency_pairs.dto;

import lombok.Data;

@Data
public class JsonError {
  private String type = "error";
  private String reason;

  public JsonError(String reason) {
    this.reason = reason;
  }
}
