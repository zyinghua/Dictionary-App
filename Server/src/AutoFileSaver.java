/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Utils.UtilsItems;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoFileSaver extends Thread{
    private long period;
    private final Dictionary dict;
    private volatile boolean shutdown;
    private final AtomicInteger verbose;

    public AutoFileSaver(long period, Dictionary dict, AtomicInteger verbose)
    {
        this.period = period;
        this.dict = dict;
        this.shutdown = false;
        this.verbose = verbose;
    }

    @Override
    public void run()
    {
        System.out.println("[Auto File Saver] Running...");

        while(!shutdown)
        {
            try {
                Thread.sleep(this.period);
            } catch (InterruptedException e) {
                if(!shutdown) System.err.println("[Auto File Saver] Thread interrupted, Message: " + e.getMessage());
            }

            this.dict.writeDictDataToFile();

            if(verbose.get() == UtilsItems.VERBOSE_ON_HIGH)
                System.out.println("[Auto File Saver] Dictionary saved to file. Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".");
        }

        System.out.println("[Auto File Saver] Finished.");
    }

    public synchronized void terminate()
    {
        this.shutdown = true;
        this.interrupt();
    }
}
