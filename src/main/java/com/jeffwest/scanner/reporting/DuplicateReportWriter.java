package com.jeffwest.scanner.reporting;

import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.scanner.archive.ChecksumManager;
import com.jeffwest.scanner.Constants;
import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.scanner.duplicate.FileSetManager;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: jeffrey.a.west
 * Date: Oct 30, 2011
 * Time: 5:01:17 PM
 */
public class DuplicateReportWriter
{
  public static final DecimalFormat SIZE_FORMAT = new DecimalFormat(Constants.SIZE_FORMAT_STRING);
  private static final long CREATION_MILLIS = System.currentTimeMillis();

  public static void writeReport(Collection<FileSetManager> managers)
  {
    for (FileSetManager manager : managers)
    {
      writeReport(manager);
    }
  }

  public static void writeReport(FileSetManager manager)
  {
    try
    {
      String pFilename = "/Users/jeffreyawest/Data/mycode/sandbox/checksum-standalone/reports/" + CREATION_MILLIS + '_' + manager.getDetectionStrategy().getClass().getSimpleName() + '_' + manager.getUUID() + ".txt";
      File file = new File(pFilename);
      System.out.println("Creating report:" + file.getAbsolutePath());
      FileOutputStream fileOut = new FileOutputStream(file);

      PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileOut));

      writeDuplicateSummary(manager, writer);

      for (FileSet fileSet : manager.getSizeOrderedDuplicateSet())
      {
        if (fileSet.hasDuplicates())
        {
          writeDuplicates(fileSet, writer);
        }
      }

      writer.flush();
      writer.close();
      fileOut.close();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private static void writeDuplicates(FileSet pFileSet, PrintWriter writer)
  {
    writer.println("===============================================");
    writer.println("File 1: " + pFileSet.getFile1().getTheFile().getName());
    writer.println("Hash: " + pFileSet.getHash());
    writer.println("Size: " + SIZE_FORMAT.format(pFileSet.getFile1().getTheFile().length()));
    writer.println("Duplicates: " + SIZE_FORMAT.format(pFileSet.getFiles().size()));
    writer.println("Duplicate Space: " + SIZE_FORMAT.format(pFileSet.getSpace()));

    writer.println("Duplicate Files");
    writer.println("-------------------");

    for (FileWrapper fileWrapper : pFileSet.getFiles())
    {
      writer.println(fileWrapper.getAbsolutePath());
    }
    writer.println("-------------------");

    writer.flush();
  }

  private static void writeDuplicateSummary(FileSetManager manager, PrintWriter writer)
  {
    writer.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    writer.println("Strategy: " + manager.getDetectionStrategy().getClass().getSimpleName());
    writer.println("Processing Time: " + SIZE_FORMAT.format(manager.getProcesingTime()));
    writer.println("Duplicate Space: " + SIZE_FORMAT.format(manager.getDuplicatesSpace()));

    if (manager.getObserver().getBiggestFileSet() != null)
    {
      FileSet set = manager.getObserver().getBiggestFileSet();

      writer.println("New Biggest File Set for Manager/Strategy=["
                     + manager.getDetectionStrategy().getClass().getSimpleName()
                     + "] size=[" + set.size()
                     + "] space=[" + SIZE_FORMAT.format(set.getSpace())
                     + "] file=[" + set.getFile1() + ']');
    }

    if (manager.getObserver().getMostDuplicatedFileSet() != null)
    {
      FileSet set = manager.getObserver().getMostDuplicatedFileSet();

      writer.println("Most Duplicated File Set for Manager/Strategy=["
                     + manager.getDetectionStrategy().getClass().getSimpleName()
                     + "] size=[" + set.size()
                     + "] space=[" + SIZE_FORMAT.format(set.getSpace())
                     + "] file=[" + set.getFile1() + ']');
    }

    writer.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
  }

  public static void writeReport(ChecksumManager manager)
  {
    //To change body of created methods use File | Settings | File Templates.
  }
}