package org.mellowtech.benchmark.btree;

import com.google.common.base.Stopwatch;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by msvens on 04/01/16.
 */
public abstract class Benchmark {

  protected PrintWriter testOutput = null;
  protected int numItems;
  protected int keySize;
  protected int valueSize;
  protected String name;
  protected Path path;
  protected String[] keys;
  protected byte[] value;
  protected Random r = ThreadLocalRandom.current();
  protected Stopwatch sw;
  protected Settings settings;


  protected Benchmark(String name, String[] keys, int keySize, int valueSize, Path benchmarkDir, Optional<PrintWriter> w,
                      Settings settings){
    this.settings = settings;
    this.path = benchmarkDir;
    this.name = name;
    this.numItems = keys.length;
    this.keySize = keySize;
    this.valueSize = valueSize;
    if(w.isPresent()){
      testOutput = w.get();
    } else {
      testOutput = new PrintWriter(System.out);
    }
    this.keys = keys;
    value = Utils.randomBytes(valueSize);
    sw = Stopwatch.createUnstarted();
  }

  protected Path testPath(){
    return path;
  }

  protected void writeHeader(){
    testOutput.format("Name,Test,MemMappedIndex,MemMappedValues,blobs,cached,inMemory,Key size,Value Size,Operations,Total time (millis),Operations/milli,Operations/sec%n");
    testOutput.flush();
  }

  protected void stopTest(String testName){
    stopTest(testName, numItems);
  }
  protected void stopTest(String testName, int operations) {
    sw.stop();
    long elapased = sw.elapsed(TimeUnit.MILLISECONDS);
    if (elapased == 0) elapased = 1;
    long opsPerM = operations / elapased;
    testOutput.format("%s,%s,%b,%b,%b,%b,%b,%d,%d,%d,%d,%d,%d%n", name, testName,
        settings.memMappedIndex(), settings.memMappedValues(), settings.blobs(), settings.cached(), settings.inMemory(),
        keySize, valueSize, operations, sw.elapsed(TimeUnit.MILLISECONDS), opsPerM, opsPerM * 1000);
    testOutput.flush();
  }

  protected void startTest() {
    sw.reset();
    sw.start();
  }

  public void runBenchmark(int numTimes){
    writeHeader();
    for(int i = 0; i < numTimes; i++) {
      writeSorted();
      writeBatch();
      writeRandom();
      readRandom();
      readSequential();
      updateRandom();
      //updateRandomNewSize();
      //deleteRandom();
    }
  }

  //write tests
  abstract void writeSorted();
  abstract void writeBatch();
  abstract void writeRandom();

  //read tests
  abstract void readRandom();
  abstract void readSequential();

  //update tests
  abstract void updateRandom();
  abstract void updateRandomNewSize();

  //delete tests
  abstract void deleteRandom();




}
