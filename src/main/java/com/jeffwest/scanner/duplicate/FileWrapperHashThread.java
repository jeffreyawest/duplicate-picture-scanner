package com.jeffwest.scanner.duplicate;

import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.scanner.Constants;
import com.jeffwest.scanner.duplicate.detection.DuplicateDetectionStrategy;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/11/12
 * Time: 11:16 AM
 */
public class FileWrapperHashThread implements Runnable
{
  private FileWrapper wrapper;
  private String hash;
  private boolean done;
  private long procesingTime;
  private FileSetManager manager;

  public static final boolean DEBUG = false;

  public FileWrapperHashThread(FileWrapper pWrapper, FileSetManager pManager)
  {
    wrapper = pWrapper;
    manager = pManager;
  }

  @Override
  public void run()
  {
    try
    {
      long start = System.currentTimeMillis();

      if (DEBUG)
      {
        System.out.println("Getting Hash: " + wrapper);
      }

      hash = manager.getDetectionStrategy().getHash(wrapper);
      long stop = System.currentTimeMillis();

      procesingTime += (stop - start);

      manager.addFile(wrapper, hash);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    done = true;
  }

  public String getHash()
  {
    return hash;
  }

  public void setHash(String pHash)
  {
    hash = pHash;
  }

  public boolean isDone()
  {
    return done;
  }

  public void setDone(boolean pDone)
  {
    done = pDone;
  }

  public long getProcesingTime()
  {
    return procesingTime;
  }
}
