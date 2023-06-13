import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    public static String text = """
                                Joey: Hey, hey.
                                Chandler: Hey.
                                Phoebe: Hey.
                                Chandler: And this from the cry-for-help department. Are you wearing makeup?
                                Joey: Yes, I am. As of today, I am officially Joey Tribbiani, actor slash model.
                                Chandler: That's so funny, ‘cause I was thinking you look more like Joey Tribbiani, man slash woman.
                                Phoebe: What were you modeling for?
                                Joey: You know those posters for the City Free Clinic?
                                Monica: Oh, wow, so you're gonna be one of those “healthy, healthy, healthy guys"?
                                Phoebe: You know, the asthma guy was really cute.
                                Chandler: Do you know which one you're gonna be?
                                Joey: No, but I hear lyme disease is open, so... (crosses fingers)
                                Chandler: Good luck, man. I hope you get it.
                                Joey: Thanks.""";


    public static void main(String[] args) {
        List<Actor> actors = SetcomTextParser.parseText(text);
        if (!actors.isEmpty()) {
            actors.forEach(Thread::start);
            while (!actors.stream()
                          .allMatch(e -> e.getState() == Thread.State.WAITING)) {

            }
            Actor firstActor = actors.get(0);
            synchronized (firstActor) {
                firstActor.notify();
            }
        }
    }
}



class Actor extends Thread {
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

class ActorSentence {
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



class SetcomTextParser {

    public static List<Actor> parseText(String text) {
        List<Actor> actors = new ArrayList<>();
        List<SetcomSentence> actorsSentences = Arrays.stream(text.split("\n"))
                                                     .map(str -> {
                                                         int delimerPos = str.indexOf(":");
                                                         return new SetcomSentence(str.substring(0, delimerPos),
                                                                                   str.substring(delimerPos + 1));
                                                     })
                                                     .toList();
        if (!actorsSentences.isEmpty()) {
            Map<String, Actor> actorsByName = new HashMap<>();
            actorsSentences.stream()
                           .map(e -> e.actorName)
                           .distinct()
                           .forEach(actorName -> {
                               actorsByName.put(actorName, new Actor(actorName, new ArrayList<>()));
                           });

            for (String actorName : actorsSentences.stream()
                                                   .map(e -> e.actorName)
                                                   .distinct()
                                                   .toList()) {
                Actor actor = actorsByName.get(actorName);
                actors.add(actor);
            }
            for (int i = 0; i < actorsSentences.size() - 1; i++) {
                SetcomSentence setcomSentence = actorsSentences.get(i);
                SetcomSentence nextSetcomSentence = actorsSentences.get(i + 1);
                if (setcomSentence.actorName.equals(nextSetcomSentence.actorName)) {

                } else {
                    Actor actor = actorsByName.get(setcomSentence.actorName);
                    Actor nextActor = actorsByName.get(nextSetcomSentence.actorName);
                    actor.getSentences()
                         .add(new ActorSentence(setcomSentence.text, Optional.of(nextActor)));
                }
            }
            SetcomSentence lastSentence = actorsSentences.get(actorsSentences.size() - 1);
            actorsByName.get(lastSentence.actorName)
                        .getSentences()
                        .add(new ActorSentence(lastSentence.text, Optional.empty()));

        }
        return actors;
    }
}

class SetcomSentence {

    String actorName;
    String text;

    public SetcomSentence(String actorName, String text) {
        this.actorName = actorName;
        this.text = text;
    }
}



