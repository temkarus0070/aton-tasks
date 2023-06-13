package FontOffice.First;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetcomTextParser {

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
