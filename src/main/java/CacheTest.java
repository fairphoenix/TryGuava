import com.google.common.base.Objects;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by anatoliy on 01.09.14.
 */
public class CacheTest {
    public static class Book {
        private int id;
        private String name;

        public Book(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Book{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final LoadingCache<String, Book> bookCache =
                CacheBuilder.newBuilder()
                        .concurrencyLevel(1)
                        .expireAfterAccess(200, TimeUnit.MINUTES)
                        .ticker(Ticker.systemTicker())
                        .build(new CacheLoader<String, Book>() {
                            @Override
                            public Book load(String s) throws Exception {
                                System.out.println("Create book " + s);
                                return new Book(new Random().nextInt(), s);
                            }
                        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(bookCache.get("Aladdin"));
                    Thread.sleep(1000);
                    System.out.println(bookCache.get("Aladdin"));
                    Thread.sleep(1000);
                    System.out.println(bookCache.get("Jack"));
                    Thread.sleep(1000);
                    System.out.println(bookCache.get("Jack"));
                    System.out.println(bookCache.get("Aladdin"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(bookCache.get("Aladdin"));
                    bookCache.invalidateAll();
                    Thread.sleep(1000);
                    System.out.println(bookCache.get("Aladdin"));

                    Thread.sleep(1000);
                    System.out.println(bookCache.get("Jack"));
                    Thread.sleep(1000);
                    System.out.println(bookCache.get("Jack"));
                    System.out.println(bookCache.get("Aladdin"));
                } catch (InterruptedException e) {
                   e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println(bookCache.get("Aladdin"));

        System.out.println(bookCache.get("Aladdin"));
       // Thread.sleep(10000);

    }
}
