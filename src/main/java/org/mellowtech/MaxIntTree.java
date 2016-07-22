package org.mellowtech;

import com.google.common.base.Stopwatch;
import org.mellowtech.core.bytestorable.CBInt;
import org.mellowtech.core.collections.BTree;
import org.mellowtech.core.collections.BTreeBuilder;
import org.mellowtech.core.collections.KeyValue;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by msvens on 19/10/15.
 */
public class MaxIntTree {

  Random r = new Random();
  char[] chars = "abcdefghijklmnop1234567890".toCharArray();
  int arrLength = chars.length;
  long numItems;

  protected String generate(int maxLength) {
    int strLength = r.nextInt(maxLength) + 1;
    StringBuilder sbuilder = new StringBuilder();
    for(int i = 0; i < strLength; i++){
      sbuilder.append(chars[r.nextInt(arrLength)]);
    }
    return sbuilder.toString();
  }

  public BTree<Integer, CBInt, Integer, CBInt> tree;

  public MaxIntTree(long numItems, boolean batch) throws Exception{
    tree = new BTreeBuilder().memoryMappedIndex(true).memoryMappedValues(true).build(CBInt.class, CBInt.class, "/Users/msvens/treetests/"+numItems+"Tree");
    this.numItems = numItems;
    if(batch)
      fillBatch();
    else
      fill();
    //tree.save();
    //tree.delete();
  }

  public void verify() throws Exception {
    for(int i = 0; i < numItems; i++){
      if(i % 1000000 == 0){
        System.out.println("verified: "+i+" items");
      }
      CBInt v = tree.get(new CBInt(i));
      if(v == null || v.value() != i){
        System.out.println("could not find: "+i);
      }
    }

  }

  private void fillBatch() throws Exception {
    Stopwatch sw = Stopwatch.createStarted();
    tree.createTree(new Iterator<KeyValue<CBInt, CBInt>>() {
      int curr = 0;
      @Override
      public boolean hasNext() {return curr < numItems;}

      @Override
      public KeyValue<CBInt, CBInt> next() {
        curr++;
        if(curr % 1000000 == 0){
          System.out.println(curr+" "+sw.elapsed(TimeUnit.SECONDS));
        }
        return new KeyValue(new CBInt(curr-1), new CBInt(curr-1));
      }
    });
    System.out.println("time "+sw.elapsed(TimeUnit.SECONDS));
    System.out.println("inserts per milli: "+ numItems / sw.elapsed(TimeUnit.MILLISECONDS));
    long size = 8 * numItems;
    System.out.println("size in megs of input data (MB): "+size/(1024*1024));
  }

  private void fill() throws Exception{
    //CBInt keyTemp = new CBInt();
    //CBInt valTemp = new CBInt();
    Stopwatch sw = Stopwatch.createStarted();
    for(int i = 0; i < numItems; i++){
      if(i % 1000000 == 0){
        System.out.println(i+" "+sw.elapsed(TimeUnit.SECONDS));
      }
      tree.put(new CBInt(i), new CBInt(i));
      if(tree.get(new CBInt(i)) == null){
        System.out.println("could not find after put: "+i);
      }
    }
    sw.stop();
    System.out.println("time "+sw.elapsed(TimeUnit.SECONDS));
    System.out.println("inserts per milli: "+ numItems / sw.elapsed(TimeUnit.MILLISECONDS));
    long size = 8 * numItems;
    System.out.println("size in megs of input data: "+size/(1024*1024));
    //System.out.println(tree.toString());
  }

  private void fillRandom() throws Exception{
    CBInt keyTemp = new CBInt();
    CBInt valTemp = new CBInt();
    Stopwatch sw = Stopwatch.createStarted();
    for(int i = 0; i < numItems; i++){
      if(i % 1000000 == 0){
        System.out.println(i+" "+sw.elapsed(TimeUnit.SECONDS));
      }
      tree.put(new CBInt(r.nextInt()), new CBInt(r.nextInt()));
    }
    sw.stop();
    System.out.println("time "+sw.elapsed(TimeUnit.SECONDS));
    System.out.println("inserts per milli: "+ numItems / sw.elapsed(TimeUnit.MILLISECONDS));
    long size = 8 * numItems;
    System.out.println("size in megs of input data: "+size/(1024*1024));
    //System.out.println(tree.toString());
  }

}
