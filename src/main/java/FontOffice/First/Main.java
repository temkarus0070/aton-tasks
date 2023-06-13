package FontOffice.First;

import java.util.List;

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
