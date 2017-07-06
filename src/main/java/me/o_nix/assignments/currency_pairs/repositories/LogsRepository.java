package me.o_nix.assignments.currency_pairs.repositories;

import me.o_nix.assignments.currency_pairs.entities.Log;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends CrudRepository<Log, Long> {
  @Query("select count(l) from #{#entityName} l where l.ip = ?1")
  long countRequestsByIp(String ip);
}
