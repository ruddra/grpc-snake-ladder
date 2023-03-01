package org.example.game.server;

import io.grpc.stub.StreamObserver;
import org.example.models.Die;
import org.example.models.GameServiceGrpc;
import org.example.models.GameState;
import org.example.models.Player;

public class GameService extends GameServiceGrpc.GameServiceImplBase {
    @Override
    public StreamObserver<Die> roll(StreamObserver<GameState> responseObserver) {
        Player client = Player.newBuilder().setName("client").setPosition(0).build();
        Player server = Player.newBuilder().setName("server").setPosition(0).build();

        return new DieStreamRequest(responseObserver, client, server);
    }
}
