package me.o_nix.assignments.currency_pairs.repositories;

import me.o_nix.assignments.currency_pairs.entities.Rate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RatesRepository extends CrudRepository<Rate, Long> {
  @Query("select r.measureTime, rt1, rt2 from #{#entityName} r "
      + "join r.rates rt1 "
      + "join r.rates rt2 "
        + "where key(rt1) = ?1 and key(rt2) = ?2 and r.measureTime between ?3 and ?4 "
        + "order by r.measureTime desc")
  List<Object[]> findRatesForTwoCurrenciesBetweenDates(String currency1, String currency2,
      LocalDateTime t1, LocalDateTime t2);

  @Query("select distinct key(r.rates) from #{#entityName} r")
  List<String> findAllAvailableCurrencies();
}
