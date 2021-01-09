package example;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleWork implements Runnable {
    private SimpleJob simpleJob;

    public SimpleWork(SimpleJob simpleJob) {
        this.simpleJob = simpleJob;
    }

    @Override
    public void run() {
        log.info("logging the contents of the simple job. SimpleId : {}", simpleJob.getSimpleId());
    }
}
