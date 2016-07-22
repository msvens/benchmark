package org.mellowtech.benchmark.btree;

import org.mellowtech.core.collections.impl.HybridTree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by msvens on 04/01/16.
 */
public class BenchmarkApp {

  public static void main(String[] args){
    int numItems = 5000000;
    Path dir = Paths.get("/Users/msvens/benchmark");
    String[] keys = Utils.generate(String.class, 16, numItems);
    Settings settings = new Settings().blobs(false).memMappedIndex(true).memMappedValues(true);
    /*BDBBenchmark bdb = new BDBBenchmark(keys, 16, 100, dir, Optional.empty(), settings);
    bdb.runBenchmark(2);
    bdb.close();*/
    BTreeBenchmark bm = new BTreeBenchmark(keys, 16, 100, dir, Optional.empty(), settings);
    bm.runBenchmark(2);
    bm.close();
    /*ybridTreeBenchmark htb = new HybridTreeBenchmark(keys, 16, 100, dir, Optional.empty(), settings);
    htb.runBenchmark(2);
    htb.close();*/
  }
}
