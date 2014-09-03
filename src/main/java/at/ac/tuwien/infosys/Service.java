
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

@Path("/{task}/{normal}/{data}")
public class Service {

    //TODO adopt to system environment --> it is suggested to use the user's home folder as basefolder e.g. /home/ubuntu/
    private String directoryPath = "/home/ubuntu/";
    //private String directoryPath = "";
    static final Logger logger = LogManager.getLogger(Service.class.getName());

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String invokeService(@PathParam("task") int task, @PathParam("normal") String plainModifier, @PathParam("data") String dataModifier) throws IOException, InterruptedException {
        return service(task, plainModifier, dataModifier);
    }



    //plain --> non normal distribution; default is normal distribution
    //@GET
    //@Produces(MediaType.TEXT_HTML)
    //public String invokeService(@PathParam("task") int task, @PathParam("normal") String plainModifier, @PathParam("data") String dataModifier) throws IOException, InterruptedException {
    public String service(int task, String plainModifier, String dataModifier) throws IOException, InterruptedException {

        //TODO add filelock
        File taskQueueFile = new File(directoryPath + "taskqueue.txt");
        if (!taskQueueFile.exists()) {
            taskQueueFile.createNewFile();
        }
        UUID taskId = UUID.randomUUID();

        String normalDistribution = "normal";
        if ("plain".equals(plainModifier)) {
            normalDistribution = "plain";
        }

        String dataSimulation = "nodata";
        if ("data".equals(dataModifier)) {
            dataSimulation = "data";
        }

        FileUtils.writeStringToFile(taskQueueFile, task + ";" + taskId + ";" + normalDistribution + ";" + dataSimulation +  "\n", true);
        logger.trace("Trigger new Task " + task + " with ID " + taskId);


        Integer possibleIterations = 0;
        Boolean finished = false;
        String result = "";


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
                    String[] parts = line.split(";");

                    if ("0".equals(parts[1])) {
                        result = "Hurray!";
                    } else {
                        String dummytext5kb = "A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of existence in this spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents. I should be incapable of drawing a single stroke at the present moment; and yet I feel that I never was a greater artist than now. When, while the lovely valley teems with vapour around me, and the meridian sun strikes the upper surface of the impenetrable foliage of my trees, and but a few stray gleams steal into the inner sanctuary, I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth, a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks, and grow familiar with the countless indescribable forms of the insects and flies, then I feel the presence of the Almighty, who formed us in his own image, and the breath of that universal love which bears and sustains us, as it floats around us in an eternity of bliss; and then, my friend, when darkness overspreads my eyes, and heaven and earth seem to dwell in my soul and absorb its power, like the form of a beloved mistress, then I often think with longing, Oh, would I could describe these conceptions, could impress upon paper all that is living so full and warm within me, that it might be the mirror of my soul, as my soul is the mirror of the infinite God! O my friend -- but it is too much for my strength -- I sink under the weight of the splendour of these visions! A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of existence in this spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents. I should be incapable of drawing a single stroke at the present moment; and yet I feel that I never was a greater artist than now. When, while the lovely valley teems with vapour around me, and the meridian sun strikes the upper surface of the impenetrable foliage of my trees, and but a few stray gleams steal into the inner sanctuary, I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth, a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks, and grow familiar with the countless indescribable forms of the insects and flies, then I feel the presence of the Almighty, who formed us in his own image, and the breath of that universal love which bears and sustains us, as it floats around us in an eternity of bliss; and then, my friend, when darkness overspreads my eyes, and heaven and earth seem to dwell in my soul and absorb its power, like the form of a beloved mistress, then I often think with longing, Oh, would I could describe these conceptions, could impress upon paper all that is living so full and warm within me, that it might be the mirror of my soul, as my soul is the mirror of the infinite God! O my friend -- but it is too much for my strength -- I sink under the weight of the splendour of these visions!A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of existence in this spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents. I should be incapable of drawing a single stroke at the present moment; and yet I feel that I never was a greater artist than now. When, while the lovely valley teems with vapour around me, and the meridian sun strikes the upper surface of the impenetrable foliage of my trees, and but a few stray gleams steal into the inner sanctuary, I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth, a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks, and grow familiar with the countless indescribable forms of the insects and flies, then I feel the presence of the Almighty, who formed us in his own image, and the breath of that universal love which bears and sustains us, as it floats around us in an eternity of bliss; and then, my friend, when darkness overspreads my eyes, and heaven and earth seem to dwell in my soul and absorb its power, like the form of a beloved mistress, then I often think with longing, Oh, would I could describe these conceptions,\n";
                        //200 = ~1MB
                        for (int i = 0; i < (Integer.parseInt(parts[1])*200); i++) {
                            result+=dummytext5kb;
                        }
                    }
                }
            }
            Thread.sleep(1000);
        }

        logger.trace("Finished task with ID: " + taskId);

        return result;
    }

}
