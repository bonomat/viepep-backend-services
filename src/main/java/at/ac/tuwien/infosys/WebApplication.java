package at.ac.tuwien.infosys;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.core.Application;

/**
 * Created by Philipp Hoenisch on 6/10/14.
 */
public class WebApplication extends Application {
    // Base URI the HTTP server will listen to
    public static final String BASE_URI = "http://localhost:8080/";
    public static int myCounter = 0;
    private static Thread calcThread;

    /**
     * Default constructor
     */
    public WebApplication() {
        super();
    }

    /**
     * Initialize the web application
     */
    @PostConstruct
    public static void initialize() {
        Calculator calculator = new Calculator();
        calcThread = new Thread(calculator);
        calcThread.start();
    }

    @PreDestroy
    public static void destrouy() {
        calcThread.interrupt();
    }


    /**
     * Define the set of "Resource" classes for the javax.ws.rs.core.Application
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();
        set.add(Service.class);
        return set;
    }

}