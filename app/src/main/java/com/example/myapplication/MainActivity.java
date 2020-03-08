package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.retrofit.IREadAPI;
import com.example.myapplication.retrofit.ModelValue;
import com.example.myapplication.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RCAdapter adapter;
    CenterLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rc_main);
        manager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
//        final LinearLayoutManager manager = new SpeedyLinearLayoutManager(getApplicationContext(), SpeedyLinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(manager);
        final List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            stringList.add("possiton " + i);
        }
        adapter = new RCAdapter(stringList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstItemVisible = manager.findFirstVisibleItemPosition();
                if (firstItemVisible != 0 && firstItemVisible % stringList.size() == 0) {
                    recyclerView.getLayoutManager().scrollToPosition(0);
                }
            }
        });

        Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                autoScroll();

                final int[] x = {recyclerView.getLayoutManager().getWidth() / 8, 3000};
                IREadAPI api = RetrofitClient.getInstance().create(IREadAPI.class);
                api.getValue("22")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ModelValue>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(final ModelValue value) {
                                autoScroll(x, value.getValue());
                                TextView tv = recyclerView.getLayoutManager()
                                        .findViewByPosition((manager.findFirstVisibleItemPosition() + manager.findLastVisibleItemPosition()) / 2)
                                        .findViewById(R.id.tv);

//                                int position = (manager.findFirstVisibleItemPosition() + manager.findLastVisibleItemPosition()) / 2;
//                                if(position<stringList.size()){
//                                    stringList.set( position,value.getValue()+ "");
//                                }else {
//                                    stringList.set( position-stringList.size(),value.getValue()+ "");
//                                }
//                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                x[0]--;
                                x[1] = 3000;
                            }
                        });
            }
        });


    }

    public void autoScroll(final int[] x, final String result) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        new CountDownTimer(x[1], 20) {
                            public void onTick(long millisUntilFinished) {
//                                if (x[0] == 0) {
//
//                                    return;
//                                }
//                                recyclerView.smoothScrollToPosition((manager.findFirstVisibleItemPosition() + manager.findLastVisibleItemPosition()) / 2);

                                recyclerView.scrollBy(x[0], 0);
                            }

                            public void onFinish() {
                                recyclerView.smoothScrollToPosition(5);
                                TextView tv = recyclerView.getLayoutManager().findViewByPosition((manager.findFirstVisibleItemPosition() + manager.findLastVisibleItemPosition()) / 2).findViewById(R.id.tv);
                                tv.setText(result + "");
//                                recyclerView.smoothScrollToPosition((manager.findFirstVisibleItemPosition() + manager.findLastVisibleItemPosition()) / 2);
                            }
                        }.start();
                    }
                });
            }
        });

    }
}
