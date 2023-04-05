/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Utils.UtilsMsg;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoFileSaver extends Thread{
    private final Dictionary dict;
    private volatile boolean shutdown;
    private final AtomicInteger verbose;

    public AutoFileSaver(Dictionary dict, AtomicInteger verbose)
    {
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
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.err.println("[Auto File Saver] Thread interrupted, Message: " + e.getMessage());
            }

            this.dict.writeDictDataToFile();

            if(verbose.get() == UtilsMsg.VERBOSE_ON_HIGH)
                System.out.println("[Auto File Saver] Dictionary saved to file. Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".");
        }

        System.out.println("[Auto File Saver] Finished.");
    }

    public synchronized void terminate()
    {
        this.shutdown = true;
    }
}
