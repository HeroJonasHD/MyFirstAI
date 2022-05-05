package ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<>();
        System.out.println("Hello there!");
        Brain[] father = new Brain[1];
        threads.add(new Thread(() -> father[0] = new Brain(false, 25, null, "Dein Dad")));
        threads.forEach(Thread::start);
        //father.addRandomConnection();
        //final Brain[] child = new Brain[1];
        //new Thread(() -> child[0] = new Brain(false, 0, father[0], "Deine Mum")).start();
        Scanner scanner = new Scanner(System.in);
        boolean next = true;
        while(next) {
            String string = scanner.nextLine();
            switch (Commands.valueOf(string.toUpperCase(Locale.ROOT))) {
                case STOP -> next = false;
                //case "getchildinfo" -> System.out.println(child[0]);
                case INFO -> System.out.println(father[0]);
                case SINGNALS -> System.out.println(father[0].getSignals());
                case HELP -> System.out.println(Arrays.toString(Commands.values()).toLowerCase(Locale.ROOT));
                case STARTBRAIN -> {
                    Thread thread = new Thread(() -> father[0].startBrain());
                    threads.add(thread);
                    thread.start();
                }
            }

        }

        threads.forEach(Thread::stop);

        System.out.println(father[0]+ "\n\n\n\n\n\n" /*+ child[0]*/);

    }

    enum Commands {
        STOP("Stop"),
        INFO("braininfo"),
        SINGNALS("returns all Current Signals"),
        HELP("returns all help informations"),
        STARTBRAIN("do Start Brain");

        public final String info;

        Commands(String info) {
            this.info = info;
        }
    }
}
