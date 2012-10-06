package com.jeffwest.scanner.duplicate;

import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.scanner.Constants;
import com.jeffwest.scanner.duplicate.detection.DuplicateDetectionStrategy;
import com.sun.org.apache.xml.internal.utils.StringVector;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 11:47 PM
 */
public class FileSetManager
    extends Observable
    implements Observer, Runnable
{
  public static final DecimalFormat SIZE_FORMAT = new DecimalFormat(Constants.SIZE_FORMAT_STRING);
  private static final Map<DuplicateDetectionStrategy, FileSetManager> fileSetManagers = new HashMap<>(13);

  private static final boolean THREADED = true;
  private static final boolean ASYNC = false;
  public static final int MAX_THREADS = 1;

  private final ArrayList<FileWrapper> toBeProcessed;
  private Map<String, FileSet> duplicateSetHashMap;
  private DuplicateDetectionStrategy detectionStrategy;
  private FileSetManagerObserver observer;

  private UUID myUUID;
  private long procesingTime = 0;
  private boolean keepGoing = true;
  private boolean working;
  private FileSet biggestFileSet;
  private FileSet mostDuplicatedFileSet;

  private long duplicateCount;
  private long duplicateSpace;

  private ThreadGroup hashThreadGroup;
  private long filesProcessed = 0;

  public FileSetManager()
  {
    myUUID = UUID.randomUUID();
    duplicateSetHashMap = new HashMap<>(6343);
    observer = new FileSetManagerObserver();
    this.addObserver(observer);
    toBeProcessed = new ArrayList<>(10);
    hashThreadGroup = new ThreadGroup("HashThreads");
  }

  public FileSetManager(final DuplicateDetectionStrategy detectionStrategy)
  {
    this();
    this.detectionStrategy = detectionStrategy;
  }

  public synchronized void addFile(final FileWrapper pWrapper, final String pHash)
  {
    processFileWrapper(pWrapper, pHash);
  }

  private void processFileWrapper(final FileWrapper wrapper, final String pHash)
  {
    filesProcessed++;

    if (pHash != null)
    {
      final FileSet set = getFileSet(pHash);
      set.addFile(wrapper);

      if (set.hasDuplicates())
      {
        duplicateCount++;
        duplicateSpace += wrapper.length();

        if (biggestFileSet == null || set.size() > biggestFileSet.size())
        {
          biggestFileSet = set;
          System.out.println("New Biggest File Set for Manager/Strategy=[" + detectionStrategy.getClass().getSimpleName()
                             + "] hash=[" + set.getHash()
                             + "] size=[" + set.size()
                             + "] space=[" + SIZE_FORMAT.format(set.getSpace())
                             + "] file=[" + set.getFile1() + ']');
        }

        if (mostDuplicatedFileSet == null || set.size() > mostDuplicatedFileSet.size())
        {
          mostDuplicatedFileSet = set;
          System.out.println("New Most Duplicated File Set for Manager/Strategy=[" + detectionStrategy.getClass().getSimpleName()
                             + "] hash=[" + set.getHash()
                             + "] size=[" + set.size()
                             + "] space=[" + SIZE_FORMAT.format(set.getSpace())
                             + "] file=[" + set.getFile1() + ']');
        }
      }

      setChanged();
      notifyObservers(wrapper);
    }
  }

  private void processFileWrapper(final FileWrapper wrapper)
  {
    String hash;

    try
    {
      long start = System.currentTimeMillis();
      hash = detectionStrategy.getHash(wrapper);
      long stop = System.currentTimeMillis();

      procesingTime += (stop - start);

      processFileWrapper(wrapper, hash);
//      System.out.println("Strategy=[" + detectionStrategy.getClass() + "] Hash=[" + hash + "] File=[" + wrapper.getTheFile().getName() + "]");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public FileSet getFileSet(final String hash)
  {
    FileSet theSet = duplicateSetHashMap.get(hash);

    if (theSet == null)
    {
      theSet = new FileSet();
      theSet.setHash(hash);

      duplicateSetHashMap.put(hash, theSet);
    }

    return theSet;
  }

  public Map<String, FileSet> getDuplicateSetHashMap()
  {
    return duplicateSetHashMap;
  }

  @Override
  public void update(final Observable o, final Object arg)
  {
//    System.out.println("Observable=[" + o.getClass() + "] arg=[" + arg.getClass() + "]");

    if (arg instanceof FileWrapper)
    {
      if (ASYNC)
      {
        processFileWrapperAsync((FileWrapper) arg);
      }
      else
      {
        processFileWrapper((FileWrapper) arg);
      }
    }
  }

  private void processFileWrapperThreaded(FileWrapper arg)
  {
    while (hashThreadGroup.activeCount() >= MAX_THREADS)
    {
      try
      {
        Thread.sleep(100);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }

    FileWrapperHashThread hashThread = new FileWrapperHashThread(arg, this);
    Thread theThread = new Thread(hashThreadGroup, hashThread);
    theThread.start();
  }

  private void processFileWrapperAsync(FileWrapper arg)
  {
    synchronized (toBeProcessed)
    {
      toBeProcessed.add(arg);
    }
  }

  public DuplicateDetectionStrategy getDetectionStrategy()
  {
    return detectionStrategy;
  }


  public Set<FileSet> getSizeOrderedDuplicateSet()
  {
    TreeSet<FileSet> set = new TreeSet<>(new Comparator<FileSet>()
    {
      @Override
      public int compare(FileSet o1, FileSet o2)
      {
        if (o1.getSpace() == o2.getSpace())
        {
          return 1;
        }

        return (int) (o2.getSpace() - o1.getSpace());
      }
    });

    for (FileSet fileSet : duplicateSetHashMap.values())
    {
      if (fileSet.hasDuplicates())
      {
        set.add(fileSet);
      }
    }

    return set;
  }

  public Set<FileSet> getAlphaOrderedDuplicateSet()
  {
    TreeSet<FileSet> set = new TreeSet<>(new Comparator<FileSet>()
    {
      @Override
      public int compare(FileSet o1, FileSet o2)
      {
        return o1.getHash().compareTo(o2.getHash());
      }
    });

    for (FileSet fileSet : duplicateSetHashMap.values())
    {
      if (fileSet.hasDuplicates())
      {
        set.add(fileSet);
      }
    }

    return set;
  }

  public String getUUID()
  {
    return myUUID.toString();
  }

  public long getDuplicatesSpace()
  {
    long space = 0;

    for (FileSet fileSet : duplicateSetHashMap.values())
    {
      if (fileSet.hasDuplicates())
      {
        space += fileSet.getSpace();
      }
    }

    return space;
  }

  public static FileSetManager getFileSetManager(DuplicateDetectionStrategy detectionStrategy)
  {
    FileSetManager manager = fileSetManagers.get(detectionStrategy);

    if (manager == null)
    {
      manager = new FileSetManager(detectionStrategy);
      fileSetManagers.put(detectionStrategy, manager);
    }

    return manager;
  }

  public long getProcesingTime()
  {
    return procesingTime;
  }

  public FileSetManagerObserver getObserver()
  {
    return observer;
  }

  @Override
  public void run()
  {
    log("Started!");
    try
    {
      synchronized (toBeProcessed)
      {
        log("Working...");
        working = true;

        if (toBeProcessed.size() > 0)
        {
          log("Files to be processed=[" + toBeProcessed.size() + ']');

          for (FileWrapper fileWrapper : toBeProcessed)
          {
            if (THREADED)
            {
              processFileWrapperThreaded(fileWrapper);
            }
            else
            {
              processFileWrapper(fileWrapper);
            }
          }

          toBeProcessed.clear();
        }
      }

      working = false;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    log("Done!");
  }

  public boolean isWorking()
  {
    return working;
  }

  private static void log(String message)
  {
    System.out.println(Thread.currentThread().getName() + ":: " + message);
  }

  public long getFilesProcessed()
  {
    return filesProcessed;
  }

  public long getDuplicateCount()
  {
    return duplicateCount;
  }

  public long getDuplicateSpace()
  {
    return duplicateSpace;
  }

  public long getFileSetSize()
  {
    return duplicateSetHashMap.size();
  }
}
