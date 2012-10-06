package com.jeffwest;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.scanner.RecursiveFileScannerObservable;
import org.apache.sanselan.SanselanConstants;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class MakeModelIndexer
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
    MakeModelIndexer observer = new MakeModelIndexer();
    RecursiveFileScannerObservable scanner = new RecursiveFileScannerObservable();

    scanner.addObserver(observer);
//    scanner.scanDirectory("/Users/jeffreyawest/Data/mycode/sandbox/metadata-extractor-apple-ext/test/");
//    scanner.scanDirectory("/Users/jeffreyawest/Pictures/PIX-FINAL");
    scanner.scanDirectory("/Users/jeffreyawest/Downloads");
//    scanner.scanDirectory("/Users/jeffreyawest/Pictures/PIX-FINAL");
  }

  @Override
  public void update(Observable o, Object arg)
  {
    if (arg instanceof JPEGWrapper)
    {
      JPEGWrapper wrapper = (JPEGWrapper) arg;

      if (wrapper.getTheFile().getAbsolutePath().contains("Modified"))
      {
        return;
      }

      String model = "";

      try
      {
        model = wrapper.getModel();

        if (model == null || model.isEmpty())
        {
          return;
        }

        Metadata metadata = wrapper.getMetadata();

        Integer dirCount = modelDirectoryMap.get(model);

        if (dirCount == null || (metadata.getDirectoryCount() > dirCount))
        {
          modelDirectoryMap.put(model, metadata.getDirectoryCount());

          String theString = getMetadataString(metadata);

          System.out.println(theString);
          System.out.println("==========================================================================");
          System.out.println("Model: " + model);
          System.out.println("File: " + wrapper);
          System.out.println("\n\n");

          String dirPath = "/Users/jeffreyawest/Data/mycode/sandbox/checksum-standalone/examples";

          String filename = model + '(' + metadata.getDirectoryCount() + ')';

          filename = filename.replaceAll("\\/", "_");
          filename = filename.replaceAll("\\\\", "_");
          filename = filename.replaceAll("\\s", "_");
          filename = filename.replaceAll(",", "_");
          filename = filename.replaceAll("\\.", "_");

          filename = dirPath + '/' + filename + ".txt";
          System.out.println("Created: " + filename);
          System.out.println("==========================================================================");
          
          createExampleFile(filename, theString);
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  private void createExampleFile(String pFilename, String pTheString)
  {
    try
    {
      File file = new File(pFilename);
      FileOutputStream os = new FileOutputStream(file);
      os.write(pTheString.getBytes());
      os.close();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  private static String getMetadataString(final Metadata pMetadata)
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