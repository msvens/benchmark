package org.mellowtech.benchmark.io;

import org.mellowtech.core.io.RecordFile;
import org.mellowtech.core.io.impl.MemBlockFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author msvens
 * @since 30/03/16
 */
public class MemBlockFileBenchmark extends RecordFileBenchmark {

  Path fName;

  public MemBlockFileBenchmark(int blockSize, int numBlocks, Path dir, Optional<PrintWriter> w) {
    super("MemBlockFile", blockSize, numBlocks, dir, w);
    fName = dir.resolve("memblockfile.mbf");
  }

  @Override
  RecordFile createRecordFile() throws IOException{
    if(Files.exists(fName)){
      Files.delete(fName);
    }
    return new MemBlockFile(fName, blockSize, numBlocks, 0);
  }

  @Override
  RecordFile openRecordFile() throws IOException {
    return new MemBlockFile(fName, blockSize, numBlocks, 0);
  }
}
