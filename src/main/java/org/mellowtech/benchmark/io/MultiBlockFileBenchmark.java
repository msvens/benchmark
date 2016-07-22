package org.mellowtech.benchmark.io;

import org.mellowtech.core.io.RecordFile;
import org.mellowtech.core.io.impl.MemBlockFile;
import org.mellowtech.core.io.impl.MultiBlockFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author msvens
 * @since 30/03/16
 */
public class MultiBlockFileBenchmark extends RecordFileBenchmark {

  Path fName;

  public MultiBlockFileBenchmark(int blockSize, int numBlocks, Path dir, Optional<PrintWriter> w) {
    super("MultiBlockFile", blockSize, numBlocks, dir, w);
    fName = dir.resolve("multiblockfile.mbf");
  }

  @Override
  RecordFile createRecordFile() throws IOException{
    if(Files.exists(fName)){
      Files.delete(fName);
    }
    RecordFile rf = new MultiBlockFile(1024*1024*64, blockSize,0, fName);
    rf.clear();
    return rf;
  }

  @Override
  RecordFile openRecordFile() throws IOException {
    return new MultiBlockFile(1024*1024*64, blockSize,0,fName);
  }
}
