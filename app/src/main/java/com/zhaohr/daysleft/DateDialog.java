/**
 * custom DatePickerDialog
 * */

package com.zhaohr.daysleft;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class DateDialog extends Dialog {

    private DatePicker datePic;
    private EditText titleText;
    private Button positiveButton, negativeButton;
    private String mTitle;

    public DateDialog(Context context) {
        super(context, R.style.dialog);

        View mView = LayoutInflater.from(getContext()).inflate(R.layout.date_dialog, null);
        super.setContentView(mView);

        datePic = (DatePicker)findViewById(R.id.datePicker);
        titleText = (EditText)findViewById(R.id.dateDialogTitle);
        positiveButton = (Button)findViewById(R.id.positiveButton);
        negativeButton = (Button)findViewById(R.id.negativeButton);
    }

    public void setTitle(String strTitle) {
        mTitle = strTitle;
        titleText.setText(mTitle);
    }

    public void setDate(int year, int month, int day) {
        datePic.init(year, month - 1, day, null);
    }

    public String getTitle() {
        mTitle = titleText.getText().toString();
        return mTitle;
    }

    public int getYear() {
        return datePic.getYear();
    }

    public int getMonth() {
        return datePic.getMonth() + 1;
    }

    public int getDay() {
        return datePic.getDayOfMonth();
    }

    public void setOnPositiveListener(View.OnClickListener listener){
        positiveButton.setOnClickListener(listener);
    }

    public void setOnNegativeListener(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener);
    }
}
