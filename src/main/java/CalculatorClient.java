import br.edu.ifg.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");
        CalculatorClient main = new CalculatorClient();
        main.run();
    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",
                50052).usePlaintext().build();
        //doUnaryCall(channel);
        doClientStreamingCall(channel);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void doUnaryCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub =
                CalculatorServiceGrpc.newBlockingStub(channel);
        SumRequest request = SumRequest.newBuilder().setFirstNumber(10)
                .setSecondNumber(25)
                .build();
        SumResponse response = stub.sum(request);
        System.out.println(request.getFirstNumber() + " + " +
                request.getSecondNumber() + " = " + response.getSumResult());
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<GeometricAverageRequest> requestObserver =
                asyncClient.geometricAverage(new StreamObserver<GeometricAverageResponse>() {
            @Override
            public void onNext(GeometricAverageResponse value) {
                System.out.println("Received a response from the server");
                System.out.println(value.getAverage());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us data");
                latch.countDown();
            }
        });

        for (int i = 1; i < 6; i++) {
            requestObserver.onNext(GeometricAverageRequest.newBuilder()
                    .setNumber(i)
                    .build());
        }
        requestObserver.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}