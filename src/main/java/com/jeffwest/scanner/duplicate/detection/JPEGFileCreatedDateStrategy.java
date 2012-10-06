package com.jeffwest.scanner.duplicate.detection;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.jeffwest.filewrapper.FileWrapper;
import com.jeffwest.filewrapper.JPEGWrapper;
import com.jeffwest.scanner.Constants;

import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jeffreyawest
 * Date: 5/11/12
 * Time: 1:31 PM
 */
public class JPEGFileCreatedDateStrategy implements DuplicateDetectionStrategy
{
  private static final String JPEG_DATE = "JPEG_DATE_";

  @Override
  public String getHash(FileWrapper wrapper) throws IOException
  {
    String date = null;
    Date date1 = null;
    String formattedDate;

    if (wrapper instanceof JPEGWrapper)
    {
      JPEGWrapper jpeg = (JPEGWrapper) wrapper;

      try
      {
        Metadata metadata = jpeg.getMetadata();

        ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);

        if (exifSubIFDDirectory != null)
        {
          date = exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
          //2007:07:29 14:48:29

          if (date != null)
          {
            formattedDate = Constants.convertJpegDate(date);
            return JPEG_DATE + formattedDate;
          }
        }
      }
      catch (Exception e)
      {
        System.out.println("Date=[" + date + ']');
        e.printStackTrace();
      }
    }

    return null;
  }
}
