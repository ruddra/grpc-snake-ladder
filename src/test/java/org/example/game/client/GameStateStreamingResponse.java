package org.example.game.client;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.stub.StreamObserver;
import org.example.models.Die;
import org.example.models.GameState;
import org.example.models.Player;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameStateStreamingResponse implements StreamObserver<GameState> {
    private CountDownLatch latch;
    private StreamObserver<Die> dieStreamObserver;

    public GameStateStreamingResponse(CountDownLatch latch) {
        this.latch = latch;

    }

    @Override
    public void onNext(GameState gameState) {
        List<Player> list = gameState.getPlayerList();
        list.forEach(p -> System.out.println(p.getName() + ":" + p.getPosition()));
        boolean winner = list.stream()
                .anyMatch(p -> p.getPosition() == 100);
        if (winner){
            System.out.println("Game Over");
            this.dieStreamObserver.onCompleted();
        } else {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            this.roll();
        }

        System.out.println("--------------");
    }

    @Override
    public void onError(Throwable throwable) {
    this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        this.latch.countDown();
    }

    public void setDieStreamObserver(StreamObserver<Die> streamObserver){
        this.dieStreamObserver = streamObserver;
    }
    public void roll(){
        int dieV = ThreadLocalRandom.current().nextInt(1,7);
        Die die = Die.newBuilder().setValue(dieV).build();
        this.dieStreamObserver.onNext(die);

    }
}
