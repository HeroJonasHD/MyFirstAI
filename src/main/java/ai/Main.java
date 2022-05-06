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
        threads.add(new Thread(() -> father[0] = new Brain(false, 100, null, "Dein Dad")));
        threads.forEach(Thread::start);
        //father.addRandomConnection();
        //final Brain[] child = new Brain[1];

        Scanner scanner = new Scanner(System.in);
        boolean next = true;
        while(next) {
            String string = scanner.nextLine();
            try {
                Commands command = Commands.valueOf(string.toUpperCase(Locale.ROOT));
                switch (command) {
                    case STOP -> next = false;
                    //case "getchildinfo" -> System.out.println(child[0]);
                    case INFO -> System.out.println(father[0]);
                    case SIGNALS -> System.out.println(father[0].getSignals());
                    case HELP -> System.out.println(Arrays.toString(Commands.values()).toLowerCase(Locale.ROOT));
                    case STARTBRAIN -> {
                        Thread thread = new Thread(() -> father[0].startBrain());
                        threads.add(thread);
                        thread.start();
                    }
                    case MUTATE -> {
                        for (int i = 0; i < 100 ; i++)
                            father[0].mutate();
                    }
                    case COMPLEXITY -> System.out.println(father[0].getComplexity());
                    case JSON -> System.out.println();
                }
            } catch (IllegalArgumentException e) {
                System.out.println("use: \"" + Commands.HELP.toString().toLowerCase(Locale.ROOT) + "\" - " + Commands.HELP.info);
            }


        }

        threads.forEach(Thread::stop);

        System.out.println(father[0] /*+ "\n\n\n\n\n\n" + child[0]*/);

    }

    enum Commands {
        STOP("Stop"),
        INFO("braininfo"),
        SIGNALS("returns all Current Signals"),
        HELP("returns all help informations"),
        STARTBRAIN("do Start Brain"),
        COMPLEXITY("get ComplexityInfo"),
        JSON("reurns Json"),
        MUTATE("mutates our CurrentBrain");

        public final String info;

        Commands(String info) {
            this.info = info;
        }
    }
}
