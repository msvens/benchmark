package org.mellowtech;

import org.mellowtech.core.collections.BTree;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        MaxIntTree mit = new MaxIntTree(10000000, true);
        mit.verify();
        //System.out.println(mit.tree.toString());
        //testRandom();
    }

  public static void testRandom() {
    Random r = new Random();
    Set<Integer> s = new HashSet<Integer>();
    int dups = 0;
    for(int i = 0; i < 800; i++) {
      int n = r.nextInt();
      if(s.contains(n)){
        //System.out.println("contains: "+n);
        dups++;
      }
      else s.add(n);
    }
    System.out.println("total dups: "+dups);
  }
}
