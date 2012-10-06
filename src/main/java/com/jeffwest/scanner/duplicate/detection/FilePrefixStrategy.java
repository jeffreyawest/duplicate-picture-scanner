package com.jeffwest.scanner.duplicate.detection;

import com.jeffwest.filewrapper.FileWrapper;

import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/8/12
 * Time: 10:33 PM
 */
public class FilePrefixStrategy implements DuplicateDetectionStrategy
{
  private static final String PREDATE = "PREDATE_";

  @Override
  public String getHash(FileWrapper wrapper)
  {
    String filename = wrapper.getTheFile().getName();

    if(Character.isLetter(filename.charAt(0)))
    {
      return null;
    }

    StringTokenizer st = new StringTokenizer(filename, "_");

    if(st.countTokens() < 2)
    {
      return null;
    }

    int index = 0;

    for (int x = 0; x < 2; x++)
    {
      index = filename.indexOf('_', index + 1);
    }

    if(index == -1)
    {
      return null;
    }

    String datePrefix = filename.substring(0, index);

    return PREDATE + datePrefix;
  }
}
