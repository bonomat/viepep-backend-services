package at.ac.tuwien.infosys;


import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Calculator implements Runnable {

    private String directoryPath = "/Users/hochi/workspace/";
    private static final Logger logger = LogManager.getLogger(Calculator.class.getName());
    private Tasks tasks;
    private List<Task> runningTasks = new ArrayList<>();

    //TODO determine typical baseload
    private Double baseload = 10.0;
    private Double lowerBound = 0.7;
    private Double upperBound = 1.5;

    public Calculator() {
        tasks = new Tasks();
    }


    @Override
    public void run() {
        logger.trace("Start calculator");

        while (true) {
            try {
                File taskQueueFile = new File(directoryPath + "taskqueue.txt");
                File finishedFile = new File(directoryPath + "finished.txt");



                logger.trace("Import new invocations");

                //Add new Tasks
                String writeBuffer = "";
                for (String line : FileUtils.readLines(taskQueueFile, "UTF-8")) {
                    String[] parts = line.split(";");
                    Task currentTask = tasks.getTask(Integer.parseInt(parts[0]));
                    currentTask.setUUID(parts[1]);

                    if (isEnoughCPUavailable(currentTask)) {

                        //TODO implement https://en.wikipedia.org/wiki/Gustafson%27s_law
                        //actual CPU = normal CP/cores
                        Double actualCPU = currentTask.getCpu()/Runtime.getRuntime().availableProcessors();

                        currentTask.setTimeLeft(getNormalDistribution(currentTask.getDuration()));
                        currentTask.setActualCPU(getNormalDistribution(actualCPU));

                        runningTasks.add(currentTask);
                    } else {
                       writeBuffer+=line + "\n";
                    }
                }

                FileUtils.writeStringToFile(taskQueueFile, writeBuffer);


                logger.trace("Invoke lookbusy with " + calculateOverallCPU() + " on " + Runtime.getRuntime().availableProcessors() + " cores");
                Process p = Runtime.getRuntime().exec(" lookbusy -c " + calculateOverallCPU() + " -n " +  Runtime.getRuntime().availableProcessors());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    return;
                }

                p.destroy();

                logger.trace("Cleanup runningList");
                List<Task> cleanList = new ArrayList<>();
                for (Task task : runningTasks) {
                    if (task.getTimeLeft()-5<0) {
                        FileUtils.writeStringToFile(finishedFile, task.getUUID() + "\n", true);
                    } else {
                       task.setTimeLeft(task.getTimeLeft()-5);
                       cleanList.add(task);
                    }
                }
                runningTasks = cleanList;



            } catch (IOException e) {
                e.printStackTrace();
            }

            if (Thread.interrupted()) {
                return;
            }

        }




    }


    private Boolean isEnoughCPUavailable(Task task) {
        //TODO respect multiple cores
        //TODO implement https://en.wikipedia.org/wiki/Gustafson%27s_law
        if (calculateOverallCPU() + (task.getCpu()/Runtime.getRuntime().availableProcessors())>100) {
            return false;
        }
        return true;
    }


    private Double calculateOverallCPU() {
        Double overallCPU = baseload;
        for (Task task : runningTasks) {
            overallCPU+=task.getActualCPU();
        }
        return overallCPU;
    }

    private Double getNormalDistribution(Double cpu) {
        while (true) {
            NormalDistribution n = new NormalDistribution(cpu, cpu/10);
            Double result = Math.abs(n.sample());
            if ((result>(cpu*lowerBound)) && (result<(cpu*upperBound))) {
                return result;
            }
        }
    }
}
