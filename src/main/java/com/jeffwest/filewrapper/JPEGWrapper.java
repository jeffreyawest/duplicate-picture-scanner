package com.jeffwest.filewrapper;

//import com.drew.imaging.ImageMetadataReader;
//import com.drew.imaging.ImageProcessingException;
//import com.drew.instanceMetadata.Metadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Descriptor;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDescriptor;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDescriptor;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import com.jeffwest.scanner.duplicate.FileSet;

import java.io.File;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/9/12
 * Time: 9:23 AM
 */
public class JPEGWrapper extends FileWrapper
{
  private static final boolean BYPASS = false;
  private static final String STR_X = " x ";
  private String caption;
  private long fileWeight;
  private String model;
  private String resolution;

  private Metadata instanceMetadata;

  final static String[] filenames = new String[]{
      "/Users/jeffreyawest/Pictures/PIX-FINAL/AutoImport/20110323_192158_IMG_1178.JPG",
      "/Users/jeffreyawest/Pictures/PIX-FINAL/AutoImport/20110323_192158_IMG_1179.JPG",
      "/Users/jeffreyawest/Pictures/PIX-FINAL/Combined_pictures/Mac Pictures/iPhoto Library/Modified/2011/Apr 2, 2011/20110323_192158_IMG_1178.jpg",
      "/Users/jeffreyawest/Pictures/PIX-FINAL/Combined_pictures/Mac Pictures/iPhoto Library/Modified/2011/3-23-2011/20110323_192158_1000001125.jpg"
  };

  private long pixelCount;

  public JPEGWrapper(File theFile)
  {
    super(theFile);
  }

  public Metadata getMetadata() throws Exception
  {
    if (instanceMetadata == null)
    {
      instanceMetadata = ImageMetadataReader.readMetadata(theFile);
    }

    return instanceMetadata;
  }

  public static void setCaptionFieldsBatch(String pCaption, FileSet pFileSet)
  {
    if (!pFileSet.hasDuplicates())
    {
      System.out.println("NO DUPLICATES!!");
      return;
    }

    ArrayList<String> params = new ArrayList<>(4 + pFileSet.getFiles().size());

    params.add("exiftool");
    params.add("-overwrite_original_in_place");
    params.add("-P");
    params.add("-caption='" + pCaption + '\'');
    params.add("-description='" + pCaption + '\'');
    params.add("-caption-abstract='" + pCaption + '\'');

    for (FileWrapper fileWrapper : pFileSet.getFiles())
    {
      params.add(fileWrapper.getTheFile().getAbsolutePath());
    }

    String[] command_params = params.toArray(new String[params.size()]);

    executeCommand(command_params);
  }

