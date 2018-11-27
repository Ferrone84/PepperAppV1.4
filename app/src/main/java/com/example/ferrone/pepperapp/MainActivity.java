package com.example.ferrone.pepperapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.Qi;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "MainActivity";
    private String textToSay = "hello";
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this,this);

        EditText speakText = findViewById(R.id.speakText);
        TextView listenResultText = findViewById(R.id.listenResultText);
        Button speakButton = findViewById(R.id.speakButton);
        Button listenButton = findViewById(R.id.listenButton);

        speakButton.setOnClickListener(speakButtonListener);
        speakText.addTextChangedListener(speakTextListener);
        listenButton.setOnClickListener(listenButtonListener);
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this,this);
        super.onDestroy();
    }

    private TextWatcher speakTextListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String result = s.toString();

            if (!result.isEmpty()) {
                textToSay = result;
            }
            else {
                textToSay = "hi";
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private View.OnClickListener speakButtonListener = v -> {
        Future<Say> sayAsync = SayBuilder.with(qiContext) // Create a builder with the QiContext.
                .withText(textToSay) // Specify the action parameters.
                .buildAsync();

        Log.i(TAG, "textToSay: " + textToSay);

        sayAsync.thenConsume(sayFuture -> {
            if (sayFuture.isSuccess()) {
                Log.i(TAG, "sayAsync: SUCCESS");
                sayFuture.get().async().run();
            }
            else {
                Log.i(TAG, "sayAsync: ERROR");
            }
        });
    };

    private View.OnClickListener listenButtonListener = v -> {
        Future<PhraseSet> hello = PhraseSetBuilder.with(qiContext)
                .withTexts("Hello", "Hi", "hello!").buildAsync();

        hello.thenConsume(helloFuture -> {
            if (helloFuture.isSuccess()) {
                Log.i(TAG, "helloFuture: SUCCESS");
                Future<Listen> listenAsync = ListenBuilder.with(qiContext) // Create a builder with the QiContext.
                        .withPhraseSet(helloFuture.get()) // Specify the action parameters.
                        .buildAsync();

                listenAsync.thenConsume(listenFuture -> {
                    if (listenFuture.isSuccess()) {
                        Log.i(TAG, "listenAsync: SUCCESS");
                        listenFuture.get().async().run();
                    } else {
                        Log.i(TAG, "listenAsync: ERROR");
                    }
                });
            }
            else {
                Log.i(TAG, "helloFuture: ERROR");
            }
        });
    };

    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

    }

    @Override
    public void onRobotFocusLost() {
        Log.i(TAG, "onRobotFocusLost: ");
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.e(TAG, "onRobotFocusRefused: "+reason);
    }
}
