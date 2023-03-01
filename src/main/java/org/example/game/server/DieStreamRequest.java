package org.example.game.server;

import io.grpc.stub.StreamObserver;
import org.example.models.Die;
import org.example.models.GameState;
import org.example.models.Player;

import java.util.concurrent.ThreadLocalRandom;

public class DieStreamRequest implements StreamObserver<Die> {

    private StreamObserver<GameState> gameStateStreamObserver;
    private Player client;
    private Player server;
    private GameState getGameState(){
        return GameState.newBuilder()
                .addPlayer(this.client)
                .addPlayer(this.server)
                .build();
    }

    public DieStreamRequest(StreamObserver<GameState> gameStateStreamObserver, Player client, Player server) {
        this.gameStateStreamObserver = gameStateStreamObserver;
        this.client = client;
        this.server = server;
    }

    @Override
    public void onNext(Die die) {
        this.client = this.getNewPlayerPosition(this.client, die.getValue());
        if (this.client.getPosition() != 100) {
            this.server = this.getNewPlayerPosition(this.server, ThreadLocalRandom.current().nextInt(1,7));
        }
        this.gameStateStreamObserver.onNext(this.getGameState());

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        this.gameStateStreamObserver.onCompleted();
    }

    private Player getNewPlayerPosition(Player player, int dieValue){
        int position = player.getPosition() + dieValue;
        position = SnakesAndLaddersMap.getPosition(position);
        if(position <= 100) {
            player = player.toBuilder()
                    .setPosition(position)
                    .build();
        }
        return player;

    }
}
