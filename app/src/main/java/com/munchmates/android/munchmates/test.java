package com.munchmates.android.munchmates;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class test extends AppCompatActivity {

    private Button awesomeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awesomeButton = new Button(this);

        awesomeButton.setOnClickListener(new AwesomeButtonClick());
    }

    private void awesomeButtonClicked() {
        awesomeButton.setText("AWESOME!");
    }

    class AwesomeButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            awesomeButtonClicked();
        }
    }
}