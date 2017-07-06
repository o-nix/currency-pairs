package me.o_nix.assignments.currency_pairs.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@Entity
public class Log {
  @Id
  @GeneratedValue
  private Long id;

  private LocalDateTime accessed = Instant.now()
      .atZone(ZoneOffset.UTC)
      .toLocalDateTime();

  private String pair;

  @Lob
  private String rates;

  private String ip;
}
