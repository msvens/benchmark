package org.mellowtech.benchmark.io;

import com.google.common.base.Stopwatch;
import org.hsqldb.lib.StopWatch;
import org.mellowtech.benchmark.btree.Utils;
import org.mellowtech.core.io.Record;
import org.mellowtech.core.io.RecordFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author msvens
 * @since 28/03/16
 */
public abstract class RecordFileBenchmark {

  protected final int blockSize;
  protected final int numBlocks;
  protected final byte[] blockEven, blockOdd;
  protected final Integer[] records;
  protected final String name;
  protected final PrintWriter testOutput;
  protected final Stopwatch sw;
  protected final Path dir;

  public RecordFileBenchmark(String name, int blockSize, int numBlocks, Path dir, Optional<PrintWriter> w){
    this.blockSize = blockSize;
    this.numBlocks = numBlocks;
    this.name = name;
    this.blockEven = Utils.randomBytes(blockSize);
    this.blockOdd = Utils.randomBytes(blockSize);

    if(w.isPresent())
      testOutput = w.get();
    else
      testOutput = new PrintWriter(System.out);
    sw = Stopwatch.createUnstarted();
    records = new Integer[numBlocks];
    for(int i = 0; i < records.length; i++){
      records[i] = i;
    }
    Utils.shuffle(records);
    this.dir = dir;

  }

  abstract RecordFile createRecordFile() throws IOException;
  abstract RecordFile openRecordFile() throws IOException;

  public void runTests(int runs) throws IOException{
    writeHeader();
    RecordFile rf;
    for(int i = 0; i < runs; i++){
      rf = createRecordFile();
      writeRandom(rf, i);
      rf.remove();

      rf = createRecordFile();
      writeOrdered(rf, i);
      rf.close();

      rf = openRecordFile();
      readOrdered(rf, i);
      rf.close();

      rf = openRecordFile();
      readRandomTest(rf, i);
      rf.close();

      rf = openRecordFile();
      readRandom(rf, i);
      rf.close();

      rf = openRecordFile();
      updateRandom(rf, i);
      rf.close();

      rf = openRecordFile();
      iterate(rf, i);
      rf.close();

      rf = openRecordFile();
      size(rf, i);
      rf.close();

      rf = openRecordFile();
      deleteRandom(rf, i);
      rf.remove();

    }
  }

  protected void writeHeader(){
    testOutput.format("Name,Test,run,blockSize,numBlocks" +
        ",bytesRead mb,bytesWritten mb,ReadOps,WriteOps,Ops,Total time (millis),Operations/milli,Operations/sec,Throghuput (MB/Sec)%n");
    testOutput.flush();
  }

  /*protected void stopTest(String testName){
    stopTest(testName, numItems);
  }*/

  protected void stopTest(String testName, long readOps, long writeOps, int run) {
    sw.stop();
    long elapased = sw.elapsed(TimeUnit.MILLISECONDS);
    if (elapased == 0) elapased = 1;
    long secs = sw.elapsed(TimeUnit.SECONDS);
    if(secs == 0) secs = 1;
    long bytesRead = (readOps * blockSize) / (1024*1024);
    long bytesWritten = (writeOps * blockSize) / (1024*1024);
    long totalOps = readOps + writeOps;
    long opsPerM = totalOps / elapased;
    long opsPerS = opsPerM * 1000;
    long throughput = (bytesRead+bytesWritten) / secs;
    testOutput.format("%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d%n", name, testName, run,
        bytesRead, bytesWritten, readOps, writeOps, totalOps, elapased, opsPerM, opsPerS, throughput);
    testOutput.flush();
  }

  protected void startTest() {
    sw.reset();
    sw.start();
  }

  protected byte[] block(int record){
    return record % 2 == 0 ? blockEven : blockOdd;
  }

  protected boolean verify(byte[] block, int record){
    //check the first 200 bytes
    byte[] correct = block(record);
    for(int i = 0; i < 20; i++){
      if(correct[i] != block[i])
        return false;
    }
    return true;
  }

  protected void writeRandom(RecordFile rf, int run) throws IOException {
    startTest();
    for(int i : records){
      rf.insert(i, block(i));
    }
    rf.save();
    stopTest("writeRandom", 0, numBlocks, run);
  }

  protected void updateRandom(RecordFile rf, int run) throws IOException {
    startTest();
    for(int i : records){
      rf.update(i, block(i));
    }
    rf.save();
    stopTest("updateRandom", 0, numBlocks, run);
  }

  protected void writeOrdered(RecordFile rf, int run) throws IOException{
    startTest();
    for(int i = 0; i < numBlocks; i++){
      rf.insert(block(i));
    }
    rf.save();
    stopTest("writeOrdered", 0, numBlocks, run);
  }

  protected void readRandom(RecordFile rf, int run) throws IOException{
    startTest();
    byte[] b = new byte[1024];
    for(int i : records){
      rf.get(i, b);
    }
    rf.save();
    stopTest("readRandom", numBlocks, 0, run);
  }

  protected void size(RecordFile rf, int run) throws IOException {
    startTest();
    rf.size();
    rf.save();
    stopTest("size", 0,0,run);
  }

  protected void readRandomTest(RecordFile rf, int run) throws IOException{
    startTest();
    byte[] b = new byte[1024];
    for(int i : records){
      rf.get(i, b);
      if(!verify(b, i))
        throw new IOException("error");
    }
    stopTest("readRandomTest", numBlocks, 0, run);
    rf.save();
  }

  protected void readOrdered(RecordFile rf, int run) throws IOException{
    startTest();
    byte[] b = new byte[1024];
    for(int i = 0; i < numBlocks; i++){
      rf.get(i, b);
    }
    rf.save();
    stopTest("readOrdered", numBlocks, 0, run);
  }

  protected void iterate(RecordFile rf, int run) throws IOException{
    startTest();
    Iterator <Record> iter = rf.iterator();
    while(iter.hasNext()){
      iter.next();
    }
    rf.save();
    stopTest("iterate", numBlocks, 0, run);
  }

  protected void deleteRandom(RecordFile rf, int run) throws IOException{
    startTest();
    for(int i : records){
      if(rf.delete(i) != true)
        throw new IOException("error");
    }
    stopTest("deleteRandom", numBlocks, 0, run);
    rf.save();
  }








}
