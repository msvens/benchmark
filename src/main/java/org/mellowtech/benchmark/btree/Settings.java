package org.mellowtech.benchmark.btree;

/**
 * Created by msvens on 04/01/16.
 */
public class Settings {

  private boolean blobs = false;
  private boolean memMappedIndex = false;
  private boolean memMappedValues = false;
  private boolean cached = false;
  private boolean inMemory = false;

  public Settings blobs(boolean isBlobs){
    blobs = isBlobs;
    return this;
  }

  public boolean blobs(){return blobs;}

  public Settings memMappedIndex(boolean memMapped){
    this.memMappedIndex = memMapped;
    return this;
  }

  public boolean memMappedIndex(){
    return memMappedIndex;
  }

  public Settings memMappedValues(boolean memMapped){
    this.memMappedValues = memMapped;
    return this;
  }

  public boolean memMappedValues(){
    return memMappedValues;
  }

  public Settings cached(boolean isCached){
    this.cached = isCached;
    return this;
  }

  public boolean cached(){
    return cached;
  }

  public Settings inMemory(boolean isInMemory){
    this.inMemory = isInMemory;
    return this;
  }

  public boolean inMemory(){
    return inMemory;
  }
}
