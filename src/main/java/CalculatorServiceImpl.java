import br.edu.ifg.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        SumResponse sumResponse = SumResponse.newBuilder().setSumResult(request.getFirstNumber() +
                        request.getSecondNumber())
                .build();
        responseObserver.onNext(sumResponse);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GeometricAverageRequest> geometricAverage(StreamObserver<GeometricAverageResponse> responseObserver) {
        StreamObserver<GeometricAverageRequest> requestObserver = new StreamObserver<GeometricAverageRequest>() {
            int product = 1;
            int count = 0;

            @Override
            public void onNext(GeometricAverageRequest value) {
                product *= value.getNumber();
                count += 1;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                double exponent = 1.0 / count;
                double average = Math.pow(product,exponent);
                responseObserver.onNext(
                        GeometricAverageResponse.newBuilder()
                                .setAverage(average)
                                .build()
                );
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }
}
