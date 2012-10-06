package com.jeffwest.scanner.archive;

import com.jeffwest.scanner.Constants;
import com.jeffwest.scanner.reporting.DuplicateReportWriter;

import java.io.*;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: jeffrey.a.west
 * Date: Apr 3, 2010
 * Time: 8:47:31 AM
 */
public class ChecksumScanner
{
  public static final DecimalFormat SIZE_FORMAT = new DecimalFormat(Constants.SIZE_FORMAT_STRING);

  private ChecksumManager manager;

  public ChecksumScanner()
  {
    manager = new ChecksumManager();
  }

  public ChecksumScanner(ChecksumManager manager)
  {
    this.manager = manager;
    manager.resetDuplicates();
  }


  public static void main(String[] args)
  {

    long start = System.currentTimeMillis();

    final String sourceDir;

    if (args.length >= 1)
    {
      sourceDir = args[0];
      System.out.println("SourceDir=" + sourceDir);
    }
    else
    {
      //sourceDir = "/Users/jeffreyawest/Pictures/Downloaded Albums/102625320803188383633";
      sourceDir = "/Users/jeffreyawest/Pictures_temp/PIX-FINAL";
    }

    String destDir = null;

    if (args.length > 1)
    {
      destDir = args[1];
      System.out.println("DestDir=" + destDir);
    }

    final ChecksumManager manager = new ChecksumManager();
    manager.setDeleteDuplicates(getEnvironmentPropertyAsBoolean("deleteDuplicates", true));
    manager.setMoveDuplicates(getEnvironmentPropertyAsBoolean("moveDuplicates", false));
    manager.setUseExistingChecksums(getEnvironmentPropertyAsBoolean("useExistingChecksums", false));
    manager.setDuplicateDestination("/Users/jeffreyawest/Pictures_temp/PIX-Duplicates");

    try
    {
      manager.initDuplicateFileWriter(sourceDir + "/running_duplicates.csv");
      manager.initChecksumFileWriter(sourceDir + "/running_checksums.csv");
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    ChecksumScanner source = new ChecksumScanner(manager);
    source.scanDirectory(new File(sourceDir));



    DuplicateReportWriter.writeReport(manager);

    manager.close();

    System.out.println("Source Duplicates:: count=[" + source.manager.getDuplicateFileWrappers().size() + "] size=[" + SIZE_FORMAT.format(source.manager.getDuplicatesSpace()) + ']');


//    destDir="/Users/jeffreyawest/Pictures_temp/Combined_pictures";
//    destDir = null;

    if (destDir != null)
    {
      System.out.println("scanning destDir=" + destDir);
      ChecksumManager manager2 = new ChecksumManager(manager);
      manager2.setDeleteDuplicates(false);
      manager2.setMoveDuplicates(true);
      manager2.setDuplicateDestination("/Users/jeffreyawest/Pictures_temp/PIX-Duplicates");
      ChecksumScanner dest = new ChecksumScanner(manager2);

      try
      {
        manager2.initDuplicateFileWriter(destDir + "/duplicates.csv");
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
        System.exit(0);
      }

      dest.scanDirectory(new File(destDir));

    }
    else
    {
      System.out.println("Dest Dir not set.  Skipping...");
    }

    long stop = System.currentTimeMillis();

    System.out.println("Done Time Taken=" + (stop - start));
  }

  private static boolean getEnvironmentPropertyAsBoolean(String s, boolean b)
  {
    final String prop = System.getProperty(s, Boolean.toString(b));
    System.out.println("ENV [" + s + "]=[" + prop + ']');
    return Boolean.parseBoolean(prop);
  }


  private void scanDirectory(File file)
  {
    manager.processDirectory(file);
  }


  public ChecksumManager getManager()
  {
    return manager;
  }

  public void setManager(ChecksumManager manager)
  {
    this.manager = manager;
  }
}
