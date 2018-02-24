package com.acromace.pointer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class CreatePointActivity extends AppCompatActivity implements CreatePointCallbackInterface {

    Server server = new Server();

    @Override
    public void createPointResponse(final boolean success, final String errorMessage)
    {
        if (success) {
            //exit page
        }
        else {
            //show user errorMessage
            Context context = getApplicationContext();
            //CharSequence text = errorMessage;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, errorMessage, duration);
            toast.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_point);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CreatePointActivity self = this;

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.createPoint(new Point(0, 0, "Hello"), self);
            }
        });
    }

}
