package pers.clare.bufferid.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.bufferid.config.BufferIdConfig;
import pers.clare.bufferid.service.BufferIdService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Api(tags = {"Single Buffer ID產生測試"})
@RestController
@RequestMapping("buffer/single")
public class BufferIdSingleController {
    @Qualifier(BufferIdConfig.Single)
    @Autowired
    private BufferIdService bufferIdService;

    private DecimalFormat formatter = new DecimalFormat("###,###,###,###");

    @ApiOperation("純數字")
    @GetMapping("number")
    public Long number(@ApiParam(value = "預設序號緩衝大小", example = "100")
                       @RequestParam(required = false, defaultValue = "100") final int buffer
            , @ApiParam(value = "ID群組", example = "group")
                       @RequestParam(required = false, defaultValue = "group") final String group
            , @ApiParam(value = "ID前綴", example = "prefix")
                       @RequestParam(required = false, defaultValue = "prefix") final String prefix

    ) {
        bufferIdService.save(group, prefix);
        return bufferIdService.next(buffer, group, prefix);
    }

    @ApiOperation("前綴格式")
    @GetMapping("string")
    public String string(
            @ApiParam(value = "預設序號緩衝大小", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int buffer
            , @ApiParam(value = "ID群組", example = "group")
            @RequestParam(required = false, defaultValue = "group") final String group
            , @ApiParam(value = "ID前綴", example = "prefix")
            @RequestParam(required = false, defaultValue = "prefix") final String prefix
            , @ApiParam(value = "ID長度", example = "20")
            @RequestParam(required = false, defaultValue = "20") final int length

    ) {
        bufferIdService.save(group, prefix);
        return bufferIdService.next(buffer, group, prefix,length);
    }

    @ApiOperation("純數字壓力測試")
    @GetMapping("number/test")
    public String numberTest(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "每個執行緒產生ID數量", example = "1000000")
            @RequestParam(required = false, defaultValue = "1000000") final int count
            , @ApiParam(value = "預設序號緩衝大小", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int buffer
            , @ApiParam(value = "ID群組", example = "group")
            @RequestParam(required = false, defaultValue = "group") final String group
            , @ApiParam(value = "ID前綴", example = "prefix")
            @RequestParam(required = false, defaultValue = "prefix") final String prefix

    ) throws Exception {
        return run(thread, count, buffer, group, prefix, 0, (b, g, p, l) -> bufferIdService.next(b, g, p));
    }

    @ApiOperation("前綴格式壓力測試")
    @GetMapping("string/test")
    public String stringTest(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "每個執行緒產生ID數量", example = "1000000")
            @RequestParam(required = false, defaultValue = "1000000") final int count
            , @ApiParam(value = "預設序號緩衝大小", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int buffer
            , @ApiParam(value = "ID群組", example = "group")
            @RequestParam(required = false, defaultValue = "group") final String group
            , @ApiParam(value = "ID前綴", example = "prefix")
            @RequestParam(required = false, defaultValue = "prefix") final String prefix
            , @ApiParam(value = "ID長度", example = "20")
            @RequestParam(required = false, defaultValue = "20") final int length

    ) throws Exception {
        return run(thread, count, buffer, group, prefix, length, (b, g, p, l) -> bufferIdService.next(b, g, p, l));
    }

    public String run(
            Integer thread
            , Integer count
            , Integer buffer
            , String group
            , String prefix
            , Integer length
            , BufferFunction fun
    ) throws Exception {
        bufferIdService.save(group, prefix);
        ExecutorService executors = Executors.newFixedThreadPool(thread);
        long start = System.currentTimeMillis();
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int t = 0; t < thread; t++) {
            tasks.add(() -> {
                for (int i = 0; i < count; i++) {
                    fun.apply(buffer, group, prefix, length);
                }
                return count;
            });
        }
        long total = 0;
        List<Future<Integer>> futures = executors.invokeAll(tasks);
        for (Future<Integer> f : futures) {
            total += f.get();
        }
        long ms = System.currentTimeMillis() - start;
        executors.shutdown();
        return "\nID格式:" + fun.apply(buffer, group, prefix, length) + "\nID總數量；" + formatter.format(total) + "\n花費總時間：" + ms + " ms\n平均：" + formatter.format(total * 1000L / ms) + "/s";
    }
}

