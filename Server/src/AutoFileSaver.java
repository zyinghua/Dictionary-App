

public class AutoFileSaver extends Thread{
    private final String fileName;
    private final Dictionary dict;
    private boolean shutdown;

    public AutoFileSaver(String fileName, Dictionary dict)
    {
        this.fileName = fileName;
        this.dict = dict;
        this.shutdown = false;
    }

    @Override
    public void run()
    {
        while(!shutdown)
        {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.err.println("AutoFileSaver thread interrupted: " + e.getMessage());
            }

            this.dict.writeDictDataToFile();
        }
    }

    public void terminate()
    {
        this.shutdown = true;
    }
}
