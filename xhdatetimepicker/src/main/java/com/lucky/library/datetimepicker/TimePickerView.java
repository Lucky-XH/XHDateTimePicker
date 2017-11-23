package com.lucky.library.datetimepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;

import com.lucky.library.datetimepicker.R;

import java.util.Calendar;
import java.util.Locale;


/**
 *
 * @author xhao
 * @date 2017/10/18
 * {@link android.widget.TimePicker}
 */

public class TimePickerView extends FrameLayout{
    private NumberPickerView mHourSpinner;
    private NumberPickerView mMinuteSpinner;
    protected OnTimeChangedListener mOnTimeChangedListener;

    private int mHour, mMinute;
    public TimePickerView(Context context) {
        this(context, null);
    }
    public TimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_time_picker, this);

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        // hour
        mHourSpinner = (NumberPickerView) findViewById(R.id.hour);
        mHourSpinner.setDisplayedValues(new String[]{
                "00","01","02","03","04","05","06","07","08","09",
                "10","11","12","13","14","15","16","17","18","19",
                "20","21","22","23","24"});
        mHourSpinner.setMinValue(0);
        mHourSpinner.setMaxValue(23);
        mHourSpinner.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        mHourSpinner.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView spinner, int oldVal, int newVal) {
                onTimeChanged();
            }
        });

        // minute
        mMinuteSpinner = (NumberPickerView) findViewById(R.id.minute);
        mMinuteSpinner.setDisplayedValues(new String[]{
                "00","01","02","03","04","05","06","07","08","09",
                "10","11","12","13","14","15","16","17","18","19",
                "20","21","22","23","24","25","26","27","28","29",
                "30","31","32","33","34","35","36","37","38","39",
                "40","41","42","43","44","45","46","47","48","49",
                "50","51","52","53","54","55","56","57","58","59"});
        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setMaxValue(59);
        mMinuteSpinner.setValue(calendar.get(Calendar.MINUTE));
        mMinuteSpinner.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView spinner, int oldVal, int newVal) {
                int minValue = mMinuteSpinner.getMinValue();
                int maxValue = mMinuteSpinner.getMaxValue();
                if (oldVal == maxValue && newVal == minValue) {
                    if (mHourSpinner.getValue() != mHourSpinner.getMaxValue()){
                        int newHour = mHourSpinner.getValue() + 1;
                        mHourSpinner.setValue(newHour);
                    }else {
                        mHourSpinner.setValue(mHourSpinner.getMinValue());
                    }
                } else if (oldVal == minValue && newVal == maxValue) {
                    if (mHourSpinner.getValue() != mHourSpinner.getMinValue()){
                        int newHour = mHourSpinner.getValue() - 1;
                        mHourSpinner.setValue(newHour);
                    }else {
                        mHourSpinner.setValue(mHourSpinner.getMaxValue());
                    }

                }
                onTimeChanged();
            }
        });
//        mMinuteSpinner.refreshByNewDisplayedValues(new String[]{} );
    }
    public void setHour(int hour) {
        setCurrentHour(hour, true);
    }

    private void setCurrentHour(int currentHour, boolean notifyTimeChanged) {
        // why was Integer used in the first place?
        if (currentHour == getHour()) {
            return;
        }
        mHourSpinner.setValue(currentHour);
        if (notifyTimeChanged) {
            onTimeChanged();
        }
    }
    public int getHour() {
        return mHourSpinner.getValue();
    }

    public void setMinute(int minute) {
        if (minute == getMinute()) {
            return;
        }
        mMinuteSpinner.setValue(minute);
        onTimeChanged();
    }


    public int getMinute() {
        return mMinuteSpinner.getValue();
    }

    public void setOnTimeChangedListener(TimePickerView.OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }
    private void onTimeChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (mOnTimeChangedListener != null) {
            mOnTimeChangedListener.onTimeChanged(this, getHour(),getMinute());
        }
    }

    public NumberPickerView getHourSpinner() {
        return mHourSpinner;
    }

    public void setHourSpinner(NumberPickerView mHourSpinner) {
        this.mHourSpinner = mHourSpinner;
    }

    public NumberPickerView getMinuteSpinner() {
        return mMinuteSpinner;
    }

    public void setMinuteSpinner(NumberPickerView mMinuteSpinner) {
        this.mMinuteSpinner = mMinuteSpinner;
    }

    /**
     * The callback interface used to indicate the time has been adjusted.
     */
    public interface OnTimeChangedListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The current hour.
         * @param minute The current minute.
         */
        void onTimeChanged(TimePickerView view, int hourOfDay, int minute);
    }
}
