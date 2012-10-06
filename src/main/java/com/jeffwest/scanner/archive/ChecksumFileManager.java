package com.jeffwest.scanner.archive;

import com.jeffwest.filewrapper.FileWrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: jeffrey.a.west
 * Date: Oct 27, 2011
 * Time: 7:18:22 PM
 */
public class ChecksumFileManager
{
  private static final int ONE_MB = 1048576;

  public static void writeChecksums(HashMap<String, FileWrapper> checksumMap, String filename)
  {
    System.out.println("Writing [" + checksumMap.size() + "] checksums to path=[" + new File(filename).getAbsolutePath() + ']');

    try
    {
      FileOutputStream fileOut = new FileOutputStream(filename);
      BufferedOutputStream bufferOut = new BufferedOutputStream(fileOut);
      PrintWriter writer = new PrintWriter(bufferOut);

      for(FileWrapper wrapper: checksumMap.values())
      {
        writer.println(wrapper.toCSV());
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

  public static Collection<FileWrapper> readSourceChecksumsFromFile(File sourceChecksumsFile)
      throws
      IOException
  {
    ArrayList<FileWrapper> list = new ArrayList();

    FileInputStream fileIn = new FileInputStream(sourceChecksumsFile);
    BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn), ONE_MB*10);
    String line = null;

    while ((line = reader.readLine()) != null)
    {
      FileWrapper wrapper = FileWrapper.fromCSV(line);

      if (wrapper != null)
      {
        list.add(wrapper);
      }
    }

    System.out.println("Read [" + list.size() + "] checksums from io!");

    return list;
  }
}
