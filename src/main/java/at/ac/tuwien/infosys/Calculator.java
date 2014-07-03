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

    private String directoryPath = "";
    private static final Logger logger = LogManager.getLogger(Calculator.class.getName());
    private Tasks tasks;
    private List<Task> runningTasks = new ArrayList<>();
    private List<Task> finishedTasks = new ArrayList<>();

    //TODO determine typical baseload
    private Double baseload = 5.0;
    private Double lowerBound = 0.7;
    private Double upperBound = 1.5;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        tasks = new Tasks();

        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        if (data.get("tasks")!=null) {
            runningTasks = (ArrayList<Task>) data.get("tasks");
        }



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

                //more than 3 CPU
                if (Runtime.getRuntime().availableProcessors() > 2) {
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
                } else {
                    if (Runtime.getRuntime().availableProcessors() > 1) {
                        if (isEnoughCPUavailableForLessThanThreeCores(currentTask)) {
                            //TODO implement https://en.wikipedia.org/wiki/Gustafson%27s_law
                            //actual CPU = normal CP/cores
                            Double actualCPU = currentTask.getCpu()/(Runtime.getRuntime().availableProcessors()*2);
                            currentTask.setTimeLeft(getNormalDistribution(currentTask.getDuration()*2));
                            currentTask.setActualCPU(getNormalDistribution(actualCPU));

                            runningTasks.add(currentTask);
                        } else {
                            writeBuffer+=line + "\n";
                        }
                    } else {
                        if (isEnoughCPUavailableForLessThanThreeCores(currentTask)) {
                            //TODO implement https://en.wikipedia.org/wiki/Gustafson%27s_law
                            //actual CPU = normal CP/cores
                            Double actualCPU = currentTask.getCpu()/(Runtime.getRuntime().availableProcessors()*4);
                            currentTask.setTimeLeft(getNormalDistribution(currentTask.getDuration()*4));
                            currentTask.setActualCPU(getNormalDistribution(actualCPU));

                            runningTasks.add(currentTask);
                        } else {
                            writeBuffer+=line + "\n";
                        }
                    }
                }







            }

            data.put("tasks", runningTasks);

            FileUtils.writeStringToFile(taskQueueFile, writeBuffer);

            logger.trace("Invoke lookbusy: " + " lookbusy -c " + calculateOverallCPU() + " -n " + Runtime.getRuntime().availableProcessors());
            Process p = Runtime.getRuntime().exec(" lookbusy -c " + Math.round(calculateOverallCPU()) + " -n " +  Runtime.getRuntime().availableProcessors());
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
        //TODO implement https://en.wikipedia.org/wiki/Gustafson%27s_law

        if ((calculateOverallCPU() + (task.getCpu()/Runtime.getRuntime().availableProcessors()))>100) {
            return false;
        }
        return true;
    }

    private Boolean isEnoughCPUavailableForLessThanThreeCores(Task task) {
        //TODO implement https://en.wikipedia.org/wiki/Gustafson%27s_law

        if ((calculateOverallCPU() + (task.getCpu()/4))>100) {
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

}
