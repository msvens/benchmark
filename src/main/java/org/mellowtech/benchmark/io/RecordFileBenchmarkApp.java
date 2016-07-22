package org.mellowtech.benchmark.io;

import org.mellowtech.benchmark.btree.BTreeBenchmark;
import org.mellowtech.benchmark.btree.Settings;
import org.mellowtech.benchmark.btree.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author msvens
 * @since 30/03/16
 */
public class RecordFileBenchmarkApp {

  public static void main(String[] args) throws Exception{
    int numItems = 1024*512;
    int blockSize = 1024*8;

    Path dir = Paths.get("/Users/msvens/benchmark/recordfiles");
    MultiBlockFileBenchmark mbfb = new MultiBlockFileBenchmark(blockSize,numItems,dir, Optional.empty());
    MemBlockFileBenchmark mbt = new MemBlockFileBenchmark(blockSize,numItems,dir, Optional.empty());
    mbfb.runTests(1);
    mbt.runTests(1);
  }
}
