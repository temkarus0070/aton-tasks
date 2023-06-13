package FontOffice.First;

import java.util.List;
import java.util.Optional;

public class Actor extends Thread {
    private List<ActorSentence> sentences;
    public Actor(String name, List<ActorSentence> sentences) {
        super(() -> {
            Thread currentThread = Thread.currentThread();
            synchronized (currentThread) {
                try {
                    currentThread.wait();
                    for (ActorSentence sentence : sentences) {
                        System.out.printf("%s: %s\n", name, sentence.getSentence());
                        Optional<Actor> nextActor = sentence.getNextActor();
                        if (nextActor.isPresent()) {
                            Actor actor = nextActor.get();
                            synchronized (actor) {
                                        actor.notify();
                            }
                        }
                        if (!sentence.equals(sentences.get(sentences.size() - 1))) {
                            currentThread.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.sentences=sentences;
        setName(name);
    }

    public List<ActorSentence> getSentences() {
        return sentences;
    }
}
