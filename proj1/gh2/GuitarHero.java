package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static final int KEYBOARDNUM = 37;

    static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    static GuitarString[] board = new GuitarString[KEYBOARDNUM];

    private static double computeFrequency(int i) {
        return 440 * Math.pow(2, (double) (i - 24) / 12);
    }

    private static void CreateBoard() {
        for (int i = 0; i < KEYBOARDNUM; i++) {
            double frequency = computeFrequency(i);
            board[i] = new GuitarString(frequency);
        }
    }

    public static void main(String[] args) {
        CreateBoard();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int keyIndex = keyboard.indexOf(key);
                if (keyIndex == -1) {
                    continue;
                }
                board[keyIndex].pluck();
            }

            double sample = 0.0;
            for (int i = 0; i < KEYBOARDNUM; i++) {
                sample += board[i].sample();
            }

            StdAudio.play(sample);

            for (int i = 0; i < KEYBOARDNUM; i++) {
                board[i].tic();
            }
        }

    }
}
