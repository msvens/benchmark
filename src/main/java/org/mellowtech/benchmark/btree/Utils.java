package org.mellowtech.benchmark.btree;

import org.mellowtech.core.bytestorable.CBByte;
import org.mellowtech.core.bytestorable.CBByteArray;
import org.mellowtech.core.bytestorable.CBString;
import org.mellowtech.core.util.TGenerator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by msvens on 03/01/16.
 */
public class Utils {


  //Sorting/shuffling
  public static<T> void shuffle(T[] array){
    shuffle(ThreadLocalRandom.current(), array);
  }
  public static<T> void shuffle(Random r, T[] array){
    for(int i = array.length-1; i > 0; i--){
      int index = r.nextInt(i+1);
      T o = array[index];
      array[index] = array[i];
      array[i] = o;
    }
  }
  public static void shuffle(byte[] array){
    Random r = ThreadLocalRandom.current();
    for(int i = array.length-1; i > 0; i--){
      int index = r.nextInt(i+1);
      byte b = array[index];
      array[index] = array[i];
      array[i] = b;
    }
  }

  public static<T> void sort(T[] array){
    Arrays.sort(array);
  }

  public static byte[] randomBytes(int length){
    return TGenerator.randomStr(ThreadLocalRandom.current(), length, 'A', 'F').getBytes();
  }

  public static String randomString(int length){
    return TGenerator.randomStr(ThreadLocalRandom.current(), length, 'A', 'F');
  }

  //Generators:
  public static<T> T[] generate(Class<T> clazz, int keySize, int items){
    if(clazz == String.class){
      Iterator<String> iter = TGenerator.of(String.class, keySize, 'A', 'F', false);
      String[] strings = new String[items];
      for(int i = 0; i < items; i++){
        strings[i] = iter.next();
      }
      return (T[]) strings;
    } else if(clazz == CBString.class){
      Iterator<String> iter = TGenerator.of(String.class, keySize, 'A', 'F', false);
      CBString[] strings = new CBString[items];
      for(int i = 0; i < items; i++){
        strings[i] = new CBString(iter.next());
      }
      return (T[]) strings;
    } else if(clazz == byte[].class){
      Iterator<byte[]> iter = TGenerator.of(byte[].class, keySize, 'A', 'F', false);
      byte[][] strings = new byte[items][];
      for(int i = 0; i < items; i++){
        strings[i] = iter.next();
      }
      return (T[]) strings;
    } else if(clazz == CBByteArray.class) {
      Iterator<byte[]> iter = TGenerator.of(byte[].class, keySize, 'A', 'F', false);
      CBByteArray[] strings = new CBByteArray[items];
      for (int i = 0; i < items; i++) {
        strings[i] = new CBByteArray(iter.next());
      }
      return (T[]) strings;
    } else {
      throw new Error("could not gnerate of type: "+clazz.getName());
    }
  }

}
