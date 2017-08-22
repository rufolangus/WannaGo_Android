package com.wannago.wannago;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wannago.wannago.models.LoginModel;
import com.wannago.wannago.models.RegisterModel;
import com.wannago.wannago.services.Account.Account;
import com.wannago.wannago.services.Account.LoginCallback;
import com.wannago.wannago.services.Account.RegisterCallback;
import com.wannago.wannago.services.Services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity
{
    private Account account;
    private TextView date_text;
    Boolean registering = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        account = Services.Account;
        SetActionbar();
        SetListeners();
    }

    void SetActionbar(){
        ActionBar bar = getActionBar();
        if(bar == null) {
            SetSupportActionBar();
            return;
        }

        bar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Register" + "</font>"));
         int color = Color.parseColor("#4CAF50");
        ColorDrawable colorDrawable = new ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
    }

    void SetSupportActionBar()
    {
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Register" + "</font>"));
        int color = Color.parseColor("#4CAF50");
        ColorDrawable colorDrawable = new ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
    }


    private void Login(final RegisterModel model)
    {
        LoginModel loginModel = new LoginModel(model.username,model.password);
        Services.Account.Login(loginModel, new LoginCallback() {
            @Override
            public void OnSuccess(int code, String rawResponse)
            {
                Toast("Logged in succesfully!");
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getApplicationContext().startActivity(intent);
                Services.Account.imageUrl = model.image;
                Services.Account.username = model.username;
                Services.Account.firstName = model.firstName;
                Services.Account.lastName = model.lastName;
                Services.Account.Gender = model.gender;
                Services.Account.birthDate = model.dateOfBirth;
                Services.Account.email = model.email;
                finish();
            }

            @Override
            public void OnError(int code, String error) {
                registering = false;
                Toast(code + " " + error);
            }
        });

    }
    private void SetListeners()
    {
        Button button = (Button)findViewById(R.id.register_button);
        date_text = (TextView)findViewById(R.id.register_birthday);
        date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventActivity.DatePickerFragment datePicker = new CreateEventActivity.DatePickerFragment();
                datePicker.setOnTimeSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date_text.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                });
                datePicker.show(getSupportFragmentManager(),"DatePicker");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final RegisterModel registerModel = Verify();
                if(registerModel != null && !registering){
                    registering = true;
                    Services.Account.Register(registerModel, new RegisterCallback() {
                        @Override
                        public void OnSuccess(int code, String response)
                        {
                            Toast("Registered succesfully! Loggin in...");
                            Login(registerModel);
                        }

                        @Override
                        public void OnError(int code, String error) {
                            //Report Error.
                            Toast(code + " " + error);
                            registering = false;
                        }
                    });}
            }
        });
        //Listen for register button press
        //On button press:
        //Verify correct input,
        //if correct, create new Registration Model,
        //call account.Register(registrationModel, new RegisterCallback(){});
    }

    RegisterModel Verify()
    {

        TextView usernameView = (TextView) findViewById(R.id.register_username);
        TextView firstNameView = (TextView) findViewById(R.id.register_name);
        TextView lastNameView = (TextView) findViewById(R.id.register_lastName);
        TextView emailView = (TextView) findViewById(R.id.register_email);
        TextView confirmEmailView = (TextView) findViewById(R.id.register_email_confirm);
        TextView passwordView = (TextView) findViewById(R.id.register_password);
        TextView confirmPasswordView = (TextView) findViewById(R.id.register_password_confirm);
        TextView birthdayView = (TextView) findViewById(R.id.register_birthday);
        RadioGroup genderView = (RadioGroup) findViewById(R.id.register_gender);
        CheckBox checkBoxView = (CheckBox) findViewById(R.id.register_checkbox_terms);

        String userName = usernameView.getText().toString();
        String firstName = firstNameView.getText().toString();
        String lastName = lastNameView.getText().toString();
        String email = emailView.getText().toString();
        String confirmEmail = confirmEmailView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();
        String birthDay = birthdayView.getText().toString();
        Integer gender = genderView.getCheckedRadioButtonId();
        Boolean termsOfService = checkBoxView.isChecked();
        if (userName.equals("")) {
            Toast("Please enter a valid username");
            return null;
        }

        if (firstName.equals("")) {
            Toast("Please enter a valid name");
            return null;
        }

        if (lastName.equals("")) {
            Toast("Please enter a valid last name");
            return null;
        }


        if (email.equals("")) {
            Toast("Please enter a valid e-mail");
            return null;
        }

        Pattern emailReg = Pattern.compile("[a-z0-9]+[_a-z0-9\\.-]*[a-z0-9]+@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})");
        Matcher emailMatch = emailReg.matcher(email);
        if(!emailMatch.matches()){
            Toast("Please enter a valid e-mail.");
            return null;
        }
        if (confirmEmail.equals("")) {
            Toast("Please enter a valid confirm e-mail");
            return null;
        }

        if (!email.equals(confirmEmail))
        {
            Toast("Emails do not match.");
        }

        if (password.equals("")) {
            Toast("Please enter a valid password");
            return null;
        }
        Pattern p = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");
        Matcher m = p.matcher(password);
        if(!m.matches()){
            Toast("Password must contain atleast 8 characters. Atleast one lowercase, a symbol and a number.");
            return null;
        }
        if (confirmPassword.equals("")) {
            Toast("Please enter a valid confirm password");
            return null;
        }

        if (!password.equals(confirmPassword))
        {
            Toast("Passwords do not match.");
        }

        if(birthDay.equals(""))
        {
            Toast("Please enter a valid birthday. dd/MM/yy or dd-MMM-yyyy");
            return null;
        }
        SimpleDateFormat sdfmt1 = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat sdfmt2= new SimpleDateFormat("dd-MMM-yyyy");
        Date date;
        try {
            date = sdfmt1.parse(birthDay);
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                date = sdfmt2.parse(birthDay);
            } catch (ParseException e1) {
                Toast("Invalid date format: dd-MMM-yyyy or dd/MM/yy");
                return null;
            }
        }

        if(gender == -1)
        {
            Toast("Please select a gender.");
            return null;
        }

        if(termsOfService == false)
        {
            Toast("Please read and agree to the terms of service.");
            return null;
        }

        RegisterModel model = new RegisterModel(userName,firstName,lastName,email,password,"",sdfmt1.format(date),gender);
        return model;
    }

    void Toast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener listener;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);


            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), R.style.TimePicker, listener,
                    year, month, day);
        }

        public void setOnTimeSetListener(DatePickerDialog.OnDateSetListener listener){
            this.listener = listener;
        }


    }
}
