package com.jeffwest.scanner;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Revised from apache cocoon

import com.jeffwest.filewrapper.FileWrapper;

import java.io.*;

/**
 * @version $Id: com.jeffwest.scanner.ImageUtils.java 587751 2007-10-24 02:41:36Z vgritsenko $
 */
final public class ImageUtils
{
  public static ImageProperties getJpegProperties(FileWrapper file, int pBufferSize) throws FileNotFoundException, IOException
  {
    byte[] jpegBytes = null;

    BufferedInputStream in = null;
    try
    {
      in = new BufferedInputStream(new FileInputStream(file.getTheFile()), pBufferSize);

      // check for "magic" header
      byte[] buf = new byte[2];

      int count = in.read(buf, 0, 2);

      if (count < 2)
      {
        throw new RuntimeException("Not a valid Jpeg file!");
      }

      if ((buf[0]) != (byte) 0xFF || (buf[1]) != (byte) 0xD8)
      {
        throw new RuntimeException("Not a valid Jpeg file: " + file.getTheFile().getAbsolutePath());
      }

      int width = 0;
      int height = 0;
      char[] comment = null;

      boolean hasDims = false;
      boolean hasComment = false;
      int ch = 0;

      while (ch != 0xDA)
      {
        /* Find next marker (JPEG markers perform with 0xFF) */
        while (ch != 0xFF)
        {
          ch = in.read();
        }

        /* JPEG markers can be padded with unlimited 0xFF's */
        while (ch == 0xFF)
        {
          ch = in.read();
        }

        /* Now, ch contains the value of the marker. */
        int length = 256 * in.read();
        length += in.read();

        if (length < 2)
        {
          throw new RuntimeException("Not a valid Jpeg file!");
        }

        /* Now, length contains the length of the marker. */

        if (ch == 0xDA)
        {
//          System.out.println("Found SOS");
//          System.out.println("length=" + length);
          int numComponents = in.read();
//          System.out.println("numComponents = " + numComponents);

          int bytesToRead = (numComponents * 2) + 3;
          int read = in.read(new byte[bytesToRead]);

          if (read != bytesToRead)
          {
            System.out.println("ERROR!");
          }

          jpegBytes = readImageBytes(in);
        }

        if (ch >= 0xC0 && ch <= 0xC3)
        {
          if (ch == 0xC0)
          {
//            System.out.println("0xC0");
          }
          else
          {
            System.exit(0);
          }

          in.read();
          height = 256 * in.read();
          height += in.read();
          width = 256 * in.read();
          width += in.read();

          int numToRead = length - 2 - 5;
          jpegBytes = new byte[numToRead];
          int bytesRead = in.read(jpegBytes);

          if (bytesRead != numToRead)
          {
            System.out.println("error");
            System.exit(0);
          }

          hasDims = true;
        }
        else if (ch == 0xFE)
        {
          // that's the comment marker
          comment = new char[length - 2];

          for (int foo = 0; foo < length - 2; foo++)
          {
            comment[foo] = (char) in.read();
          }
          hasComment = true;
        }
        else
        {
          in.read(new byte[length - 2]);
        }
      }

      return (new ImageProperties(width, height, comment, "jpeg", jpegBytes));

    }
    finally
    {
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
        }
      }
    }
  }

  private static byte[] readImageBytes(final InputStream in) throws IOException
  {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

    int ch = 0;
    boolean complete = false;
    int lastByte = 0;
    int count = 0;

    try
    {
      do
      {
        ch = in.read();
        count++;

        if (ch == -1)
        {
          complete = true;
          break;
        }

        if (lastByte == 0xFF && ch == 0xD9)
        {
          complete = true;
        }
        else if (ch == 0xFF)
        {
          int ch2 = in.read();
          count++;

          if (ch2 == 0xD9)
          {
            complete = true;
          }
          else
          {
            lastByte = ch2;
            byteOut.write(ch);
            byteOut.write(ch2);
          }
        }
        else
        {
          byteOut.write(ch);
        }

      }
      while (!complete);
    }
    catch (Exception e)
    {
      System.out.println("Size: " + byteOut.size());
      e.printStackTrace();
    }

    return byteOut.toByteArray();
  }
}