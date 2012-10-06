package com.jeffwest.sanselan;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import java.nio.file.Files;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JPEGReader
{
  static int fileCount = 0;
  static final SimpleDateFormat jpegDateFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
  static final SimpleDateFormat filenameDateFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
  private static final boolean RENAME_ENABLED = false;

  /**
   * Read metadata from image io and display it.
   *
   * @param file
   */
  public static void printMetadata(File file)
  {
    IImageMetadata metadata = null;

    try
    {
      metadata = Sanselan.getMetadata(file);
      dumpMetadata(metadata);
    }
    catch (Exception ignore)
    {
      ignore.printStackTrace();
    }

  }

  public static void dumpMetadata(IImageMetadata metadata)
  {
    if (metadata instanceof JpegImageMetadata)
    {
      JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
      dumpJPEGMetadata(jpegMetadata);
    }
    else
    {
      System.out.println("Unknown Image Metadata Type: " + metadata.getClass().getSimpleName());
    }
  }

  private static void dumpJPEGMetadata(JpegImageMetadata jpegMetadata)
  {
    printTagValue(jpegMetadata, TiffConstants.TIFF_TAG_XRESOLUTION);
    printTagValue(jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);
    printTagValue(jpegMetadata, TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
    printTagValue(jpegMetadata, TiffConstants.EXIF_TAG_CREATE_DATE);
    printTagValue(jpegMetadata, TiffConstants.EXIF_TAG_ISO);
    printTagValue(jpegMetadata, TiffConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
    printTagValue(jpegMetadata, TiffConstants.EXIF_TAG_APERTURE_VALUE);
    printTagValue(jpegMetadata, TiffConstants.EXIF_TAG_BRIGHTNESS_VALUE);

    // simple interface to GPS data
    TiffImageMetadata exifMetadata = jpegMetadata.getExif();

    if (exifMetadata != null)
    {
      try
      {
        TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
        if (null != gpsInfo)
        {
          double longitude = gpsInfo.getLongitudeAsDegreesEast();
          double latitude = gpsInfo.getLatitudeAsDegreesNorth();
          System.out.println("    " + "GPS Description: " + gpsInfo);
          System.out.println("    " + "GPS Longitude (Degrees East): " + longitude);
          System.out.println("    " + "GPS Latitude (Degrees North): " + latitude);
        }
      }
      catch (ImageReadException e)
      {
        e.printStackTrace();
      }
    }
    System.out.println("EXIF items -");

    ArrayList items = jpegMetadata.getItems();

    for (Object item : items)
    {
      System.out.println("    " + "item: " + item);
    }

    System.out.println();
  }

  private static void printTagValue(
      JpegImageMetadata jpegMetadata, TagInfo tagInfo)
  {
    TiffField field = jpegMetadata.findEXIFValue(tagInfo);
    if (field == null)
    {
      System.out.println(tagInfo.name + ": " + "Not Found.");
    }
    else
    {
      System.out.println(tagInfo.name + ": " + field.getValueDescription());
    }
  }

  private static String getTagValue(
      JpegImageMetadata jpegMetadata, TagInfo tagInfo)
  {
    TiffField field = jpegMetadata.findEXIFValue(tagInfo);

    if (field != null)
    {
      return field.getValueDescription();
    }

    return null;
  }

  /**
   * Example of adding an EXIF item to metadata, in this case using ImageHistory field.
   * (I have no idea if this is an appropriate use of ImageHistory, or not, just picked
   * a field to update that looked like it wasn't commonly mucked with.)
   */
  public static void main(String[] args)
  {
//    String strStartDirectory = "/Users/jeffreyawest/Pictures_temp/";
    String strStartDirectory = "/Users/jeffreyawest/Pictures/PIX-FINAL";
    File topDir = new File(strStartDirectory);

    processDir(topDir);

    System.out.println("Processed file count:" + fileCount);
  }

  private static void processDir(final File dir)
  {
    File[] files = dir.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isFile();
      }
    });

    if (files != null)
    {
      System.out.println("Found files count=[" + files.length + ']');

      for (File imageFile : files)
      {
        final String filename = imageFile.getName().toUpperCase();

        System.out.println("Processing file: " + imageFile.getAbsolutePath());

        if (filename.endsWith("JPEG")
            || filename.endsWith("JPG"))
        {
          processJPEGFile(imageFile);
        }
        else if (filename.endsWith("GIF")
            || filename.endsWith("MOV")
            || filename.endsWith("AVI")
            || filename.endsWith("MP4")
            || filename.endsWith("PNG"))
        {
          processOtherFile(imageFile);
        }
        else
        {
          System.out.println("NOT IMAGE FILE: " + imageFile.getAbsolutePath());
          try
          {
            Thread.sleep(500);
          }
          catch (Exception ignore)
          {
          }
        }
      }
    }

    FilenameFilter dirFilter = new FilenameFilter()
    {
      @Override
      public boolean accept(File file, String s)
      {
        return file.isDirectory();
      }
    };

    File[] dirs = dir.listFiles(dirFilter);

    if (dirs != null)
    {
      for (int i = 0; i < dirs.length; i++)
      {
        File subDir = dirs[i];
        processDir(subDir);
      }
    }
  }

  private static void processJPEGFile(File imageFile)
  {
    fileCount++;
    if (!(imageFile.getName().toUpperCase().endsWith(".JPG") || imageFile.getName().toUpperCase().endsWith(".JPEG")))
    {
      System.out.println("Not JPEG: " + imageFile.getName());
      return;
    }

    IImageMetadata metadata = null;
    JpegImageMetadata jpegMetadata = null;

    try
    {
      metadata = Sanselan.getMetadata(imageFile);
    }
    catch (Exception ignore)
    {
      processOtherFile(imageFile);
    }

    try
    {
      if (metadata == null)
      {
        System.out.println("Unable to get Sanselan Metadata");
        processOtherFile(imageFile);
        return;
      }

      jpegMetadata = (JpegImageMetadata) metadata;

      String jpegDateString = getTagValue(jpegMetadata, TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
//      System.out.println("JPEG Date String=[" + jpegDateString + "]");

      if (jpegDateString == null)
      {
        jpegDateString = getTagValue(jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);
      }

      if (jpegDateString == null)
      {
        System.out.println("Unable to determine date!");
        processOtherFile(imageFile);
      }
      else
      {
//        System.out.println("JPEG Date String=" + jpegDateString);

        jpegDateString = jpegDateString.substring(1, jpegDateString.length());
        Date theDate = jpegDateFormatter.parse(jpegDateString);

//        System.out.println("filename = " + filename_begin);
        String formattedDateString = filenameDateFormatter.format(theDate);
        renameFileWithDateString(imageFile, formattedDateString);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      processOtherFile(imageFile);
    }

  }

  private static void processOtherFile(File otherFile)
  {
    fileCount++;
    String dateString = getCreatedDate(otherFile);

    if (dateString != null)
    {
      try
      {
        renameFileWithDateString(otherFile, dateString);
      }
      catch (ParseException e)
      {
        e.printStackTrace();
      }
    }
  }

  private static String getCreatedDate(File imageFile)
  {
    Path path = imageFile.toPath();

    BasicFileAttributeView basicView = Files.getFileAttributeView(path, BasicFileAttributeView.class);

    try
    {
      BasicFileAttributes basicAtts = basicView.readAttributes();
      FileTime creationTime = basicAtts.creationTime();
      Date date = new Date(creationTime.toMillis());
      return filenameDateFormatter.format(date);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return null;
  }

  private static boolean renameFileWithDateString(File imageFile, String formattedDateString) throws ParseException
  {
    if (RENAME_ENABLED)
    {
      String filename_begin = imageFile.getName();

      File newFile = new File(imageFile.getParent() + '/' + formattedDateString + '_' + filename_begin);

      Date theDate = filenameDateFormatter.parse(formattedDateString);
      newFile.setLastModified(theDate.getTime());

      if (filename_begin.startsWith(formattedDateString))
      {
        return false;
      }

      System.out.println("filename = " + filename_begin);
      System.out.println("date string=" + formattedDateString);
      System.out.println("new filename=" + imageFile.getParent() + '/' + formattedDateString + '_' + filename_begin);

      boolean success = imageFile.renameTo(newFile);

      if (!success)
      {
        System.out.println("rename FAILED!");
        System.exit(0);
      }

      System.out.println("===============================================");

      return success;
    }

    return false;
  }
}