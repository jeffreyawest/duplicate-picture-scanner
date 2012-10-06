package com.jeffwest.scanner.archive;

import com.jeffwest.scanner.Constants;
import com.jeffwest.scanner.duplicate.FileSet;
import com.jeffwest.filewrapper.FileWrapper;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jeffrey.a.west
 * Date: Oct 27, 2011
 * Time: 7:31:11 PM
 */
public final class ChecksumManager
{
  public static final DecimalFormat SIZE_FORMAT = new DecimalFormat(Constants.SIZE_FORMAT_STRING);
  private static final boolean SOUT = true;
  private static final boolean EVAL_EXTENSIONS = false;
  private static final HashSet<String> extensions;

  private static final boolean SAVE_CHECKSUMS = false;
  private static final String CHECKSUMS_CSV = "SCANNED_CHECKSUMS.CSV";

  private final HashMap<String, FileWrapper> masterFileChecksumMap;
  private final HashMap<String, FileSet> duplicateFileWrappers;

  private HashMap<String, FileWrapper> masterImageChecksumMap;
  private HashMap<String, FileSet> duplicateImageWrappers;

  private boolean duplicateWriterInitialized = false;
  private OutputStream duplicateOut;
  private PrintWriter duplicateWriter;

  private boolean checksumWriterInitialized = false;
  private OutputStream checksumOut;
  private PrintWriter checksumWriter;

  private FileWrapper biggestFile;
  private FileSet biggestFileSet;

  private String duplicateDestination;

  private long duplicatesSpace = 0;
  private boolean moveDuplicates;
  private boolean deleteDuplicates;

  private boolean useExistingChecksums = false;


  static
  {
    extensions = new HashSet<String>();
    extensions.add("JPG");
    extensions.add("JPEG");
    extensions.add("MPG");
    extensions.add("AVI");
    extensions.add("ITHMB");
    extensions.add("MOV");
    extensions.add("PNG");
  }

  public ChecksumManager()
  {
    duplicateFileWrappers = new HashMap<String, FileSet>(541);
    masterFileChecksumMap = new HashMap<String, FileWrapper>(42589);

    duplicateImageWrappers = new HashMap<String, FileSet>(541);
    masterImageChecksumMap = new HashMap<String, FileWrapper>(42589);
  }

  public ChecksumManager(ChecksumManager manager)
  {
    this();
    masterFileChecksumMap.putAll(manager.masterFileChecksumMap);
    masterImageChecksumMap.putAll(manager.masterImageChecksumMap);
  }

  public void initDuplicateFileWriter(String pFilename) throws FileNotFoundException
  {
    duplicateOut = new BufferedOutputStream(new FileOutputStream(pFilename), Constants.ONE_MB / 2);
    duplicateWriter = new PrintWriter(new OutputStreamWriter(duplicateOut));
    duplicateWriterInitialized = true;

    System.out.println("initialized duplicate io writer with path=" + (new File(pFilename).getAbsolutePath()));
  }

  public void initChecksumFileWriter(String pFilename) throws FileNotFoundException
  {
    checksumOut = new BufferedOutputStream(new FileOutputStream(pFilename), Constants.ONE_MB / 2);
    checksumWriter = new PrintWriter(new OutputStreamWriter(checksumOut));
    checksumWriterInitialized = true;

    System.out.println("initialized scanner io writer with path=" + (new File(pFilename).getAbsolutePath()));

  }

