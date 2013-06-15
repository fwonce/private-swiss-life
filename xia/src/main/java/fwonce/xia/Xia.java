package fwonce.xia;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



import fwonce.xia.misc.DirectoryNavigator;
import fwonce.xia.operate.DocumentFetching;
import fwonce.xia.operate.Id3Writing;

/**
 * 拖虾米网页分析歌曲信息
 * 
 * @author Floyd Wan
 */
public class Xia {

  public static void main(String[] args) throws Throwable {
    DirectoryNavigator navigator = new DirectoryNavigator();
    
    new Thread(new Id3Writing(navigator.getFileCount()), "Id3Writer").start();
    
    Iterator<File> iterator = navigator.iterator();
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10,
        50000L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
    while (iterator.hasNext()) {
      File input = iterator.next();
      threadPoolExecutor.execute(new DocumentFetching(input));
    }
    threadPoolExecutor.shutdown();
  }
}