  public String getModel()
  {
    if (model == null)
    {
      ExifIFD0Directory makerDir = null;
      try
      {
        makerDir = getMetadata().getOrCreateDirectory(ExifIFD0Directory.class);
        ExifIFD0Descriptor makerDesc = new ExifIFD0Descriptor(makerDir);

        model = makerDesc.getModel();
        String make = makerDesc.getMake();

        if (make != null && !make.isEmpty() && !model.contains(make))
        {
          model = make.trim() + ' ' + model.trim();
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    return model;
  }

  public void setCaptionField(String pCaption)
  {
    String caption = ltrim(pCaption.trim());

    String[] command_params = new String[]{"exiftool",
                                           "-overwrite_original_in_place",
                                           "-P",
                                           "-caption='" + caption + '\'',
                                           "-description='" + caption + '\'',
                                           "-caption-abstract='" + caption + '\'',
                                           theFile.getAbsolutePath()};

    executeCommand(command_params);
  }

  private static String ltrim(String pTrim)
  {
    char[] chars = pTrim.toCharArray();
    int startText = 0;

    while (startText < chars.length)
    {
      if (Character.isLetterOrDigit(pTrim.charAt(startText)))
      {
        break;
      }
      startText++;
    }

    return pTrim.substring(startText, pTrim.length());
  }

  public void addCaptionField(String pCaption)
  {
    String currentCaption = getCaption();

    if (currentCaption == null
        || currentCaption.trim().equals("null")
        || currentCaption.trim().equals("null "))
    {
      setCaptionField(pCaption);
    }
    else if (!currentCaption.contains(pCaption))
    {
      setCaptionField(currentCaption + ' ' + pCaption);
    }
    else
    {
//      System.out.println("Already Contains Caption!");
    }

  }

  private static void executeCommand(String[] pCommands)
  {
    if (BYPASS)
    {
      StringBuilder sb = new StringBuilder();

      for (String command : pCommands)
      {
        sb.append(command).append(' ');
      }

      System.out.println("COMMAND:" + sb);
    }
    else
    {
      Runtime rt = Runtime.getRuntime();

      try
      {
        Process pr = rt.exec(pCommands);
        pr.waitFor();
        StringBuilder sb = new StringBuilder();

        for (String command : pCommands)
        {
          sb.append(command).append(' ');
        }

        System.out.println("Command=[" + sb + "] returned code=[" + pr.exitValue() + ']');

        if (pr.exitValue() != 0)
        {
          System.out.println("ERROR!!!");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  public static final long weighFile(JPEGWrapper pFile)
  {
    // pFile size
    // GPS
    // HDR
    // resolution

    return 0;

  }


  public String getCaption()
  {
    if (caption == null)
    {
      Metadata metadata = null;

      try
      {
        metadata = getMetadata();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        return null;
      }

      IptcDirectory iptc = metadata.getDirectory(IptcDirectory.class);

      if (iptc == null)
      {
        return null;
      }

      IptcDescriptor desc = new IptcDescriptor(iptc);

      caption = desc.getCaptionDescription();
    }

    return caption;
  }

  public static void main(String[] args)
  {
    ArrayList<JPEGWrapper> list = new ArrayList<>();

    for (String filename : filenames)
    {
      File file = new File(filename);
      JPEGWrapper wrapper = new JPEGWrapper(file);

      System.out.println("Size=[" + file.length() + "] GPS=[" + wrapper.hasGPS() + "] path=[" + file.getAbsolutePath() + "]");

      list.add(wrapper);
    }
  }

  public boolean isModifiedVersion()
  {
    return theFile.getAbsolutePath().toUpperCase().contains("odified");
  }

  public boolean isLowerRes()
  {
//    Metadata metadata = getMetadata();
//
//    if(metadata.get)
    return false;
  }

  public boolean hasGPS()
  {
    Metadata metadata = null;

    try
    {
      metadata = getMetadata();
      GpsDirectory gpsDir = metadata.getOrCreateDirectory(GpsDirectory.class);
      GpsDescriptor desc = new GpsDescriptor(gpsDir);

      if (desc.getGpsVersionIdDescription() != null)
      {
        return true;
      }

//      for (Directory directory : instanceMetadata.getDirectories())
//      {
//        for (Tag tag : directory.getTags())
//        {
//          System.out.println(tag);
//        }
//      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return false;
  }

  public long getPixelCount()
  {
    if (pixelCount == -1)
    {
      try
      {
        Metadata metadata = getMetadata();

        JpegDirectory dir = metadata.getDirectory(JpegDirectory.class);
        JpegDescriptor desc = new JpegDescriptor(dir);
        long height = Long.parseLong(desc.getImageHeightDescription());
        long width = Long.parseLong(desc.getImageWidthDescription());

        final StringBuilder sb = new StringBuilder(10);
        sb.append(desc.getImageWidthDescription());
        sb.append(STR_X);
        sb.append(desc.getImageHeightDescription());

        resolution = sb.toString();

        pixelCount = height * width;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    return pixelCount;
  }

  public String getResolution()
  {
    if (resolution == null)
    {
      try
      {
        Metadata metadata = getMetadata();
        JpegDirectory dir = metadata.getDirectory(JpegDirectory.class);
        JpegDescriptor desc = new JpegDescriptor(dir);

        final StringBuilder sb = new StringBuilder(10);
        sb.append(desc.getImageWidthDescription());
        sb.append(STR_X);
        sb.append(desc.getImageHeightDescription());

        resolution = sb.toString();

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    return resolution;
  }
}
