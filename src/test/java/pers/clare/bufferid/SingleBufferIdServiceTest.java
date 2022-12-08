package pers.clare.bufferid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.manager.impl.MySQLIdManager;
import pers.clare.bufferid.service.BufferIdService;
import pers.clare.bufferid.service.SingleBufferIdService;
import pers.clare.bufferid.util.IdUtil;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;


@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class SingleBufferIdServiceTest {
    private static final int max = 10;

    private final DataSource dataSource;
    private final BufferIdService bufferIdService;

    private final String prefix = "TEST";

    @Autowired
    public SingleBufferIdServiceTest(DataSource dataSource) {
        this.dataSource = dataSource;
        MySQLIdManager idManager = new MySQLIdManager(dataSource);
        this.bufferIdService = new SingleBufferIdService(idManager);
        idManager.initSchema();
    }

    String create() {
        String id = String.valueOf(System.currentTimeMillis());
        assertEquals(1, bufferIdService.save(id, prefix));
        return id;
    }

    @Test
    @Order(1)
    void save() {
        String id = create();
        assertEquals(0, bufferIdService.save(id, prefix));
    }

    @Test
    @Order(99)
    void remove() {
        String id = create();
        assertEquals(1, bufferIdService.remove(id, prefix));
        assertEquals(0, bufferIdService.remove(id, prefix));
    }

    @Test
    @Order(2)
    void nextLong() {
        String id = create();
        for (int i = 1; i < max; i++) {
            assertEquals(i, bufferIdService.next(id, prefix));
        }
    }

    @Test
    @Order(3)
    void nextString() {
        String id = create();
        int length = 20;
        for (int i = 1; i < max; i++) {
            assertEquals(IdUtil.addZero(prefix, String.valueOf(i), length), bufferIdService.next(id, prefix, length));
        }
    }

    @Test
    @Order(4)
    void nextLongBuffer() {
        String id = create();
        for (int i = 1; i < max; i++) {
            assertEquals(i, bufferIdService.next(1, 3, id, prefix));
        }
    }

    @Test
    @Order(5)
    void nextStringBuffer() {
        String id = create();
        int length = 20;
        for (int i = 1; i < max; i++) {
            assertEquals(IdUtil.addZero(prefix, String.valueOf(i), length), bufferIdService.next(1, 3, id, prefix, length));
        }
    }

    @Test
    @Order(6)
    void raceCondition() throws Exception {
        String id = create();
        int thread = 10;
        int count = 100;
        long sum = multi(thread, () -> {
            long total = 0;
            for (int i = 0; i < count; i++) {
                total += bufferIdService.next(id, prefix);
            }
            return total;
        });
        int height = thread * count;
        assertEquals((height + 1) * height / 2, sum);
    }

    @Test
    @Order(7)
    void raceConditionString() throws Exception {
        String id = create();
        int thread = 10;
        int count = 100;
        Map<String, String> values = new ConcurrentHashMap<>();
        multi(thread, () -> {
            String value;
            for (int i = 0; i < count; i++) {
                value = bufferIdService.next(id, prefix, 20);
                values.put(value, value);
            }
            return 0L;
        });
        assertEquals(thread * count, values.size());
    }

    @Test
    @Order(7)
    void raceConditionLongBuffer() throws Exception {
        String id = create();
        int thread = 10;
        int count = 100;
        Map<Long, Long> values = new ConcurrentHashMap<>();
        multi(thread, () -> {
            Long value;
            for (int i = 0; i < count; i++) {
                value = bufferIdService.next(1, 4, id, prefix);
                values.put(value, value);
            }
            return 0L;
        });
        assertEquals(thread * count, values.size());
    }

    @Test
    @Order(8)
    void raceConditionStringBuffer() throws Exception {
        String id = create();
        int thread = 10;
        int count = 100;
        Map<String, String> values = new ConcurrentHashMap<>();
        multi(thread, () -> {
            String value;
            for (int i = 0; i < count; i++) {
                value = bufferIdService.next(1, 4, id, prefix, 20);
                values.put(value, value);
            }
            return 0L;
        });
        assertEquals(thread * count, values.size());
    }

    static long multi(int thread, Callable<Long> callable) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(thread);
        List<Callable<Long>> tasks = new ArrayList<>();
        for (int i = 0; i < thread; i++) {
            tasks.add(callable);
        }

        long total = 0;
        for (Future<Long> f : executorService.invokeAll(tasks)) {
            total += f.get();
        }
        executorService.shutdown();
        return total;
    }

}
