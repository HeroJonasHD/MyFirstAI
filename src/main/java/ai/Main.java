package ai;

import java.util.Locale;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        System.out.println("Hello there!");
        Brain[] father = new Brain[1];
        new Thread(() -> father[0] = new Brain(true, 25, null, "Dein Dad")).start();
        //father.addRandomConnection();
        final Brain[] child = new Brain[1];
        new Thread(() -> child[0] = new Brain(false, 0, father[0], "Deine Mum")).start();
        Scanner scanner = new Scanner(System.in);
        boolean next = true;
        while(next) {
            String string = scanner.nextLine();
            switch (string.toLowerCase(Locale.ROOT)) {
                case "stop" -> next = false;
                case "getchildinfo" -> System.out.println(child[0]);
                case "getfatherinfo" -> System.out.println(father[0]);
                case "help" -> System.out.println("stop \ngetChildInfo");
                case "" -> System.out.println("use help for help");
            }
        }


        System.out.println(father[0]+ "\n\n\n\n\n\n" + child[0]);
    }
}
