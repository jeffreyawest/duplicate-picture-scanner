package com.jeffwest;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.scanner.Constants;
import com.jeffwest.scanner.RecursiveFileScannerObservable;
import org.apache.sanselan.SanselanConstants;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class JpegFileRenamer
    implements Observer,
               SanselanConstants
{
  static int fileCount = 0;
  static final SimpleDateFormat jpegDateFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
  static final SimpleDateFormat filenameDateFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
  private static final boolean RENAME_ENABLED = false;

  private HashMap<String, Integer> modelDirectoryMap = new HashMap<>();

  public static void main(String[] args)
  {
    JpegFileRenamer observer = new JpegFileRenamer();
    RecursiveFileScannerObservable scanner = new RecursiveFileScannerObservable();

    scanner.addObserver(observer);
//    scanner.scanDirectory("/Users/jeffreyawest/Data/mycode/sandbox/metadata-extractor-apple-ext/test/");
    scanner.scanDirectory("/Users/jeffreyawest/Pictures/PIX-FINAL/China 2011/2010");
//    scanner.scanDirectory("/Volumes/600_Files/PIX-FINAL");
//    scanner.scanDirectory("/Users/jeffreyawest/Pictures/PIX-FINAL");
  }

  @Override
  public void update(Observable o, Object arg)
  {
    if (arg instanceof JPEGWrapper)
    {
      JPEGWrapper wrapper = (JPEGWrapper) arg;

      try
      {
        Metadata metadata = wrapper.getMetadata();
        ExifSubIFDDirectory dir = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        ExifSubIFDDescriptor desc = new ExifSubIFDDescriptor(dir);

        String jpegDate = desc.getCreatedDate();

        if (jpegDate == null)
        {
          return;
        }

        String toDate = Constants.convertJpegDate(jpegDate);

        final String originalFilename = wrapper.getTheFile().getName();
        String startFilename = new String(originalFilename);

        String parentDir;
        String originalFilenameDate = "";

//        parentDir = "/Users/jeffreyawest/Pictures/PIX-FINAL/Honeymoon";
        parentDir = wrapper.getTheFile().getParent();

        if (startFilename.startsWith(toDate))
        {
          return;
        }

        if (startFilename.length() > 15)
        {
          String filenameDate = startFilename.substring(0, 15);

          try
          {
            Constants.parseDate(filenameDate);
            startFilename = startFilename.substring(16, startFilename.length());
          }
          catch (Exception e)
          {
            //not a date
            System.out.println("Not a Date");
          }
        }

        String toFilename = "";

        toFilename = toDate + '_' + startFilename;
        boolean fileExists = new File(parentDir + '/' + toFilename).exists();

        while (fileExists && !toFilename.equals(originalFilename))
        {
          int lastIndex = toFilename.lastIndexOf('.');
          String baseName = toFilename.substring(0, lastIndex);

          System.out.println(baseName);

          toFilename = baseName + "-1.JPG";
          System.out.println("File=[" + baseName + "] exists and will try name=[" + toFilename + "]");
          fileExists = new File(parentDir + '/' + toFilename).exists();
        }

        final String originalPath = wrapper.getTheFile().getAbsolutePath();
        final String newPath = parentDir + '/' + toFilename;

        if (!originalPath.equals(newPath))
        {
          boolean success = wrapper.getTheFile().renameTo(new File(newPath));

          System.out.println("Rename old=[" + originalPath + "] new=[" + newPath + "] Success=[" + success + "]");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  private String getMetadataString(Metadata pMetadata)
  {
    StringWriter sWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(sWriter);

    for (Directory directory : pMetadata.getDirectories())
    {
      for (Tag tag : directory.getTags())
      {
        String tagString = tag.toString();
        writer.println(tagString);
      }
    }

    writer.flush();
    return sWriter.toString();
  }
}