package me.o_nix.assignments.currency_pairs.listeners;


import com.google.common.collect.Maps;
import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.o_nix.assignments.currency_pairs.data.strategies.FileStrategy;
import me.o_nix.assignments.currency_pairs.services.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@Slf4j
public class HistoricalDataPuller implements ApplicationListener<ContextRefreshedEvent> {
  private static final String CSV_FILE_ROOT_PATH = "/eurofxref-hist.csv";
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter
      .ofPattern("yyy-MM-dd")
      .withZone(ZoneOffset.UTC);

  @PostConstruct
  public void onInit() {
    log.info(getClass().getName() + " initialized");
  }

  @Autowired
  private RatesService ratesService;

  @Value("${currency.historical-zip-url}")
  private String url;

  @Autowired
  private FileStrategy fileStrategy;

  @Override
  @SneakyThrows
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("Checking for data...");

    if (!ratesService.containsData()) {
      log.info("No data found, fetching...");

      ClassLoader classLoader = getClass().getClassLoader();
      InputStream ratesZipFileStream = fileStrategy.getFile(url);
      Path pathToSaveRatesZip = Files.createTempFile("rates", ".zip");
      Files.copy(ratesZipFileStream, pathToSaveRatesZip, StandardCopyOption.REPLACE_EXISTING);

      ratesZipFileStream.close();

      try (FileSystem fileSystem = FileSystems.newFileSystem(pathToSaveRatesZip, classLoader)) {
        Path historicalDataFile = fileSystem.getPath(CSV_FILE_ROOT_PATH);

        try (InputStream csvInputStream = Files.newInputStream(historicalDataFile)) {
          processCsv(csvInputStream);
        }
      }
    } else {
      log.info("Data is already present");
    }
  }

  @SneakyThrows
  @Transactional
  private void processCsv(InputStream inputStream) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    CSVIterator csvIterator = new CSVIterator(new CSVReader(reader));

    String[] titles = csvIterator.next(); // Skip headers

    while (csvIterator.hasNext()) {
      String[] values = csvIterator.next();
      Map<String, BigDecimal> rates = Maps.newHashMap();
      ZonedDateTime measureTimeUtc = Instant.now().atZone(ZoneOffset.UTC);

      rates.put(RatesService.BASE_CURRENCY.getCurrencyCode(), BigDecimal.ONE); // As the downloaded data has EUR base

      for (int i = 0; i < values.length; i++) {
        String value = values[i];

        if (i == 0) { // Date column
          LocalDate rowDate = LocalDate.parse(value, FORMATTER);
          measureTimeUtc = rowDate.atStartOfDay(ZoneOffset.UTC);
        } else { // Currency column
          String currencyName = titles[i];

          if (!StringUtils.isEmpty(currencyName)) {
            BigDecimal rateValue;

            try { // If "N/A" or missing
              rateValue = new BigDecimal(value);
            } catch (NumberFormatException e) {
              rateValue = BigDecimal.ZERO;
            }

            rates.put(currencyName, rateValue);
          }
        }
      }

      log.info("Saving data for date: " + measureTimeUtc);

      ratesService.saveRate(measureTimeUtc.toLocalDateTime(), rates);
    }

    log.info("Data import complete");
  }
}
