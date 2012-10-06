package com.jeffwest.scanner.checksum;/*
   This program is a part of the companion code for Core Java 8th ed.
   (http://horstmann.com/corejava)

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;

/**
 * This program computes the CRC checksum of a file. <br>
 * Usage: java NIOTest filename
 *
 * @author Cay Horstmann
 * @version 1.01 2004-05-11
 */
public class NIOTest
{
  public static long checksumInputStream(String filename) throws IOException
  {
    InputStream in = new FileInputStream(filename);
    CRC32 crc = new CRC32();

    int c;
    while ((c = in.read()) != -1)
    {
      crc.update(c);
    }
    return crc.getValue();
  }

  public static long checksumBufferedInputStream(String filename) throws IOException
  {
    InputStream in = new BufferedInputStream(new FileInputStream(filename));
    CRC32 crc = new CRC32();

    int c;
    while ((c = in.read()) != -1)
    {
      crc.update(c);
    }
    return crc.getValue();
  }

  public static long checksumRandomAccessFile(String filename) throws IOException
  {
    RandomAccessFile file = new RandomAccessFile(filename, "r");
    long length = file.length();
    CRC32 crc = new CRC32();

    for (long p = 0; p < length; p++)
    {
      file.seek(p);
      int c = file.readByte();
      crc.update(c);
    }
    return crc.getValue();
  }

  public static long checksumMappedFile(String filename) throws IOException
  {
    FileInputStream in = new FileInputStream(filename);
    FileChannel channel = in.getChannel();

    CRC32 crc = new CRC32();
    int length = (int) channel.size();

    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);

    for (int p = 0; p < length; p++)
    {
      int c = buffer.get(p);
      crc.update(c);
    }
    return crc.getValue();
  }

  public static void main(String[] args) throws IOException
  {
    String filename = "/Users/jeffreyawest/Pictures/PIX-FINAL/Sky Diving/AFF1.MPG";
    long start;
    long crcValue;
    long end;

//    System.out.println("Input Stream:");
//    start = System.currentTimeMillis();
//    crcValue = checksumInputStream(filename);
//    end = System.currentTimeMillis();
//    System.out.println(Long.toHexString(crcValue));
//    System.out.println((end - start) + " milliseconds");

    System.out.println("Buffered Input Stream:");
    start = System.currentTimeMillis();
    crcValue = checksumBufferedInputStream(filename);
    end = System.currentTimeMillis();
    System.out.println(Long.toHexString(crcValue));
    System.out.println((end - start) + " milliseconds");

//    System.out.println("Random Access File:");
//    start = System.currentTimeMillis();
//    crcValue = checksumRandomAccessFile(filename);
//    end = System.currentTimeMillis();
//    System.out.println(Long.toHexString(crcValue));
//    System.out.println((end - start) + " milliseconds");

    System.out.println("Mapped File:");
    start = System.currentTimeMillis();
    crcValue = checksumMappedFile(filename);
    end = System.currentTimeMillis();
    System.out.println(Long.toHexString(crcValue));
    System.out.println((end - start) + " milliseconds");
  }
}

   
    