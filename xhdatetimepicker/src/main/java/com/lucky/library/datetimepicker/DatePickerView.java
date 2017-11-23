package com.lucky.library.datetimepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author xhao
 * @date 2017/10/18
 */

public class DatePickerView extends FrameLayout implements NumberPickerView.OnValueChangeListener{

    private static final int DEFAULT_COLOR = 0xff3388ff;
    private static final int DEFAULT_NORMAL_TEXT_COLOR = 0xFF555555;

    private static final int YEAR_START = 1901;
    private static final int YEAR_STOP = 2100;
    private static final int YEAR_SPAN = YEAR_STOP - YEAR_START + 1;

    private static final int MONTH_START = 1;
    private static final int MONTH_STOP = 12;
    private static final int MONTH_SPAN = MONTH_STOP - MONTH_START + 1;


    private static final int DAY_START = 1;
    private static final int DAY_STOP = 31;
    private static final int DAY_SPAN = DAY_STOP - DAY_START + 1;

    private NumberPickerView mYearPickerView;
    private NumberPickerView mMonthPickerView;
    private NumberPickerView mDayPickerView;

    private int mThemeColorG = DEFAULT_COLOR;
    private int mNormalTextColor = DEFAULT_NORMAL_TEXT_COLOR;

    /**
     * display values
     */
    private String[] mDisplayYears;
    private String[] mDisplayMonths;
    private String[] mDisplayDays;

    /**
     * true to use scroll anim when switch picker passively
     */
    private boolean mScrollAnim = true;

    private OnDateChangedListener mOnDateChangedListener;

    public DatePickerView(Context context) {
        super(context);
        initInternal(context);
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initInternal(context);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(context, attrs);
        initInternal(context);
    }

    private void initInternal(Context context){
        View contentView = inflate(context, R.layout.view_date_picker, this);

        mYearPickerView = (NumberPickerView) contentView.findViewById(R.id.year);
        mMonthPickerView = (NumberPickerView) contentView.findViewById(R.id.month);
        mDayPickerView = (NumberPickerView) contentView.findViewById(R.id.day);

        mYearPickerView.setOnValueChangedListener(this);
        mMonthPickerView.setOnValueChangedListener(this);
        mDayPickerView.setOnValueChangedListener(this);

        setConfigs();
    }

