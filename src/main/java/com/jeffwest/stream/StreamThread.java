package com.jeffwest.stream;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: jeffrey.a.west
 * Date: Mar 15, 2010
 * Time: 9:41:28 AM
 */
public class StreamThread implements Runnable
{
  private String mFileIn;
  private String mFileOut;
  private int mBufferSize;
  private int mReadSize;
  private StreamTester.CHECKSUM_TYPE mChecksumType;
  private long mDurationMillis;
  private boolean mComplete;
  private String mName;
  private String mChecksumValue;
  private int mBufferFactor;
  private boolean mDoChecksum;
  private long mFileSize;
  private long mReadCount;

  private long mBytesRead = 0;

  private double mReadMBps;
  private long mReadOperations;
  private long mReadIOPS;

  private long mStop;
  private long mStart;

  public StreamThread(String pName, String pFileIn, String pFileOut, int pBufferSize, int pReadSize, int pBufferFactor, boolean pDoChecksum)
  {
    mFileIn = pFileIn;
    mFileOut = pFileOut;
    mBufferSize = pBufferSize;
    mReadSize = pReadSize;
    mName = pName;
    mBufferFactor = pBufferFactor;
    mDoChecksum = pDoChecksum;
  }


  @Override
  public void run()
  {
    try
    {
      System.out.println("STARTING::: Thread name=[" + Thread.currentThread().getName() +
              "] BufferSize=[" + mBufferSize +
              "] ReadSize=[" + mReadSize +
              "] doChecksum=[" + mDoChecksum +
              "] ReadSize=[" + mReadSize +
              "] BufferFactor=[" + mBufferFactor +
              "] File=[" + mFileIn + ']');
      mStart = System.currentTimeMillis();
      mFileSize = new File(mFileIn).length();

      mChecksumValue = copyFile(mFileIn, mFileOut + '_' + mName, mBufferSize, mReadSize, mDoChecksum);

      mStop = System.currentTimeMillis();
      mDurationMillis = mStop - mStart;

      calculate();

      StringBuilder sb = new StringBuilder();
      sb.append("Thread=");
      sb.append(Thread.currentThread().getName());
      sb.append(" fileIn=");
      sb.append(mFileIn);
      sb.append(" fileOut=");
      sb.append(mFileOut);
      sb.append(" fileSize=");
      sb.append(mFileSize);
      sb.append(" duration(ms)=");
      sb.append(mDurationMillis);
      sb.append(" MBps=");
      sb.append(StreamTester.MBPS_FORMAT.format(mReadMBps));
      sb.append(" ReadOperations=");
      sb.append(mReadOperations);
      sb.append(" IOPS(avg)=");
      sb.append(mReadIOPS);

      System.out.println(sb);

      System.out.println("COMPLETE::: Thread name=[" + Thread.currentThread().getName() +
              "] BufferSize=[" + mBufferSize +
              "] doChecksum=[" + mDoChecksum +
              "] ReadSize=[" + mReadSize +
              "] BufferFactor=[" + mBufferFactor +
              "] File=[" + mFileIn + ']');


    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    mComplete = true;
  }

  private void calculate()
  {
    long fileSize = new File(mFileIn).length();

    mReadOperations = fileSize / mBufferSize;

    long durationSeconds = mDurationMillis / 1000 / 60;

    mReadIOPS = mReadOperations / durationSeconds;

    mReadMBps = ((double) fileSize / (double) 1048576) / (double) durationSeconds;
  }

  public String getFileIn()
  {
    return mFileIn;
  }

  public void setFileIn(String pFileIn)
  {
    mFileIn = pFileIn;
  }

  public int getBufferSize()
  {
    return mBufferSize;
  }

  public void setBufferSize(int pBufferSize)
  {
    mBufferSize = pBufferSize;
  }

  public StreamTester.CHECKSUM_TYPE getChecksumType()
  {
    return mChecksumType;
  }

  public void setChecksumType(StreamTester.CHECKSUM_TYPE pChecksumType)
  {
    mChecksumType = pChecksumType;
  }

  public long getDurationMillis()
  {
    return mDurationMillis;
  }

  public void setDurationMillis(long pDurationMillis)
  {
    mDurationMillis = pDurationMillis;
  }

  public boolean isComplete()
  {
    return mComplete;
  }

  public void setComplete(boolean pComplete)
  {
    mComplete = pComplete;
  }

  public String getName()
  {
    return mName;
  }

  public void setName(String pName)
  {
    mName = pName;
  }

  public String getChecksumValue()
  {
    return mChecksumValue;
  }

  public void setChecksumValue(String pChecksumValue)
  {
    mChecksumValue = pChecksumValue;
  }

  public int getBufferFactor()
  {
    return mBufferFactor;
  }

  public void setBufferFactor(int pBufferFactor)
  {
    mBufferFactor = pBufferFactor;
  }

  public String getFileOut()
  {
    return mFileOut;
  }

  public void setFileOut(String pFileOut)
  {
    mFileOut = pFileOut;
  }

  public int getReadSize()
  {
    return mReadSize;
  }

  public void setReadSize(int pReadSize)
  {
    mReadSize = pReadSize;
  }

  public boolean isDoChecksum()
  {
    return mDoChecksum;
  }

  public void setDoChecksum(boolean pDoChecksum)
  {
    mDoChecksum = pDoChecksum;
  }

  public double getReadMBps()
  {
    return mReadMBps;
  }

  public void setReadMBps(double pReadMBps)
  {
    mReadMBps = pReadMBps;
  }

  public long getReadOperations()
  {
    return mReadOperations;
  }

  public void setReadOperations(int pReadOperations)
  {
    mReadOperations = pReadOperations;
  }

  public long getReadIOPS()
  {
    return mReadIOPS;
  }

  public void setReadIOPS(int pReadIOPS)
  {
    mReadIOPS = pReadIOPS;
  }

  public double getRealtimeReadMBps()
  {
    double MBps = 0.0;

    double duration = (System.currentTimeMillis() - mStart) / 1000;

    //System.out.println("size of io=[" + mFileOut + "_" + mName + "] is=[" + fileSize + "]");
    MBps = ((double) mBytesRead / (double) 1048576) / duration;
    //System.out.println("MBps double=" + MBps);
    return MBps;
  }

  public long getRealtimeReadIOPS()
  {
    long IOPS = 0;

    long duration = (System.currentTimeMillis() - mStart) / 1000;

    //System.out.println("size of io=[" + mFileOut + "_" + mName + "] is=[" + fileSize + "]");
    if (duration > 0)
    {
      IOPS = mReadCount / duration;
    }
    //System.out.println("IOPS double=" + IOPS);
    return IOPS;
  }

  public double getPercentComplete()
  {
    return (double) 100 * ((double) mBytesRead / (double) mFileSize);
  }

  public String copyFile(String pFileNameIn, String pFileNameOut, int pBufferSize, int pReadSize, boolean pUseChecksum) throws IOException
  {
    final String ZEROS = "00000000";
    final int CRC32_CHECKSUM_LENGTH = 8;

    CRC32 checksumEngine = new CRC32();
    long decimalChecksum = 0;
    String hexChecksum = null;

    InputStream sourceInputStream;
    final OutputStream destinationOutputStream;

    final FileInputStream fileInputStream = new FileInputStream(pFileNameIn);
    final FileOutputStream fileOutputStream = new FileOutputStream(pFileNameOut);

    sourceInputStream = new BufferedInputStream(fileInputStream, pBufferSize);
    destinationOutputStream = new BufferedOutputStream(fileOutputStream, pBufferSize);

    if (pUseChecksum)
    {
      sourceInputStream = new CheckedInputStream(sourceInputStream, checksumEngine);
    }

    byte[] readBuffer = new byte[pReadSize];

    int bytesRead = 0;
    mReadCount = 0;

    while ((bytesRead = sourceInputStream.read(readBuffer)) >= 0)
    {
      mReadCount++;
      mBytesRead += bytesRead;
      destinationOutputStream.write(readBuffer, 0, bytesRead);
    }

    if (pUseChecksum)
    {
      decimalChecksum = ((CheckedInputStream) sourceInputStream).getChecksum().getValue();
      hexChecksum = Long.toHexString(decimalChecksum).toUpperCase();
      hexChecksum = ZEROS.substring(0, CRC32_CHECKSUM_LENGTH - hexChecksum.length()) + hexChecksum;
    }

    sourceInputStream.close();
    fileOutputStream.close();

    return hexChecksum;
  }

  public long getFileSize()
  {
    return mFileSize;
  }

  public void setFileSize(long pFileSize)
  {
    mFileSize = pFileSize;
  }

  public long getBytesRead()
  {
    return mBytesRead;
  }

  public void setBytesRead(long pBytesRead)
  {
    mBytesRead = pBytesRead;
  }

  public long getStop()
  {
    return mStop;
  }

  public void setStop(long pStop)
  {
    mStop = pStop;
  }

  public long getStart()
  {
    return mStart;
  }

  public void setStart(long pStart)
  {
    mStart = pStart;
  }

  public long getReadCount()
  {
    return mReadCount;
  }

  public void setReadCount(long pReadCount)
  {
    mReadCount = pReadCount;
  }
}