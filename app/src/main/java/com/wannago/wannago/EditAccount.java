package com.wannago.wannago;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wannago.wannago.models.ImageUploadResponse;
import com.wannago.wannago.models.RegisterModel;
import com.wannago.wannago.services.Account.Account;
import com.wannago.wannago.services.Account.LoginCallback;
import com.wannago.wannago.services.Services;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditAccount extends AppCompatActivity {

    EditText usernameEdit;
    EditText firstNameEdit;
    EditText lastNameEdit;
    EditText emailEdit;
    EditText confirmEmailEdit;
    TextView DateOfBirthEdit;
    RadioGroup gender;
    RadioButton male;
    RadioButton female;
    RadioButton none;
    Account account;
    File file;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        SetActionbar();
        usernameEdit = (EditText)findViewById(R.id.edit_username);
        firstNameEdit = (EditText)findViewById(R.id.edit_name);
        lastNameEdit = (EditText)findViewById(R.id.edit_lastName);
        emailEdit = (EditText)findViewById(R.id.edit_email);
        confirmEmailEdit = (EditText)findViewById(R.id.edit_email_confirm);
        DateOfBirthEdit = (TextView)findViewById(R.id.edit_birthday);
        gender = (RadioGroup)findViewById(R.id.edit_gender);
        male = (RadioButton)findViewById(R.id.edit_male);
        female = (RadioButton)findViewById(R.id.edit_female);
        none = (RadioButton)findViewById(R.id.edit_nondisclosed);
        profileImage = (ImageView)findViewById(R.id.edit_profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,5);

            }
        });
        DateOfBirthEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateDatePicker();
            }
        });
        account = Services.Account;
        SetFields();

        Button save = (Button)findViewById(R.id.edit_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterModel model = Verify();
                if(model != null)
                {
                    account.Edit(model, new LoginCallback() {
                        @Override
                        public void OnSuccess(int code, String rawResponse) {
                            SetFields();
                            Toast("Save successful");
                            if(file != null)
                                UploadImage();

                        }

                        @Override
                        public void OnError(int code, String error) {
                            Toast(code + " " + error);
                        }
                    });
                }
            }
        });
    }

    public void UploadImage()
    {
        Services.Account.UploadImage(file, new ImageUploadResponse(){

            @Override
            public void OnSuccess(String message)
            {
                Toast(message);
            }

            @Override
            public void OnError(String message)
            {
                Toast(message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 5 && resultCode == RESULT_OK)
        {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                file = new File(picturePath);
                profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }


    void SetActionbar(){
        ActionBar bar = getActionBar();
        if(bar == null) {
            SetSupportActionBar();
            return;
        }

        bar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Edit Account" + "</font>"));
        int color = Color.parseColor("#4CAF50");
        ColorDrawable colorDrawable = new ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
    }
    void SetSupportActionBar()
    {
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Edit Account" + "</font>"));
        int color = Color.parseColor("#4CAF50");
        ColorDrawable colorDrawable = new ColorDrawable(color);
        bar.setBackgroundDrawable(colorDrawable);
    }

    private void SetRadioButtons(int id)
    {
        male.setChecked(false);
        female.setChecked(false);
        none.setChecked(false);

        if(id == 0)
            male.setChecked(true);
        else if(id == 1)
            female.setChecked(true);
        else if(id == 2)
            none.setChecked(true);

    }
    private void SetFields(){
        usernameEdit.setText(account.username);
        firstNameEdit.setText(account.firstName);
        lastNameEdit.setText(account.lastName);
        emailEdit.setText(account.email);
        Picasso.with(getApplicationContext()).load(account.imageUrl == "" ? null : account.imageUrl)
                                             .placeholder(R.drawable.ic_menu_camera)
                                             .error(R.drawable.ic_menu_camera)
                                             .into(profileImage);
        confirmEmailEdit.setText(account.email);
        String birthDate = account.birthDate;
        SetRadioButtons(account.Gender);

        //TODO: FIX ASP.NET DATE STUFF. FIGURE OUT FORMAT.
        DateOfBirthEdit.setText("11/28/1989");

    }
    RegisterModel Verify()
    {



        String userName = usernameEdit.getText().toString();
        String firstName = firstNameEdit.getText().toString();
        String lastName = lastNameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String confirmEmail = confirmEmailEdit.getText().toString();
        String birthDay = DateOfBirthEdit.getText().toString();
        Integer gender = this.gender.getCheckedRadioButtonId();
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

        if (confirmEmail.equals("")) {
            Toast("Please enter a valid confirm e-mail");
            return null;
        }

        if (!email.equals(confirmEmail))
        {
            Toast("Emails do not match.");
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

        RegisterModel model = new RegisterModel(userName,firstName,lastName,email,"","",sdfmt1.format(date),gender);
        return model;
    }

    public void CreateDatePicker(){
        CreateEventActivity.DatePickerFragment picker = new CreateEventActivity.DatePickerFragment();
        picker.setOnTimeSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateOfBirthEdit.setText(dayOfMonth + "/" + month + "/" + year);
            }
        });
        picker.show(getSupportFragmentManager(),"Date Picker");
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
    void Toast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
