package me.o_nix.assignments.currency_pairs.services;

import lombok.extern.slf4j.Slf4j;
import me.o_nix.assignments.currency_pairs.dto.PairDetailsResponse;
import me.o_nix.assignments.currency_pairs.entities.Log;
import me.o_nix.assignments.currency_pairs.repositories.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.stream.Collectors;

@Service
@Slf4j
public class LoggingService {
  @Autowired
  private LogsRepository logsRepository;

  @Transactional
  public long logInteraction(String ip, String pair) {
    Log log = new Log();

    log.setIp(ip);
    log.setPair(pair);

    return logsRepository
        .save(log)
        .getId();
  }

  @Transactional
  public void updateInteractionDetails(long id, PairDetailsResponse details) {
    Assert.isTrue(id > 0, "You should provide Log ID when performing update");
    Log log = logsRepository.findOne(id);

    Assert.notNull(log, "No Log entry with this ID");

    String ratesString = details.getValues().stream()
        .map(String::valueOf)
        .collect(Collectors.joining(", "));

    log.setRates(ratesString);
  }

  @Transactional(readOnly = true)
  public long countRequestsFromIp(String ip) {
    return logsRepository.countRequestsByIp(ip);
  }
}
