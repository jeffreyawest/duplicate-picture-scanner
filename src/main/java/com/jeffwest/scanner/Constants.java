package com.jeffwest.scanner;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jeffrey.a.west
 * Date: Oct 30, 2011
 * Time: 5:22:13 PM
 */
public class Constants
{
  public static final String SIZE_FORMAT_STRING = "###,###,###,###,###.##";
  private static final DecimalFormat SIZE_FORMAT = new DecimalFormat(SIZE_FORMAT_STRING);

  public static final int ONE_MB = 1048576;
  private static final SimpleDateFormat jpegDateFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
  private static final SimpleDateFormat toDateFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

  public static String convertJpegDate(String pJpegDateStr)
  {
    Date jpegDate = null;
    try
    {
      synchronized (jpegDateFormatter)
      {
        jpegDate = jpegDateFormatter.parse(pJpegDateStr);
      }
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }

    String myDate;
    synchronized (toDateFormatter)
    {
      myDate = toDateFormatter.format(jpegDate);
    }

    return myDate;
  }

  public static String formatDate(Date pDate)
  {
    synchronized (toDateFormatter)
    {
      return toDateFormatter.format(pDate);
    }
  }

  public static Date parseDate(String pDateDtr) throws ParseException
  {
    synchronized (toDateFormatter)
    {
      return toDateFormatter.parse(pDateDtr);
    }
  }
}
