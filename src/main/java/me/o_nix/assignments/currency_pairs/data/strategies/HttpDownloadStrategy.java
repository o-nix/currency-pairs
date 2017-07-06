package me.o_nix.assignments.currency_pairs.data.strategies;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

@Component
@Scope("prototype")
@Slf4j
public class HttpDownloadStrategy implements FileStrategy {
  @Autowired
  private RestTemplate restTemplate;

  private InputStream inputStream;

  @Override
  @SneakyThrows
  public InputStream getFile(String path) {
    Path zipDownloadPath = Files.createTempFile("download", ".zip");
    ResponseExtractor<Void> responseExtractor = response -> {
      Files.copy(response.getBody(), zipDownloadPath, StandardCopyOption.REPLACE_EXISTING);

      log.info("Downloading complete");

      inputStream = Files.newInputStream(zipDownloadPath, StandardOpenOption.DELETE_ON_CLOSE);
      return null;
    };

    log.info("Downloading data file...");

    restTemplate.execute(URI.create(path), HttpMethod.GET, null, responseExtractor);

    return inputStream;
  }
}
