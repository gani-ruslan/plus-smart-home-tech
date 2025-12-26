package ru.practicum.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.mapper.ProtoToHubEventMapper;
import ru.practicum.mapper.ProtoToSensorEventMapper;
import ru.practicum.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final CollectorService collectorService;
    private final ProtoToSensorEventMapper sensorMapper;
    private final ProtoToHubEventMapper hubMapper;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("\ngRPC: sensor event received:\n {}", request);
        try {
            var avro = sensorMapper.toAvro(request);
            collectorService.sendSensorEvent(avro);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver, e, "collectSensor");
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("\ngRPC: hub event received:\n {}", request);
        try {
            var avro = hubMapper.toAvro(request);
            collectorService.sendHubEvent(avro);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver, e, "collectHub");
        }
    }

    private void handleError(StreamObserver<?> responseObserver, Exception e, String context) {
        log.error("Error: {} in \n {}", context, e.getMessage(), e);
        responseObserver.onError(new StatusRuntimeException(
                Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)
        ));
    }
}
