package com.jeffwest.stream;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StreamTester
{
  private static final String STR_COMMA = ",";
  public static final NumberFormat MBPS_FORMAT = new DecimalFormat("###,###,###,###.##");
//524288

  public static void main(String[] pArgs)
  {
    String fileIn = null, fileOut = null, strBufferSize = null, strReadSize = null, strThreadCount = null, strDoChecksum = null, strBufferFactor = null;

    try
    {
      fileIn = System.getProperty("fileIn");
      if (fileIn == null)
      {
        System.out.println("-DfileIn=[String] is a required option");
        System.exit(-1);
      }

      System.out.println("fileIn=[" + fileIn + ']');

      fileOut = System.getProperty("fileOut");

      System.out.println("fileOut=[" + fileOut + ']');

      if (fileOut == null)
      {
        System.out.println("-fileOut=[String] is a required option");
        System.exit(-1);
      }

      strBufferSize = System.getProperty("bufferSize");

      if (strBufferSize == null)
      {
        strBufferSize = "65536";
      }

      strReadSize = System.getProperty("readSize");

      if (strReadSize == null)
      {
        strReadSize = strBufferSize;
      }

      strThreadCount = System.getProperty("threadCount");

      if (strThreadCount == null)
      {
        strThreadCount = "1";
      }

      strBufferFactor = System.getProperty("bufferFactor");

      if (strBufferFactor == null)
      {
        strBufferFactor = "-1";
      }

      strDoChecksum = System.getProperty("doChecksum");

      if (strDoChecksum == null)
      {
        strDoChecksum = "false";
      }

      System.out.println("strDoChecksum=" + strDoChecksum);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(0);
    }

    int threadCount = Integer.parseInt(strThreadCount);
    int bufferSize = Integer.parseInt(strBufferSize);
    int bufferFactor = Integer.parseInt(strBufferFactor);
    int readSize = Integer.parseInt(strReadSize);
    boolean doChecksum = Boolean.valueOf(strDoChecksum);

    long start = System.currentTimeMillis();

    List<StreamThread> threads = startThreads(threadCount, bufferSize, readSize, fileIn, fileOut, bufferFactor, doChecksum);

    try
    {
      Thread.sleep(500);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }

    boolean allDone = true;
    long totalSleep = 0;
    int completeCount = 0;
    do
    {
      completeCount = 0;

      double aggregateMBps = 0;
      long aggregateIOPS = 0;

      for (StreamThread thread : threads)
      {
        if (thread.isComplete())
        {
          completeCount++;
        }
        else
        {
          System.out.println("Thread:" + thread.getName() + "% Complete=[" + MBPS_FORMAT.format(thread.getPercentComplete()) + "] AVG Read MBps=[" + MBPS_FORMAT.format(thread.getRealtimeReadMBps()) + "] Read Requests=[" + thread.getReadCount() + ']');
          aggregateMBps += thread.getRealtimeReadMBps();
          aggregateIOPS += thread.getRealtimeReadIOPS();
        }
      }

      System.out.println("Aggregate MBps read=[" + MBPS_FORMAT.format(aggregateMBps) + "] total=[" + MBPS_FORMAT.format(aggregateMBps * 2) + ']');

      if (completeCount >= threads.size())
      {
        break;
      }

      try
      {
        totalSleep += 5000;
        Thread.sleep(5000);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }

      System.out.println("Running Duration=[" + MBPS_FORMAT.format((double) totalSleep / (double) 60000) + "] minutes");
    }
    while (completeCount < threads.size());

    double aggregateMBps = 0;
    for (StreamThread thread : threads)
    {
      aggregateMBps += thread.getReadMBps();
    }

    long stop = System.currentTimeMillis();

    long average = 0;

    for (StreamThread thread : threads)
    {
      average += thread.getDurationMillis();
    }

    average = average / threads.size();

    System.out.println("All Done - Date=[" + new Date() + ']');

    aggregateMBps = 0;

    for (StreamThread thread : threads)
    {
      aggregateMBps += thread.getRealtimeReadMBps();
    }

    System.out.println("Aggregate FINAL MBps=[" + MBPS_FORMAT.format(aggregateMBps) + ']');

    try
    {
      FileOutputStream fileOutStream = new FileOutputStream("StreamPerformance.log.csv", true);
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileOutStream));

      for (StreamThread thread : threads)
      {
        StringBuffer sb = new StringBuffer();
        sb.append(new Date(start));
        sb.append(STR_COMMA);
        sb.append(new Date(stop));
        sb.append(STR_COMMA);
        sb.append(bufferSize);
        sb.append(STR_COMMA);
        sb.append(readSize);
        sb.append(STR_COMMA);
        sb.append(doChecksum);
        sb.append(STR_COMMA);
        sb.append(fileIn);
        sb.append(STR_COMMA);
        sb.append(thread.getFileSize());
        sb.append(STR_COMMA);
        sb.append(thread.getDurationMillis());
        sb.append(STR_COMMA);
        sb.append(((double) thread.getDurationMillis() / (double) 60000));
        sb.append(STR_COMMA);
        sb.append(thread.getName());
        sb.append(STR_COMMA);
        sb.append(StreamTester.MBPS_FORMAT.format(thread.getReadMBps()));
        sb.append(STR_COMMA);
        sb.append(thread.getReadOperations());
        sb.append(STR_COMMA);
        sb.append(thread.getReadIOPS());

        writer.println(sb);
      }

      writer.flush();
      writer.close();
      fileOutStream.close();
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

  private static List<StreamThread> startThreads(int pThreadCount, int pBufferSize, int pReadSize, String pFileIn, String pFileOut, int pBufferFactor, boolean pDoChecksum)
  {
    List<StreamThread> mThreads = new ArrayList<StreamThread>(pThreadCount);

    for (int x = 0; x < pThreadCount; x++)
    {
      StreamThread streamThread = new StreamThread(pThreadCount + "-StreamThread-" + x, pFileIn, pFileOut, pBufferSize, pReadSize, pBufferFactor, pDoChecksum);
      mThreads.add(streamThread);
      Thread thread = new Thread(streamThread, pThreadCount + "-StreamThread-" + x);
      thread.start();
    }

    return mThreads;
  }


  public enum CHECKSUM_TYPE
  {
    CRC32, MD5
  }

}