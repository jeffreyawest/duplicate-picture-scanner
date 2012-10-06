package com.jeffwest.filewrapper;

import com.jeffwest.scanner.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: jeffrey.a.west
 * Date: Oct 29, 2011
 * Time: 7:11:47 PM
 */
public class FileWrapper
{
  public static final int ONE_MB = 1048576;
  public static final int TWO_MB = ONE_MB * 2;
  public static final int FOUR_MB = TWO_MB * 2;

  protected File theFile;
  protected String hash;

  private static final String STRING_QUOTES = "\"";
  private static final String STRING_COMMA = ",";

  public FileWrapper(File theFile)
  {
    this.theFile = theFile;
  }

  public FileWrapper(String path, String hash)
  {
    this(new File(path));
    this.hash = hash;
  }

  public String getAbsolutePath()
  {
    return theFile.getAbsolutePath();
  }

  public String toString()
  {
    return theFile.getName();
  }

  public int hashCode()
  {
    return this.toString().hashCode();
  }

  public File getTheFile()
  {
    return theFile;
  }

  public long length()
  {
    return theFile.length();
  }

  public boolean equals(Object pObject)
  {
    if (pObject instanceof FileWrapper)
    {
      FileWrapper fw = (FileWrapper) pObject;

      return theFile != null && theFile.getAbsolutePath().equals(fw.theFile.getAbsolutePath());
    }
    else
    {
      return false;
    }
  }

  public String getHash()
  {
    return hash;
  }

  public String toCSV()
  {
//    System.out.println("CHECKSUM=["+getHash()+"]");
    final StringBuilder sb = new StringBuilder();

    sb.append(STRING_QUOTES);
    sb.append(hash);
    sb.append(STRING_QUOTES);

    sb.append(STRING_COMMA);

    sb.append(STRING_QUOTES).append(length()).append(STRING_QUOTES);
    sb.append(STRING_COMMA);

    sb.append(STRING_QUOTES).append(toString()).append(STRING_QUOTES);

    return sb.toString();
  }

  public String getCreatedDateStr()
  {
    Path path = theFile.toPath();

//    AclFileAttributeView aclView = Files.getFileAttributeView(path, AclFileAttributeView.class);
    BasicFileAttributeView basicView = Files.getFileAttributeView(path, BasicFileAttributeView.class);

    try
    {
      BasicFileAttributes basicAtts = basicView.readAttributes();
      FileTime creationTime = basicAtts.creationTime();

//      String theCreation = creationTime.toString();

      Date date = new Date(creationTime.toMillis());
      return Constants.formatDate(date);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return null;
  }



  public static FileWrapper fromCSV(String line)
  {
    StringTokenizer st = new StringTokenizer(line, STRING_COMMA);
    int count = st.countTokens();

    if (false && count != 3)
    {
      StringBuilder sb = new StringBuilder();

      int index = 1;

      while (st.hasMoreTokens())
      {
        sb.append("Token [").append(index++).append("]=[").append(st.nextToken()).append(']');
      }

      System.out.println("TOEKN COUNT=[" + count + "] UNABLE TO MAKE FILEWRAPPER FROM STRING=[" + line + ']');
      return null;
    }

    String checksum = trimQuotes(st.nextToken(","));
//    System.out.println("scanner="+scanner);

    String strLength = trimQuotes(st.nextToken(","));
//    System.out.println("length="+strLength);

    String path = trimQuotes(line.substring(checksum.length() + strLength.length() + 6));
//    System.out.println("path="+path);

    if (new File(path).exists())
    {
      return new FileWrapper(path, checksum);
    }

    return null;
  }

  private static String trimQuotes(String s)
  {
    return s.substring(1, s.length() - 1);
  }

  public static FileWrapper getFileWrapper(File file)
  {
    String pathCAPS = file.getAbsolutePath().toUpperCase();

    if (pathCAPS.endsWith("JPEG")
        || pathCAPS.endsWith("JPG"))
    {
      return new JPEGWrapper(file);
    }
    else
    {
      return new FileWrapper(file);
    }
  }
}
