import java.io.IOException;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50052).addService(new CalculatorServiceImpl()).build();
        server.start();
        System.out.println("Server running ...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));
        server.awaitTermination();
    }
}
