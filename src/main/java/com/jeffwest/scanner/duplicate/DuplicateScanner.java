package com.jeffwest.scanner.duplicate;

import com.jeffwest.scanner.RecursiveFileScannerObservable;
import com.jeffwest.scanner.duplicate.detection.*;
import com.jeffwest.scanner.duplicate.resolution.DeleteDuplicateStrategy;
import com.jeffwest.scanner.duplicate.resolution.DisplayDuplicateSet;
import com.jeffwest.scanner.duplicate.resolution.DuplicateResolutionStrategy;
import com.jeffwest.scanner.duplicate.resolution.MarkDuplicateStrategy;
import com.jeffwest.scanner.reporting.DuplicateReportWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 11:10 PM
 */
public class DuplicateScanner implements Runnable
{
  private List<DuplicateDetectionStrategy> detectionStrategies;
  private List<DuplicateResolutionStrategy> resolutionStrategies;
  private String startDir;
  private HashMap<DuplicateDetectionStrategy, FileSetManager> fileSetManagers;
  private RecursiveFileScannerObservable scannerObservable;
  private ThreadGroup managerThreadGroup = new ThreadGroup("Managers");


  public static void main(String[] args)
  {
    DuplicateScanner scanner = new DuplicateScanner();

//    scanner.addDetectionStrategy(new FilePrefixStrategy());
    scanner.addDetectionStrategy(new JPEGImageChecksumStrategy());
//    scanner.addDetectionStrategy(new JPEGFileCreatedDateStrategy());

//    scanner.addDetectionStrategy(new MD5FileChecksumStrategy());
    scanner.addDetectionStrategy(new CRC32FileChecksumStrategy());
//    scanner.addDetectionStrategy(new FileCreationDateStrategy());
//    scanner.addDetectionStrategy(new FilenameStrategy());

//    scanner.setResolutionStrategy(new DisplayDuplicateSet());
    scanner.addResolutionStrategy(new DeleteDuplicateStrategy());

//    scanner.setStartDir("/Users/jeffreyawest/Pictures_temp/Combined_pictures");
//    scanner.setStartDir("/Users/jeffreyawest/Pictures/PIX-FINAL/");
//    scanner.startDir = "/Users/jeffreyawest/Pictures/PIX-FINAL";
    scanner.startDir = "/Users/jeffreyawest/Pictures/PIX-FINAL/";
//    scanner.startDir = "/Users/jeffreyawest/Pictures/";

    long start = System.currentTimeMillis();
    scanner.perform();
    long stop = System.currentTimeMillis();

    System.out.println("Running time: " + (stop - start));
  }

  private void addResolutionStrategy(DuplicateResolutionStrategy pStrategy)
  {
    resolutionStrategies.add(pStrategy);
  }

  public DuplicateScanner()
  {
    scannerObservable = new RecursiveFileScannerObservable();
    detectionStrategies = new ArrayList<>(13);
    resolutionStrategies = new ArrayList<>(13);
    fileSetManagers = new HashMap<>(detectionStrategies.size());
  }

  public FileSetManager addDetectionStrategy(DuplicateDetectionStrategy strategy)
  {
    FileSetManager setManager = FileSetManager.getFileSetManager(strategy);
    fileSetManagers.put(strategy, setManager);

    detectionStrategies.add(strategy);
    scannerObservable.addObserver(setManager);

    return setManager;
  }

  public void perform()
  {
    long start = System.currentTimeMillis();

    // scan directories and detect duplicates
    scannerObservable.scanDirectory(startDir);

    startManagers();

    long numTimesSlept = 0;

    while (managerThreadGroup.activeCount() > 0)
    {
      if (numTimesSlept % 60 == 1)
      {
        managerThreadGroup.list();
      }

      try
      {
        Thread.sleep(500);
        numTimesSlept++;
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }

    long stop = System.currentTimeMillis();

    System.out.println("Time = " + (stop - start));

    // write log/report
    DuplicateReportWriter.writeReport(fileSetManagers.values());
    resolveDuplicates();
  }

  private void resolveDuplicates()
  {
    for (DuplicateResolutionStrategy strategy : resolutionStrategies)
    {
      strategy.resolveDuplicates(fileSetManagers.values());
    }
  }

  public void startManagers()
  {
    for (FileSetManager manager : fileSetManagers.values())
    {
      String threadName = manager.getDetectionStrategy().getClass().getSimpleName() + "-Manager";
      Thread thread = new Thread(managerThreadGroup, manager, threadName);

      thread.start();
    }
  }

  public String getStartDir()
  {
    return startDir;
  }

  public void setStartDir(String startDir)
  {
    this.startDir = startDir;
  }

  public Collection<FileSetManager> getManagers()
  {
    return fileSetManagers.values();
  }

  public RecursiveFileScannerObservable getScannerObservable()
  {
    return scannerObservable;
  }

  @Override
  public void run()
  {
    long start = System.currentTimeMillis();
    perform();
    long stop = System.currentTimeMillis();

    System.out.println("Running time: " + (stop - start));
  }
}
