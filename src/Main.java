import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private final static ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private final static ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private final static ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    private static int textLength = 100_000;
    private static int textAmount = 10_000;

    public static void main(String[] args) {
        String[] text = new String[textAmount];

        Thread generateText = new Thread(() -> {
            for (int i = 0; i < textAmount; i++) {
                text[i] = generateText("abc", textLength);
                try {
                    queueA.put(text[i]);
                    queueB.put(text[i]);
                    queueC.put(text[i]);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        generateText.start();

        Thread aAnalyzer = new Thread(() -> lettersCounter('a', queueA));
        aAnalyzer.start();
        Thread bAnalyzer = new Thread(() -> lettersCounter('b', queueB));
        bAnalyzer.start();
        Thread cAnalyzer = new Thread(() -> lettersCounter('c', queueC));
        cAnalyzer.start();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void lettersCounter(char letter, BlockingQueue<String> text) {
        int counter = 0;
        for (int i = 0; i < textAmount; i++) {
            try {
                String word = text.take();
                for (int j = 0; j < word.length(); j++) {
                    if (word.charAt(j) == letter) {
                        counter++;
                    }
                }
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Наибольшее количество букв " + letter + " : " + counter);
    }
}
