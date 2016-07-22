package org.mellowtech.benchmark.btree;

import org.mellowtech.core.bytestorable.CBByteArray;
import org.mellowtech.core.bytestorable.CBString;
import org.mellowtech.core.collections.BTree;
import org.mellowtech.core.collections.BTreeBuilder;
import org.mellowtech.core.collections.KeyValue;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

/**
 * Created by msvens on 04/01/16.
 */
public class BTreeBenchmark extends Benchmark {

  private BTree<String, CBString, byte[], CBByteArray> tree = null;
  private CBByteArray cbValue = new CBByteArray(value);

  public BTreeBenchmark(String[] keys, int keySize, int valueSize, Path benchmarkDir, Optional<PrintWriter> w,
                        Settings settings) {
    super("core.mellowtech.collections.BTree",
        keys, keySize, valueSize, benchmarkDir, w, settings);
  }

  private void newTree(){
    try {
      if (tree != null) tree.delete();
      BTreeBuilder builder = new BTreeBuilder().blobValues(settings.blobs()).memoryMappedValues(settings.memMappedValues());
      Path p = testPath().resolve("btreebenchmark");
      //tree = builder.build(CBString.class, CBByteArray.class, p);
      tree = builder.build(CBString.class, CBByteArray.class, p.getParent(), p.getFileName().toString());
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  public void close(){
    try{
      tree.close();
      tree = null;
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  @Override
  void writeSorted() {
    try {
      newTree();
      Utils.sort(keys);
      startTest();
      for (int i = 0; i < keys.length; i++) {
        tree.put(new CBString(keys[i]), cbValue);
      }
      stopTest("writeSorted");
      tree.save();
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void writeBatch() {
    try{
      newTree();
      Utils.sort(keys);
      startTest();
      tree.createTree(new Iterator<KeyValue<CBString, CBByteArray>>() {
        int curr = 0;
        @Override
        public boolean hasNext() {return curr < numItems;}
        @Override
        public KeyValue<CBString, CBByteArray> next() {
          return new KeyValue<>(new CBString(keys[curr++]), cbValue);
        }
      });
      stopTest("writeBatch");
      tree.save();
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void writeRandom() {
    try{
      newTree();
      Utils.shuffle(keys);
      startTest();
      for (int i = 0; i < keys.length; i++) {
        tree.put(new CBString(keys[i]), cbValue);
      }
      stopTest("writeRandom");
      tree.save();
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void readRandom() {
    try{
      Utils.shuffle(keys);
      startTest();
      for(int i = 0; i < keys.length; i++){
        tree.get(new CBString(keys[i]));
      }
      stopTest("readRandom");
    } catch(Exception e){
      throw new Error(e);
    }
  }

  @Override
  void readSequential() {
    try{
      startTest();
      Iterator <KeyValue<CBString, CBByteArray>> iter = tree.iterator();
      while(iter.hasNext()){
        iter.next();
      }
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
      CBByteArray val1 = new CBByteArray(val);
      startTest();
      for (int i = 0; i < keys.length; i++) {
        tree.put(new CBString(keys[i]), val1);
      }
      stopTest("updateRandom");
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  @Override
  void updateRandomNewSize() {

  }

  @Override
  void deleteRandom() {

  }
}
