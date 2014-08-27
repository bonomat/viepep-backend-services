package at.ac.tuwien.infosys;


import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class Calculator implements Job {

    //TODO adopt to system environment --> it is suggested to use the user's home folder as basefolder e.g. /home/ubuntu/
    private String directoryPath = "/home/ubuntu/";
    //private String directoryPath = "";
    private static final Logger logger = LogManager.getLogger(Calculator.class.getName());
    private Tasks tasks;
    private List<Task> runningTasks = new ArrayList<>();
    private List<Task> finishedTasks = new ArrayList<>();
    private Integer availableProcessors = 0;

    //TODO determine typical baseload
    private Double baseload = 0.0;
    private Double lowerBound = 0.95;
    private Double upperBound = 1.05;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        tasks = new Tasks();

        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        if (data.get("tasks")!=null) {
            runningTasks = (ArrayList<Task>) data.get("tasks");
        }

        availableProcessors =  Runtime.getRuntime().availableProcessors();

        logger.trace("Start calculator");

        try {
            File taskQueueFile = new File(directoryPath + "taskqueue.txt");
            File finishedFile = new File(directoryPath + "finished.txt");

            createFilesWhenTheyAreInexistant(taskQueueFile, finishedFile);


            logger.trace("Cleanup runningList");
            List<Task> cleanList = new ArrayList<>();
            for (Task task : runningTasks) {
                if (task.getTimeLeft()-5<0) {
                    finishedTasks.add(task);
                } else {
                    task.setTimeLeft(task.getTimeLeft()-5);
                    cleanList.add(task);
                }
            }
            runningTasks = cleanList;

            logger.trace("Import new invocations");

            String writeBuffer = "";
            for (String line : FileUtils.readLines(taskQueueFile, "UTF-8")) {
                String[] parts = line.split(";");
                Task currentTask = tasks.getTask(Integer.parseInt(parts[0]));
                currentTask.setUUID(parts[1]);

                if (isEnoughCPUavailable(currentTask)) {
                    if (parts[2].equals("plain")) {
                        currentTask.setActualCPU(currentTask.getCpu() / availableProcessors);
                        currentTask.setTimeLeft(currentTask.getDuration());
                    } else {
                        currentTask.setActualCPU(getNormalDistribution(currentTask.getCpu() / availableProcessors));
                        currentTask.setTimeLeft(getNormalDistribution(currentTask.getDuration()));
                    }
                    currentTask.setId(parts[0]);
                    runningTasks.add(currentTask);
                } else {
                    writeBuffer+=line + "\n";
                }
            }

            data.put("tasks", runningTasks);

            FileUtils.writeStringToFile(taskQueueFile, writeBuffer);


            String runningProcesses = "";
            for (Task task: runningTasks) {
                runningProcesses+= "typ: " + task.getId() + ";cpu:" + task.getActualCPU()*availableProcessors + ";remainingTime:" + task.getTimeLeft() + "\n";
            }

            logger.trace("Available resources: " + availableProcessors*100 + "\n" +
                    "Running processes: " + runningTasks.size() + "\n" +
                    "Used power: " + calculateOverallCPU()*availableProcessors + "\n" +
                    "Running processes:" + "\n" + runningProcesses + "\n" +
                    "Waiting processes: " + "\n" + writeBuffer );

            logger.trace("Invoke lookbusy: " + " /usr/local/bin/lookbusy -c " + calculateOverallCPU() + " -n " + Runtime.getRuntime().availableProcessors());
            Process p = Runtime.getRuntime().exec(" /usr/local/bin/lookbusy -c " + Math.round(calculateOverallCPU()) + " -n " +  availableProcessors);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return;
            }

            logger.trace("\"Inform\" web-services that they are finished.");

            for (Task task: finishedTasks) {
                FileUtils.writeStringToFile(finishedFile, task.getUUID() + "\n", true);
            }

            //Interleaving period to smooth the startup of the next lookbusy iteration
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }

            p.destroy();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Boolean isEnoughCPUavailable(Task task) {
        if ((calculateOverallCPU() + (task.getCpu()/availableProcessors))>100) {
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

    private void createFilesWhenTheyAreInexistant(File taskQueueFile, File finishedFile) throws IOException {
        if (!taskQueueFile.exists()) {
            taskQueueFile.createNewFile();
        }

        if (!finishedFile.exists()) {
            finishedFile.createNewFile();
        }
    }

    private Double durationSpeedup() {
        //based on Gustavonsons Law

        return (1 - 0.7) + availableProcessors * 0.7;

    }



}


