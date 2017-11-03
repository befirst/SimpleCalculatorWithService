package com.example.simbirsoft.denis.calculatorservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity{

    public static final String CODE_STATUS = "CODE_STATUS";
    public static final String CODE_ERROR = "CODE_ERROR";
    public static final String CODE_SUCCESS = "CODE_SUCCESS";
    public static final String CODE_ERROR_DIVIDE_BY_ZERO = "CODE_ERROR_DIVIDE_BY_ZERO";
    public static final String CODE_RESULT = "CODE_RESULT";

    public static final String CODE_EXTRA_EXPRESSION = "CODE_EXTRA_EXPRESSION";

    public static final String CODE_BROADCAST_ACTION =
            "com.example.simbirsoft.denis.calculateservice.servicebackbroadcast";

    public static final String DIALOG_TAG = "DIALOG_TAG";

    private BroadcastReceiver receiver;
    private EditText expression;
    private CoordinatorLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        expression = findViewById(R.id.edit_text_expression);
        layout = findViewById(R.id.main_coordinator_layout);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFloatingButtonClick(view);
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiveFromService(context, intent);
            }
        };

        IntentFilter filter = new IntentFilter(CODE_BROADCAST_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void onFloatingButtonClick(View view) {
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra(CODE_EXTRA_EXPRESSION, expression.getText().toString());
        startService(intent);
    }

    private void onReceiveFromService(Context context, Intent intent) {
        if (intent.getExtras().containsKey(CODE_STATUS)) {
            switch (intent.getStringExtra(CODE_STATUS)) {
                case CODE_SUCCESS:
                    onServiceSuccess(intent.getDoubleExtra(CODE_RESULT, 0));
                    break;
                case CODE_ERROR:
                    onServiceError();
                    break;
                case CODE_ERROR_DIVIDE_BY_ZERO:
                    onServiceDivideByZero();
                    break;
            }
        }
    }

    private void onServiceSuccess(double result) {
        Snackbar.make(layout, String.valueOf(result), Snackbar.LENGTH_LONG).show();
    }

    private void onServiceError() {
        AlertDialogFragment alert = AlertDialogFragment.newInstance(getResources().getString(R.string.error_detail));
        alert.show(getSupportFragmentManager(), DIALOG_TAG);
    }

    private void onServiceDivideByZero() {
        AlertDialogFragment alert = AlertDialogFragment.newInstance(getResources().getString(R.string.divide_by_zero));
        alert.show(getSupportFragmentManager(), DIALOG_TAG);
    }


    //region OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion
}

