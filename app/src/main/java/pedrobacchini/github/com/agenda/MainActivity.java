package pedrobacchini.github.com.agenda;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static com.jakewharton.rxbinding3.view.RxView.clicks;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable mDisposable = new CompositeDisposable();
    private TextView tvClick1;
    private int countNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvClick1 = findViewById(R.id.tvClick1);

        mDisposable.add(Observable.just("Alpha", "Beta", "Gamma")
                .subscribe(System.out::println, System.out::println, () -> System.out.println("Completado")));

        mDisposable.add(Observable.just(1, 2, 5, 8)
                .map(integer -> integer * 2)
                .subscribe(System.out::println, System.out::println, () -> System.out.println("Completado")));

        mDisposable.add(Observable.just(1, 2, 5, 8)
                .filter(integer -> integer % 2 == 0)
                .subscribe(System.out::println, System.out::println, () -> System.out.println("Completado")));

        Button bClick2 = findViewById(R.id.bClick2);
        TextView tvClick2 = findViewById(R.id.tvClick2);

        mDisposable.add(clicks(bClick2)
                .buffer(700L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map(List::size)
                .map(integer -> {
                    if(integer == 0) return "";
                    else if(integer == 1) return "Single Tap";
                    else return "Double Tap";
                })
                .subscribe(tvClick2::setText, System.out::println));

        TextView tvCount = findViewById(R.id.tvCount1);

        findViewById(R.id.bUp1).setOnClickListener(v -> {
            countNumber++;
            tvCount.setText(countNumber + " ");
        });

        findViewById(R.id.bdown1).setOnClickListener(v -> {
            countNumber--;
            tvCount.setText(countNumber + " ");
        });

        Observable<Integer> clicksUp = RxView.clicks(findViewById(R.id.bUp2)).map(unit -> 1);
        Observable<Integer> clicksDown = RxView.clicks(findViewById(R.id.bdown2)).map(unit -> -1);

        TextView tvCount2 = findViewById(R.id.tvCount2);

        mDisposable.add(Observable.merge(clicksUp, clicksDown)
                .startWith(0)
                .scan((integer, integer2) -> integer + integer2)
                .map(Object::toString)
                .subscribe(tvCount2::setText)
        );


        mDisposable.add(Observable.defer(() -> Observable.just("teste1", "teste2"))
                .subscribe(t -> System.out.println(), throwable -> System.out.println())
        );

        mDisposable.add(Observable.defer(() -> Observable.just("teste1", "teste2"))
                .subscribe()
        );
    }

    @Override
    protected void onDestroy() {
        mDisposable.dispose();
        super.onDestroy();
    }

    private long lastClickTime = 0;

    public void onClick1(View v) {
        Long clickTime = System.currentTimeMillis();
        if(clickTime - lastClickTime < 700L) {
            tvClick1.setText("Double Tap");
            lastClickTime = 0;
        } else {
            tvClick1.setText("Single Tap");
        }
        lastClickTime = clickTime;
    }
}
