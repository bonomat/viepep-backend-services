package at.ac.tuwien.infosys;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tasks {

    private Map<Integer, Task> tasks = new HashMap<Integer, Task>();


    public Tasks() {
        tasks.put(1, new Task(15.0, 30.0));
        tasks.put(2, new Task(20.0, 80.0));
        tasks.put(3, new Task(30.0, 120.0));
        tasks.put(4, new Task(50.0, 100.0));
        tasks.put(5, new Task(55.0, 10.0));
        tasks.put(6, new Task(65.0, 20.0));
        tasks.put(7, new Task(75.0, 40.0));
        tasks.put(8, new Task(125.0, 20.0));
        tasks.put(9, new Task(125.0, 60.0));
        tasks.put(10, new Task(190.0, 30.0));

    }

    public Task getTask(Integer taskId) {
        if ((taskId>0) && (taskId<11)) {
            return tasks.get(taskId);
        } else {
            return new Task(1.0, 1.0);
        }
    }
}

