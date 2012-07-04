package fwonce.xia.misc;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import fwonce.xia.constant.Uris;

/**
 * ±È¿˙MP3
 * 
 * @author Floyd Wan
 */
public class DirectoryNavigator implements Iterable<File> {

  private File dir;
  private List<File> fileList;
  private Integer fileCount;
  
  public DirectoryNavigator() {
    try {
      dir = new File(Uris.absPath);
      if (dir == null || !dir.isDirectory()) {
        throw new IllegalArgumentException(Uris.absPath
            + " is not a directory");
      }
      File[] files = dir.listFiles();
      this.fileList = Arrays.asList(files);
      this.fileCount = files.length;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Integer getFileCount() {
    return fileCount;
  }

  @Override
  public Iterator<File> iterator() {
    return fileList.iterator();
  }
}
