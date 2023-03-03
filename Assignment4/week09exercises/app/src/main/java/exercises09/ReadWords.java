package exercises09;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ReadWords {

    public static void main(String[] args) {

        // 9.4.3
        if (args.length == 0 || args[0].equals("3")) {
            readWords
                    .take(100)
                    .forEach(w -> System.out.println(w));
        }

        // 9.4.4
        if (args.length != 0 && args[0].equals("4")) {
            readWords
                    .filter(w -> w.length() >= 22)
                    .forEach(w -> System.out.println(w));
        }

        // 9.4.5
        if (args.length != 0 && args[0].equals("5")) {
            readWords
                    .filter(w -> TestWordStream.isPalindrome(w))
                    .forEach(w -> System.out.println(w));
        }

    }

    // 9.4.1
    public static Observable<String> readWords = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> s) {
            File f = new File(
                    "./src/main/resources/english-words.txt");
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = "";
                while ((line = br.readLine()) != null) {
                    s.onNext(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    });

    // 9.4.2
    public static Observer<String> display = new Observer<String>() {
        @Override
        public void onNext(String t) {
            System.out.println(t);
        }

        @Override
        public void onSubscribe(Disposable d) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(Throwable e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onComplete() {
            // TODO Auto-generated method stub

        };
    };
}
