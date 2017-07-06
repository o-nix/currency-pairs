package me.o_nix.assignments.currency_pairs.data.strategies;

import java.io.InputStream;

public interface FileStrategy {
  InputStream getFile(String path);
}
