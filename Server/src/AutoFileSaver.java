/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

public class AutoFileSaver extends Thread{
    private final Dictionary dict;
    private volatile boolean shutdown;

    public AutoFileSaver(Dictionary dict)
    {
        this.dict = dict;
        this.shutdown = false;
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
                System.err.println("AutoFileSaver thread interrupted: " + e.getMessage());
            }

            this.dict.writeDictDataToFile();
        }

        System.out.println("[Auto File Saver] Finished.");
    }

    public synchronized void terminate()
    {
        this.shutdown = true;
    }
}
