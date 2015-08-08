package com.mikhalenko.memo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "extra_date";

    private static final String EXTRA_YEAR = "extra_year";
    private static final String EXTRA_MONTH = "extra_month";
    private static final String EXTRA_DAY = "extra_day";
    private static final String EXTRA_HOUR = "extra_hour";
    private static final String EXTRA_MIN = "extra_min";

    private long mDate;

    private int mYear, mMonth, mDay, mHour, mMin;

    public static DatePickerFragment newInstance(long date) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_DATE, date);

        DatePickerFragment f = new DatePickerFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        long date = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMin).getTimeInMillis();
        outState.putLong(EXTRA_DATE, date);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mDate = savedInstanceState.getLong(EXTRA_DATE);
        else
            mDate = getArguments().getLong(EXTRA_DATE);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mDate);

        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMin = cal.get(Calendar.MINUTE);

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_date_dialog, null);

        TimePicker timePicker = (TimePicker) v.findViewById(R.id.dialog_date_timePicker);

        timePicker.setSaveFromParentEnabled(false);
        timePicker.setSaveEnabled(true);

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(mHour);
        timePicker.setCurrentMinute(mMin);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMin = minute;
            }
        });

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
        datePicker.setSaveFromParentEnabled(false);
        datePicker.setSaveEnabled(true);
        datePicker.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
            }
        });

        DialogInterface.OnClickListener onOkClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(AppCompatActivity.RESULT_OK);
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.date_time_dialog_title);
        builder.setPositiveButton(android.R.string.ok, onOkClickListener);
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private void sendResult(int resCode) {
        if (getTargetFragment() == null)
            return;

        mDate = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMin).getTimeInMillis();
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resCode, i);
    }
}
