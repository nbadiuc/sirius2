package com.sirius.excercise;

import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class PumpedExecutorServiceTest {
    
    @Test
    public void testSubmit_shouldExecuteTask() throws Exception {
        PumpedExecutorService executor = new PumpedExecutorService();
        boolean[] done = new boolean[1];
    
        final Future<?> future = executor.submit(() -> {
            done[0] = true;
        });
        future.get();
        executor.shutdownNow();
        
        assertTrue(done[0]);
    }
    
    @Test
    public void testSubmit_shouldMeasureExecutionTime() throws Exception {
        PumpedExecutorService executor = new PumpedExecutorService();
    
        executor.submit(new MeasuringTask()).get();
    
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        executor.shutdownNow();
    }
    
    @Test
    public void testSubmit_shouldLogErrors() throws Exception {
        PumpedExecutorService executor = new PumpedExecutorService();
    
        executor.submit(new ThrowingTask()).get();
        
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        executor.shutdownNow();
    }
    
    @Test
    public void testSubmit_shouldSetThreadNameToClassName() throws Exception {
        PumpedExecutorService executor = null;
    
        final MeasuringTask task = new MeasuringTask();
        executor.submit(task).get();
        
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        executor.shutdownNow();
        System.out.println(task.getClass().getName());
    }
    
    static class MeasuringTask implements Runnable {
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread().getName());
            System.err.println(System.currentTimeMillis() - start);
        }
    }
    
    static class ThrowingTask implements Runnable {
        @Override
        public void run() {
            throw new RuntimeException("/me should be logged");
        }
    }
}
