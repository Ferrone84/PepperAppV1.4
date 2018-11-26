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

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "MainActivity";
    private EditText speakText;
    private TextView listenResultText;
    private Button speakButton;
    private Button listenButton;
    private Say say;
    private String textToSay = "hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this,this);

        speakText = findViewById(R.id.speakText);
        listenResultText = findViewById(R.id.listenResultText);
        speakButton = findViewById(R.id.speakButton);
        listenButton = findViewById(R.id.listenButton);

        speakButton.setOnClickListener(speakButtonListener);
        speakText.addTextChangedListener(speakTextListener);
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
                textToSay = "hello";
            }
            //Log.i(TAG, "changes: " + textToSay);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private View.OnClickListener speakButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Run the action asynchronously.
            Future<Void> sayFuture = say.async().run();
            sayFuture.andThenConsume(Qi.onUiThread((Consumer<Void>) ignore -> {
                Toast.makeText(MainActivity.this, "Say action finished with success.", Toast.LENGTH_SHORT).show();
            }));
        }
    };

    public void onRobotFocusGained(QiContext qiContext) {

        say = SayBuilder.with(qiContext) // Create a builder with the QiContext.
                .withText(textToSay) // Specify the action parameters.
                .build();

        Log.i(TAG, "textToSay: " + textToSay);
        /*
        // Create a new say action.
        Say say = SayBuilder.with(qiContext) // Create the builder with the context.
                .withText("Hello human!") // Set the text to say.
                .build(); // Build the say action.

        // Execute the action.
        say.run();

        // Create a phrase set.
        PhraseSet hello = PhraseSetBuilder.with(qiContext)
                .withTexts("Hello", "Hi", "hello!").build();
        PhraseSet forwards = PhraseSetBuilder.with(qiContext)
                .withTexts("move forwards", "forwards").build();
        PhraseSet backwards = PhraseSetBuilder.with(qiContext)
                .withTexts("move backwards", "backwards").build();
        PhraseSet stop = PhraseSetBuilder.with(qiContext)
                .withTexts("stop moving", "stop").build();

        // Build the action.
        Listen listen = ListenBuilder.with(qiContext)
                .withPhraseSets(hello, forwards, backwards, stop)
                .build();

        // Run the action synchronously.
        ListenResult listenResult = listen.run();
        Log.i(TAG, "Heard phrase: " + listenResult.getHeardPhrase().getText()); // Prints "Heard phrase: forwards".
        PhraseSet matchedPhraseSet = listenResult.getMatchedPhraseSet(); // Equals to forwards.
        for (Phrase phrase : matchedPhraseSet.getPhrases()) {
            Log.i(TAG, "Matched phrase: " + phrase.getText());
        }*/
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
}
