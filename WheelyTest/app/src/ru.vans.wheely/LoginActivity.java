package ru.vans.wheely;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by vans on 01.09.14.
 */
public class LoginActivity extends Activity {

    private Button mLoginButton;
    private EditText mLoginText;
    private EditText mPasswordText;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginText = (EditText) findViewById(R.id.login_field);
        mPasswordText = (EditText) findViewById(R.id.password_field);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = mLoginText.getText().toString();
                String password= mPasswordText.getText().toString();

                Intent intent = new Intent(LoginActivity.this, ru.vans.wheely.WheelyService.class);
                intent.putExtra(ru.vans.wheely.Utils.LOGIN_FIELD, login);
                intent.putExtra(ru.vans.wheely.Utils.PASSWORD_FIELD, password);
                intent.setAction("ACTION_CONNECT");
                startService(intent);

                Intent intent2 = new Intent(LoginActivity.this, ru.vans.wheely.MapActivity.class);
                startActivity(intent2);
                finish();
            }
        });
    }
}
