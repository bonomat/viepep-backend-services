
package at.ac.tuwien.infosys;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Path("/{task}")
public class Service {

    private String directoryPath = "";
    static final Logger logger = LogManager.getLogger(Service.class.getName());

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String invokeService(@PathParam("task") int task) throws IOException, InterruptedException {

        //TODO add filelock
        File taskQueueFile = new File(directoryPath + "taskqueue.txt");
        if (!taskQueueFile.exists()) {
            taskQueueFile.createNewFile();
        }
        UUID taskId = UUID.randomUUID();

        FileUtils.writeStringToFile(taskQueueFile, task + ";" + taskId + "\n", true);
        logger.trace("Trigger new Task " + task + " with ID " + taskId);


        Integer possibleIterations = 0;
        Boolean finished = false;


        //TODO add countdown for while loop

        while ((possibleIterations<300) && (!finished)) {
            File finishedFile = new File(directoryPath + "finished.txt");
            if (!finishedFile.exists()) {
                finishedFile.createNewFile();
            }
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