  public final void processImageWrapper(final FileWrapper imageFile, boolean quiet)
  {
    try
    {
      if (imageFile.length() == 0)
      {
        return;
      }

      final String checksum = imageFile.getHash();

      if (checksumWriterInitialized)
      {
        writeChecksum(imageFile);
      }

      FileWrapper otherFile = masterFileChecksumMap.get(checksum);

      if (otherFile == null)
      {
        masterFileChecksumMap.put(checksum, imageFile);
      }
      else
      {
        if (otherFile.equals(imageFile))
        {
//          System.out.println("continuing");
          return;
        }

        if (!EVAL_EXTENSIONS || extensions.contains(getFileExtension(otherFile)))
        {

          processDuplicate(otherFile, imageFile, quiet);
        }
        else
        {
          System.out.println("Ignoring DUPLICATE io: " + imageFile.getTheFile().getAbsolutePath());
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }


  public final void processFileWrapper(final FileWrapper file, boolean quiet)
  {
    try
    {
      if (file.length() == 0)
      {
        return;
      }

      final String checksum = file.getHash();

      if (checksumWriterInitialized)
      {
        writeChecksum(file);
      }

      FileWrapper otherFile = masterFileChecksumMap.get(checksum);

      if (otherFile == null)
      {
        masterFileChecksumMap.put(checksum, file);
      }
      else
      {
        if (otherFile.equals(file))
        {
//          System.out.println("continuing");
          return;
        }

        if (!EVAL_EXTENSIONS || extensions.contains(getFileExtension(otherFile)))
        {

          processDuplicate(otherFile, file, quiet);
        }
        else
        {
          System.out.println("Ignoring DUPLICATE io: " + file.getTheFile().getAbsolutePath());
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }


  public HashMap<String, FileWrapper> processDirectory(File pTopDir)
  {
    HashMap<String, FileWrapper> directoryChecksums;

    if (useExistingChecksums)
    {
      try
      {
        File[] checksumFiles = pTopDir.listFiles(new FilenameFilter()
        {
          public boolean accept(final File file, final String string)
          {
            return string.toUpperCase().equals(CHECKSUMS_CSV);
          }
        });

        if (checksumFiles.length == 1)
        {
          System.out.println("Found scanner io=[" + checksumFiles[0].getAbsolutePath() + ']');
          Collection<FileWrapper> fromFile = ChecksumFileManager.readSourceChecksumsFromFile(checksumFiles[0]);
          directoryChecksums = new HashMap<String, FileWrapper>(fromFile.size());

          for (FileWrapper wrapper : fromFile)
          {
            processFileWrapper(wrapper, true);
            directoryChecksums.put(wrapper.getHash(), wrapper);
          }

          return directoryChecksums;
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    File[] files = pTopDir.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isFile();
      }
    });

    directoryChecksums = new HashMap<String, FileWrapper>(files.length);

    System.out.println("Scanning files=[" + files.length + "] directory=[" + pTopDir.getAbsolutePath() + ']');

    for (File file : files)
    {
      if (file.length() <= 0 || !file.exists())
      {
        continue;
      }

      FileWrapper wrapper = new FileWrapper(file);
      processFileWrapper(wrapper, false);
      directoryChecksums.put(wrapper.getHash(), wrapper);
    }

    File[] directories = pTopDir.listFiles(new FileFilter()
    {
      @Override
      public boolean accept(File pathname)
      {
        return pathname.isDirectory();
      }
    });

    for (File directory : directories)
    {
      directoryChecksums.putAll(processDirectory(directory));
    }

    if (SAVE_CHECKSUMS)
    {
      ChecksumFileManager.writeChecksums(directoryChecksums, pTopDir.getAbsolutePath() + '/' + CHECKSUMS_CSV);
    }

    return directoryChecksums;
  }

  private void writeChecksum(final FileWrapper file)
  {
    final String CSVline = file.toCSV();
    checksumWriter.println(CSVline);
    checksumWriter.flush();
  }

  private static String getFileExtension(final FileWrapper wrapper)
  {
    final String absolutePath = wrapper.getTheFile().getAbsolutePath();

    final int index = absolutePath.lastIndexOf('.');

    if (index <= absolutePath.length() && index > 0)
    {
      return absolutePath.substring(index + 1);
    }
    else
    {
      return "";
    }
  }


  private void processDuplicate(FileWrapper file1, FileWrapper file2, boolean quiet)
  {
    FileSet fileSet;

    fileSet = duplicateFileWrappers.get(file1.getHash());

    if (fileSet == null)
    {
      fileSet = new FileSet(file1, file2);
      duplicateFileWrappers.put(file1.getHash(), fileSet);
    }
    else
    {
      fileSet.addFile(file2);
    }

    if (biggestFileSet == null || biggestFileSet.getSpace() < fileSet.getSpace())
    {
      biggestFileSet = fileSet;

    }

    duplicatesSpace += file2.length();

    if (duplicateWriterInitialized)
    {
      writeDuplicate(fileSet);
    }

    if (biggestFile == null)
    {
      biggestFile = fileSet.getFile1();
    }
    else if (biggestFile.length() < fileSet.getSpace())
    {
      biggestFile = fileSet.getFile1();

      System.out.println("New BIGGEST_FILE=[" + biggestFile.getTheFile().getAbsolutePath() + "] Size=[" + SIZE_FORMAT.format(fileSet.getSpace()) + ']');
    }


    if (!quiet)
    {
      System.out.println("==================================================");
      System.out.println("DUPLICATE! FILE1=[" + fileSet.getFile1() + ']');
      System.out.println("Total Duplicate Space: " + SIZE_FORMAT.format(duplicatesSpace));
      if (biggestFile != null)
      {
        System.out.println("BIGGEST size=[" + SIZE_FORMAT.format(biggestFile.getTheFile().length()) + "] path=[" + biggestFile + ']');
      }

      if (biggestFileSet != null)
      {
        System.out.println("+++Biggest File Set file1=[" + biggestFileSet.getFile1() + "] duplicates=[" + biggestFileSet.getFiles().size() + "] space=[" + SIZE_FORMAT.format(biggestFileSet.getSpace()) + ']');
      }
    }

    if (moveDuplicates)
    {
      moveDuplicate(fileSet);
    }
    else if (deleteDuplicates)
    {
      //deleteDuplicate(fileSet);
    }
  }

  private void writeDuplicate(FileSet fileSet)
  {
    duplicateWriter.flush();
  }

  private void moveDuplicate(FileSet fileSet)
  {
    System.out.println("Moving fileSet to [" + duplicateDestination + ']');

    FileWrapper dup = fileSet.getFile1();

    String startingPath = dup.getTheFile().getAbsolutePath();
    String finalPath = duplicateDestination;

    finalPath += ('/' + startingPath.substring(startingPath.indexOf(':') + 1));

    if (SOUT)
    {
      System.out.println("Final Path=[" + finalPath + ']');
    }

    File newFile = new File(finalPath);

    String dirName = newFile.getAbsolutePath().substring(0, newFile.getAbsolutePath().lastIndexOf('/'));
    File newDir = new File(dirName);

    if (SOUT)
    {
      System.out.println("Making directory [" + dirName + ']');
    }

    boolean makeDirs = newDir.mkdirs();
    boolean success = dup.getTheFile().renameTo(newFile);

    if (SOUT)
    {
      System.out.println("MakeDirs=[" + makeDirs + "] Success=[" + success + ']');
    }
  }


  public HashMap<String, FileWrapper> getMasterImageChecksumMap()
  {
    return masterImageChecksumMap;
  }

  public void setMasterImageChecksumMap(HashMap<String, FileWrapper> masterImageChecksumMap)
  {
    this.masterImageChecksumMap = masterImageChecksumMap;
  }

  public HashMap<String, FileSet> getDuplicateImageWrappers()
  {
    return duplicateImageWrappers;
  }

  public void setDuplicateImageWrappers(HashMap<String, FileSet> duplicateImageWrappers)
  {
    this.duplicateImageWrappers = duplicateImageWrappers;
  }

  public HashMap<String, FileWrapper> getMasterFileChecksumMap()
  {
    return masterFileChecksumMap;
  }

  public HashMap<String, FileSet> getDuplicateFileWrappers()
  {
    return duplicateFileWrappers;
  }

  public long getDuplicatesSpace()
  {
    return duplicatesSpace;
  }

  public FileWrapper getBiggestFile()
  {
    return biggestFile;
  }

  public void setMoveDuplicates(boolean moveDuplicates)
  {
    this.moveDuplicates = moveDuplicates;
  }

  public void setDeleteDuplicates(boolean deleteDuplicates)
  {
    this.deleteDuplicates = deleteDuplicates;
  }

  public void setDuplicateDestination(String duplicateDestination)
  {
    this.duplicateDestination = duplicateDestination;
  }

  public void resetDuplicates()
  {
    duplicateFileWrappers.clear();
    duplicatesSpace = 0;
  }

  public void close()
  {
    checksumWriterInitialized = false;
    duplicateWriterInitialized = false;

    checksumWriter.flush();
    duplicateWriter.flush();

    try
    {
      checksumOut.close();
      duplicateOut.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public Set<FileSet> getOrderedDuplicateSet()
  {
    TreeSet<FileSet> set = new TreeSet<FileSet>(new Comparator<FileSet>()
    {
      @Override
      public int compare(FileSet o1, FileSet o2)
      {
        return (int) (o2.getSpace() - o1.getSpace());
      }
    });

    set.addAll(duplicateFileWrappers.values());

    return set;
  }

  public void setUseExistingChecksums(boolean useExistingChecksums)
  {
    this.useExistingChecksums = useExistingChecksums;
  }

  public FileSet getBiggestFileSet()
  {
    return biggestFileSet;
  }
}