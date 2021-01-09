package medium;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.greghaines.jesque.json.ObjectMapperFactory;
import net.greghaines.jesque.worker.MapBasedJobFactory;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerEvent;
import net.greghaines.jesque.worker.WorkerImpl;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.greghaines.jesque.utils.JesqueUtils.entry;
import static net.greghaines.jesque.utils.JesqueUtils.map;

@Slf4j
public class SimpleConsumer {
    public static void main(String[] args) {

        ObjectMapper objectMapper = ObjectMapperFactory.get();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
        ExecutorService executorService = Executors.newScheduledThreadPool(3);
        int workers = 3;
        for (int i = 0; i < workers; i++) {
            Worker worker = getNewWorker();
            executorService.submit(worker);
        }
    }

    private static Worker getNewWorker() {
        final Worker worker = new WorkerImpl(JesqueConfig.getConfig(),
                Collections.singletonList("simple"),
                new MapBasedJobFactory(map(entry("SimpleWork", SimpleWork.class))));
        addListeners(worker);
        return worker;
    }

    private static void addListeners(Worker worker) {

        worker.getWorkerEventEmitter().addListener((event, worker1, queue, job, runner, result, t) ->
                log.debug("success in executing the following the job {}", job.getArgs(), t), WorkerEvent.JOB_SUCCESS);

        worker.getWorkerEventEmitter().addListener((event, worker2, queue, job, runner, result, t) ->
                log.error("failure in executing the following the job {}", job.getArgs(), t), WorkerEvent.JOB_FAILURE);

        worker.getWorkerEventEmitter().addListener((event, worker3, queue, job, runner, result, t) ->
                log.error("error in the following worker {}", worker3.getName(), t), WorkerEvent.WORKER_ERROR);

        worker.getWorkerEventEmitter().addListener((event, worker4, queue, job, runner, result, t) ->
                log.debug("worker : {} has stopped", worker4.getName(), t), WorkerEvent.WORKER_STOP);
    }
}