    private void initAttr(Context context, AttributeSet attrs){
        if (attrs == null) {
            return;
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DatePickerView);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if(attr == R.styleable.DatePickerView_dpv_ScrollAnimation){
                mScrollAnim = array.getBoolean(attr, true);
            }else if(attr == R.styleable.DatePickerView_dpv_ThemeColor){
                mThemeColorG = array.getColor(attr, DEFAULT_COLOR);
            }if(attr == R.styleable.DatePickerView_dpv_NormalTextColor){
                mNormalTextColor = array.getColor(attr, DEFAULT_NORMAL_TEXT_COLOR);
            }
        }
        array.recycle();
    }

    private void setConfigs(){
        Calendar calendar = Calendar.getInstance();
        if(!checkCalendarAvailable(calendar, YEAR_START, YEAR_STOP)){
            calendar = adjustCalendarByLimit(calendar, YEAR_START, YEAR_STOP);
        }
        setDisplayValues(calendar, false);
    }
    private boolean checkCalendarAvailable(Calendar calendar, int yearStart, int yearStop){
        int year = calendar.get(Calendar.YEAR);
        return (yearStart <= year) && (year <= yearStop);
    }
    private Calendar adjustCalendarByLimit(Calendar calendar, int yearStart, int yearStop){
        int year = calendar.get(Calendar.YEAR);
        if(year < yearStart){
            calendar.set(Calendar.YEAR, yearStart);
            calendar.set(Calendar.MONTH, MONTH_START);
            calendar.set(Calendar.DAY_OF_MONTH, DAY_START);
        }
        if(year > yearStop){
            calendar.set(Calendar.YEAR, yearStop);
            calendar.set(Calendar.MONTH, MONTH_STOP - 1);
            int daySway = getSumOfDayInMonth(yearStop, MONTH_STOP);
            calendar.set(Calendar.DAY_OF_MONTH, daySway);
        }
        return calendar;
    }

    private void setDisplayValues(Calendar calendar, boolean anim){
        setDisplayData();
        initValuesForYear(calendar, anim);
        initValuesForMouth(calendar, anim);
        initValuesForDay(calendar, anim);
    }

    private void setDisplayData(){

        if(mDisplayYears == null){
            mDisplayYears = new String[YEAR_SPAN];
            for(int i = 0; i < YEAR_SPAN; i++){
                mDisplayYears[i] = String.valueOf(YEAR_START + i);
            }
        }
        if(mDisplayMonths == null){
            mDisplayMonths = new String[MONTH_SPAN];
            for(int i = 0; i < MONTH_SPAN; i++){
                mDisplayMonths[i] = formatNumber(MONTH_START + i);
            }
        }
        if(mDisplayDays == null){
            mDisplayDays = new String[DAY_SPAN];
            for(int i = 0; i < DAY_SPAN; i++){
                mDisplayDays[i] = formatNumber(DAY_START + i);
            }
        }
    }
    public String formatNumber(int i){
        return i < 10 ? "0" + i : ""+ i;
    }

    //without scroll animation when init
    private void initValuesForYear(Calendar calendar, boolean anim){
        int yearSway = calendar.get(Calendar.YEAR);
        setValuesForPickerView(mYearPickerView, yearSway, YEAR_START, YEAR_STOP, mDisplayYears, false, anim);
    }

    private void initValuesForMouth(Calendar calendar, boolean anim){

        int monthStart = MONTH_START;
        int monthStop = MONTH_STOP;
        int monthSway = calendar.get(Calendar.MONTH) + 1;
        String[] newDisplayedVales = mDisplayMonths;
        setValuesForPickerView(mMonthPickerView, monthSway, monthStart, monthStop, newDisplayedVales, false, anim);
    }

    private void initValuesForDay(Calendar calendar, boolean anim){
        int dayStart = DAY_START;
        int dayStop = getSumOfDayInMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        int daySway = calendar.get(Calendar.DAY_OF_MONTH);
        setValuesForPickerView(mDayPickerView, daySway, dayStart, dayStop, mDisplayDays, false, anim);
    }

    private void setValuesForPickerView(NumberPickerView pickerView, int newSway, int newStart, int newStop, String[] newDisplayedVales, boolean needRespond, boolean anim){
        if(newDisplayedVales == null){
            throw new IllegalArgumentException("newDisplayedVales should not be null.");
        }else if(newDisplayedVales.length == 0){
            throw new IllegalArgumentException("newDisplayedVales's length should not be 0.");
        }
        int newSpan = newStop - newStart + 1;
        if(newDisplayedVales.length < newSpan){
            throw new IllegalArgumentException("newDisplayedVales's length should not be less than newSpan.");
        }

        int oldStart = pickerView.getMinValue();
        int oldStop = pickerView.getMaxValue();
        int oldSpan = oldStop - oldStart + 1;
        int fromValue = pickerView.getValue();

        pickerView.setMinValue(newStart);
        if(newSpan > oldSpan){
            pickerView.setDisplayedValues(newDisplayedVales);
            pickerView.setMaxValue(newStop);
        }else{
            pickerView.setMaxValue(newStop);
            pickerView.setDisplayedValues(newDisplayedVales);
        }
        if(mScrollAnim && anim){
            int toValue = newSway;
            if(fromValue < newStart){
                fromValue = newStart;
            }
            pickerView.smoothScrollToValue(fromValue, toValue, needRespond);
        }else{
            pickerView.setValue(newSway);
        }
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        if(picker == null) {
            return;
        }
        if(picker == mYearPickerView){
            passiveUpdateMonthAndDay(oldVal, newVal);
        }else if(picker == mMonthPickerView){
            int fixYear = mYearPickerView.getValue();
            passiveUpdateDay(fixYear, fixYear, oldVal, newVal);
        }else if(picker == mDayPickerView){
            if(mOnDateChangedListener != null){
                mOnDateChangedListener.onDateChanged(getCalendarData());
            }
        }
    }

    private void passiveUpdateMonthAndDay(int oldYearFix, int newYearFix){
        int oldMonthSway = mMonthPickerView.getValue();
        int oldDaySway = mDayPickerView.getValue();

            int newMonthSway = oldMonthSway;
            int oldDayStop = getSumOfDayInMonth(oldYearFix, oldMonthSway);
            int newDayStop = getSumOfDayInMonth(newYearFix, newMonthSway);

            if(oldDayStop == newDayStop){
                if(mOnDateChangedListener != null){
                    mOnDateChangedListener.onDateChanged(getCalendarData(newYearFix, newMonthSway, oldDaySway));
                }
                return;
            }
            int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
            setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDays, true, true);
            if(mOnDateChangedListener != null){
                mOnDateChangedListener.onDateChanged(getCalendarData(newYearFix, newMonthSway, newDaySway));
            }
            return;
    }

    private void passiveUpdateDay(int oldYear, int newYear, int oldMonth, int newMonth){
        int oldDaySway = mDayPickerView.getValue();

        int oldDayStop = getSumOfDayInMonth(oldYear, oldMonth);
        int newDayStop = getSumOfDayInMonth(newYear, newMonth);

        if(oldDayStop == newDayStop){
            if(mOnDateChangedListener != null){
                mOnDateChangedListener.onDateChanged(getCalendarData(newYear, newMonth, oldDaySway));
            }
            return;//不需要更新
        }else{
            int newDaySway = oldDaySway <= newDayStop ? oldDaySway : newDayStop;
            setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDays, true, true);
            if(mOnDateChangedListener != null){
                mOnDateChangedListener.onDateChanged(getCalendarData(newYear, newMonth, newDaySway));
            }
            return;
        }
    }

    public View getNumberPickerYear(){
        return mYearPickerView;
    }

    public View getNumberPickerMonth(){
        return mMonthPickerView;
    }

    public View getNumberPickerDay(){
        return mDayPickerView;
    }

    public void setNumberPickerYearVisibility(int visibility){
        setNumberPickerVisibility(mYearPickerView, visibility);
    }

    public void setNumberPickerMonthVisibility(int visibility){
        setNumberPickerVisibility(mMonthPickerView, visibility);
    }

    public void setNumberPickerDayVisibility(int visibility){
        setNumberPickerVisibility(mDayPickerView, visibility);
    }

    public void setNumberPickerVisibility(NumberPickerView view, int visibility){
        if(view.getVisibility() == visibility){
            return;
        }else if(visibility == View.GONE || visibility == View.VISIBLE || visibility == View.INVISIBLE){
            view.setVisibility(visibility);
        }
    }

    private CalendarData getCalendarData(int pickedYear, int pickedMonthSway, int pickedDay){
        return new CalendarData(pickedYear, pickedMonthSway, pickedDay);
    }

    public CalendarData getCalendarData(){
        int pickedYear = mYearPickerView.getValue();
        int pickedMonthSway = mMonthPickerView.getValue();
        int pickedDay = mDayPickerView.getValue();
        return new CalendarData(pickedYear, pickedMonthSway, pickedDay);
    }

    public static class CalendarData{
        public int pickedYear;
        public int pickedMonthSway;
        public int pickedDay;

        public Calendar calendar;

        /**
         * model类的构造方法
         * @param pickedYear
         * 			年
         * @param pickedMonthSway
         * 			月，公历农历均从1开始。农历如果有闰年，按照实际的顺序添加
         * @param pickedDay
         * 			日，从1开始，日期在月份中的显示数值
         */
        public CalendarData(int pickedYear, int pickedMonthSway, int pickedDay) {
            this.pickedYear = pickedYear;
            this.pickedMonthSway = pickedMonthSway;
            this.pickedDay = pickedDay;
            initCalendar();
        }

        /**
         * 初始化成员变量chineseCalendar，用来记录当前选中的时间。此成员变量同时存储了农历和公历的信息
         */
        private void initCalendar(){
            calendar = Calendar.getInstance();//公历日期构造方法
            calendar.set(pickedYear,pickedMonthSway - 1, pickedDay);
        }

        public Calendar getCalendar(){
            return calendar;
        }
    }

    public interface OnDateChangedListener{
        /**
         *
         * @param calendarData
         */
        void onDateChanged(CalendarData calendarData);
    }

    public void setOnDateChangedListener(OnDateChangedListener listener){
        mOnDateChangedListener = listener;
    }

    private int getSumOfDayInMonth(int year, int month){

        return new GregorianCalendar(year, month, 0).get(Calendar.DATE);
    }
}
