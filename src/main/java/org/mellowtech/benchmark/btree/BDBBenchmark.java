package org.mellowtech.benchmark.btree;

import com.sleepycat.je.*;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author msvens
 * @since 05/01/16
 */
public class BDBBenchmark extends Benchmark{


  private Environment env;
  private Path dbPath;
  private Database db;

  public BDBBenchmark(String[] keys, int keySize, int valueSize, Path benchmarkDir,
                      Optional<PrintWriter> w, Settings settings) {
    super("com.sleepycat.je.Database", keys, keySize, valueSize, benchmarkDir, w, settings);
    dbPath = testPath().resolve("bdb");
    try {
      Files.createDirectories(dbPath);
    } catch(Exception e){
      throw new Error(e);
    }
    createEnvironment();
  }

  void createEnvironment(){
    try{
      EnvironmentConfig ec = new EnvironmentConfig();
      ec.setAllowCreate(true);
      ec.setConfigParam(EnvironmentConfig.LOG_FILE_MAX,""+(1024*1024*64));
      env = new Environment(dbPath.toFile(),ec);
    } catch(Exception e){
      throw new Error(e);
    }
  }

  void newDatabase(){
    if(db != null){
      db.close();
      env.removeDatabase(null, "bdbTest");
    }
    DatabaseConfig dc = new DatabaseConfig();
    dc.setAllowCreate(true);
    db = env.openDatabase(null, "bdbTest", dc);
  }

  void close(){
    try{
      if(db != null) db.close();
      if(env != null) env.close();
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void writeSorted() {
    newDatabase();
    try {
      Utils.sort(keys);
      startTest();
      for (int i = 0; i < numItems; i++) {
        DatabaseEntry key = new DatabaseEntry(keys[i].getBytes(StandardCharsets.UTF_8));
        DatabaseEntry data = new DatabaseEntry(value);
        db.put(null, key, data);
      }
      stopTest("writeSorted");
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void writeBatch() {
    //startTest();
    //stopTest("writeBatch-no test");
  }

  @Override
  void writeRandom() {
    newDatabase();
    try{
      Utils.shuffle(keys);
      startTest();
      for(int i = 0; i < numItems; i++){
        DatabaseEntry key = new DatabaseEntry(keys[i].getBytes(StandardCharsets.UTF_8));
        DatabaseEntry data = new DatabaseEntry(value);
        db.put(null, key, data);
      }
      stopTest("writeRandom");
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void readRandom() {
    try{
      Utils.shuffle(keys);
      startTest();
      for(int i = 0; i < numItems; i++){
        DatabaseEntry key = new DatabaseEntry(keys[i].getBytes(StandardCharsets.UTF_8));
        DatabaseEntry value = new DatabaseEntry();
        db.get(null, key, value, LockMode.DEFAULT);
        /*if(value.getData().length != valueSize){
          System.out.println("error");
        }*/
      }
      stopTest("readRandom");
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void readSequential() {
    try{
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry value = new DatabaseEntry();
      startTest();
      Cursor c = db.openCursor(null, null);
      int i = 0;
      while(c.getNext(key,value,LockMode.DEFAULT) != OperationStatus.NOTFOUND){
        i++;
      }
      System.out.println(i);
      c.close();
      stopTest("readSequential");
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void updateRandom() {
    try {
      byte[] val = Arrays.copyOf(value, value.length);
      Utils.shuffle(val);
      Utils.shuffle(keys);
      startTest();
      for(int i = 0; i < numItems; i++){
        DatabaseEntry key = new DatabaseEntry(keys[i].getBytes(StandardCharsets.UTF_8));
        DatabaseEntry data = new DatabaseEntry(val);
        db.put(null, key, data);
      }
      stopTest("updateRandom");
    }catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void updateRandomNewSize() {

  }

  @Override
  void deleteRandom() {

  }
}
