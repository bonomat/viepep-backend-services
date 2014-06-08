
package at.ac.tuwien.infosys;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;

import java.io.IOException;


public class Main {



    protected static HttpServer startServer() throws IOException {
        ResourceConfig resourceConfig = new PackagesResourceConfig("at.ac.tuwien.infosys");

        System.out.println("Starting grizzly2...");
        return GrizzlyServerFactory.createHttpServer("http://0.0.0.0:8080", resourceConfig);
    }
    
    public static void main(String[] args) throws IOException {

        Calculator calculator = new Calculator();
        Thread calcThread = new Thread(calculator);
        calcThread.start();


        HttpServer httpServer = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...",
                "http://localhost:8080"));
       // System.in.read();
       // httpServer.stop();
       // calcThread.interrupt();
    }    
}
