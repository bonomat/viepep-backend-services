package at.ac.tuwien.infosys;


import java.util.HashMap;
import java.util.Map;

public class Tasks {

    private Map<Integer, Task> tasks = new HashMap<Integer, Task>();


    public Tasks() {


        tasks.put(1, new Task(50.0, 40.0, 2));
        tasks.put(2, new Task(75.0, 80.0, 4));
        tasks.put(3, new Task(75.0, 120.0, 6));
        tasks.put(4, new Task(100.0, 40.0, 2));
        tasks.put(5, new Task(120.0, 100.0, 4));

    }

    public Task getTask(Integer taskId) {
        if ((taskId>0) && (taskId<11)) {
            return tasks.get(taskId);
        } else {
            return new Task(1.0, 1.0, 1);
        }
    }
}

