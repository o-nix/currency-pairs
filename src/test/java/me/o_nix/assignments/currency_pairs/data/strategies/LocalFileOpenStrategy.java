package me.o_nix.assignments.currency_pairs.data.strategies;

import lombok.SneakyThrows;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Primary
public class LocalFileOpenStrategy implements FileStrategy {
  @Override
  @SneakyThrows
  public InputStream getFile(String path) {
    InputStreamEditor resourceEditor = new InputStreamEditor();
    resourceEditor.setAsText(path);

    return (InputStream) resourceEditor.getValue();
  }
}
