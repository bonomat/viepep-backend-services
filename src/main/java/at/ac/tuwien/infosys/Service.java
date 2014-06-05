
package at.ac.tuwien.infosys;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Path("/service/{task}")
public class Service {

    private String directoryPath = "";
    static final Logger logger = LogManager.getLogger(Service.class.getName());

    @GET
    @Produces("text/plain")
    public String invokeService(@PathParam("task") int task) throws IOException, InterruptedException {


        Task taskType = new Task(0.0, 0.0);

        switch(task) {
            case 1: taskType = new Task(5.0, 10.0); break;
            case 2: taskType = new Task(10.0, 20.0); break;
            case 3: taskType = new Task(25.0, 5.0); break;
        }

        //TODO add filelock
        File taskQueueFile = new File(directoryPath + "taskqueue.txt");
        UUID taskId = UUID.randomUUID();

        FileUtils.writeStringToFile(taskQueueFile, task + ";" + taskId + "\n", true);
        logger.trace("Trigger new Task " + task + " with ID " + taskId);


        Boolean finished = false;

        while (!finished) {
            File finishedFile = new File(directoryPath + "finished.txt");
            for (String line : FileUtils.readLines(finishedFile, "UTF-8")) {
                if (line.contains(taskId.toString())) {
                   finished = true;
                   String fileString = FileUtils.readFileToString(finishedFile);
                   String finalString = fileString.replaceAll(taskId.toString(), "");
                   FileUtils.writeStringToFile(finishedFile, finalString);
                }
            }
            Thread.sleep(1000);
        }

        logger.trace("Finished task with ID: " + taskId);

        return "Hurray!";
    }
}
