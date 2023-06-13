package FontOffice.First;

import java.lang.reflect.Array;
import java.util.Optional;

public class ActorSentence {
    private final String sentence;
    private final Optional<Actor> nextActor;

    public ActorSentence(String sentence, Optional<Actor> nextActor) {
        this.sentence = sentence;
        this.nextActor = nextActor;
    }

    public String getSentence() {
        return sentence;
    }

    public Optional<Actor> getNextActor() {
        return nextActor;
    }
}
