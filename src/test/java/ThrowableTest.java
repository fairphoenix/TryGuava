import com.google.common.base.Throwables;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by anatoliy on 21.09.14.
 */
public class ThrowableTest {
    @Test
    public void testGetCausalChain() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<Throwable> throwables = null;

        Callable<FileInputStream> fileCallable = new
                Callable<FileInputStream>() {
                    @Override
                    public FileInputStream call() throws Exception {
                        return new FileInputStream("Bogus file");
                    }
                };


        assertThat(throwables.get(0).getClass().isAssignableFrom(ExecutionException.class),is(true));
        assertThat(throwables.get(1).getClass().isAssignableFrom(FileNotFoundException.class),is(true));
        executor.shutdownNow();
    }
}
