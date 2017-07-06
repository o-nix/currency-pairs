package me.o_nix.assignments.currency_pairs.entities;

import com.google.common.collect.Maps;
import lombok.Data;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
public class Rate {
  @Id
  @GeneratedValue
  private Long id;

  private LocalDateTime measureTime;

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "currency_name")
  @Column(name = "rate", precision = 19, scale = 7)
  @CollectionTable(name = "rate_values",
      joinColumns = @JoinColumn(name = "id"),
      indexes = @Index(name = "currency_name_index", columnList = "currency_name"))
  private Map<String, BigDecimal> rates = Maps.newHashMap();
}
