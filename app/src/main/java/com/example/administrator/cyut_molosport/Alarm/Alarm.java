package com.example.administrator.cyut_molosport.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/14.
 */
public class Alarm implements Serializable {

    public enum Difficulty{
        EASY,
        MEDIUM,
        HARD;

        @Override
        public String toString() {
            switch(this.ordinal()){
                case 0:
                    return "Easy";
                case 1:
                    return "Medium";
                case 2:
                    return "Hard";
            }
            return super.toString();
        }
    }

    public enum Day{
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY;

        @Override
        public String toString() {
            switch(this.ordinal()){
                case 0:
                    return "星期日";
                case 1:
                    return "星期一";
                case 2:
                    return "星期二";
                case 3:
                    return "星期三";
                case 4:
                    return "星期四";
                case 5:
                    return "星期五";
                case 6:
                    return "星期六";
            }
            return super.toString();
        }

    }
    private static final long serialVersionUID = 8699489847426803789L;
    private int id;
    private Boolean alarmActive = true;
    private Calendar alarmTime = Calendar.getInstance();
    private Day[] days = {Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY, Day.SATURDAY, Day.SUNDAY};
    private String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
    private Boolean vibrate = true;
    private String alarmName = "Alarm Clock";
    private Difficulty difficulty = Difficulty.EASY;

    public Alarm() {

    }

//	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
//		out.defaultWriteObject();
//		out.writeObject(getAlarmToneUri().getEncodedPath());
//	}

//	private void readObject(java.io.ObjectInputStream in) throws IOException {
//		try {
//			in.defaultReadObject();
//			this.setAlarmToneUri(Uri.parse(in.readObject().toString()));
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

    /**
     * @return the alarmActive
     */
    public Boolean getAlarmActive() {
        return alarmActive;
    }

    /**
     * @param alarmActive
     *            the alarmActive to set
     */
    public void setAlarmActive(Boolean alarmActive) {
        this.alarmActive = alarmActive;
    }

    /**
     * @return the alarmTime
     */
    public Calendar getAlarmTime() {
        if (alarmTime.before(Calendar.getInstance()))
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        while(!Arrays.asList(getDays()).contains(Day.values()[alarmTime.get(Calendar.DAY_OF_WEEK)-1])){
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        }
        return alarmTime;
    }

    /**
     * @return the alarmTime
     */
    public String getAlarmTimeString() {

        String time = "";
        if (alarmTime.get(Calendar.HOUR_OF_DAY) <= 9)
            time += "0";
        time += String.valueOf(alarmTime.get(Calendar.HOUR_OF_DAY));
        time += ":";

        if (alarmTime.get(Calendar.MINUTE) <= 9)
            time += "0";
        time += String.valueOf(alarmTime.get(Calendar.MINUTE));

        return time;
    }

    /**
     * @param alarmTime
     *            the alarmTime to set
     */
    public void setAlarmTime(Calendar alarmTime) {
        this.alarmTime = alarmTime;
    }

    /**
     * @param alarmTime
     *            the alarmTime to set
     */
    public void setAlarmTime(String alarmTime) {

        String[] timePieces = alarmTime.split(":");

        Calendar newAlarmTime = Calendar.getInstance();
        newAlarmTime.set(Calendar.HOUR_OF_DAY,
                Integer.parseInt(timePieces[0]));
        newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(timePieces[1]));
        newAlarmTime.set(Calendar.SECOND, 0);
        setAlarmTime(newAlarmTime);
    }

    /**
     * @return the repeatDays
     */
    public Day[] getDays() {
        return days;
    }

    /**
     * @param set
     *            the repeatDays to set
     */
    public void setDays(Day[] days) {
        this.days = days;
    }

    public void addDay(Day day){
        boolean contains = false;
        for(Day d : getDays())
            if(d.equals(day))
                contains = true;
        if(!contains){
            List<Day> result = new LinkedList<Day>();
            for(Day d : getDays())
                result.add(d);
            result.add(day);
            setDays(result.toArray(new Day[result.size()]));
        }
    }

    public void removeDay(Day day) {

        List<Day> result = new LinkedList<Day>();
        for(Day d : getDays())
            if(!d.equals(day))
                result.add(d);
        setDays(result.toArray(new Day[result.size()]));
    }

    /**
     * @return the alarmTonePath
     */
    public String getAlarmTonePath() {
        return alarmTonePath;
    }

    /**
     * @param alarmTonePath the alarmTonePath to set
     */
    public void setAlarmTonePath(String alarmTonePath) {
        this.alarmTonePath = alarmTonePath;
    }

    /**
     * @return the vibrate
     */
    public Boolean getVibrate() {
        return vibrate;
    }

    /**
     * @param vibrate
     *            the vibrate to set
     */
    public void setVibrate(Boolean vibrate) {
        this.vibrate = vibrate;
    }

    /**
     * @return the alarmName
     */
    public String getAlarmName() {
        return alarmName;
    }

    /**
     * @param alarmName
     *            the alarmName to set
     */
    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRepeatDaysString() {
        StringBuilder daysStringBuilder = new StringBuilder();
        if(getDays().length == Day.values().length){
            daysStringBuilder.append("每天");
        }else{
            Arrays.sort(getDays(), new Comparator<Day>() {
                @Override
                public int compare(Day lhs, Day rhs) {

                    return lhs.ordinal() - rhs.ordinal();
                }
            });
            for(Day d : getDays()){
                switch(d){
                    case TUESDAY:
                    case THURSDAY:
//					daysStringBuilder.append(d.toString().substring(0, 4));
//					break;
                    default:
                        daysStringBuilder.append(d.toString().substring(0, 3));
                        break;
                }
                daysStringBuilder.append(',');
            }
            daysStringBuilder.setLength(daysStringBuilder.length()-1);
        }

        return daysStringBuilder.toString();
    }

    public void schedule(Context context) {
        setAlarmActive(true);

        //Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        Log.d("Alarm", String.valueOf(myIntent));
        myIntent.putExtra("alarm", this);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmTime().getTimeInMillis(), pendingIntent);
    }

    public String getTimeUntilNextAlarmMessage(){
        long timeDifference = getAlarmTime().getTimeInMillis() - System.currentTimeMillis();
        long days = timeDifference / (1000 * 60 * 60 * 24);
        long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
        long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
        long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
        String alert = "提醒將會在";
        if (days > 0) {
            alert += String.format(
                    "%d 天, %d 小時, %d 分 %d 秒", days,
                    hours, minutes, seconds);
        } else {
            if (hours > 0) {
                alert += String.format("%d 小時, %d 分 %d 秒",
                        hours, minutes, seconds);
            } else {
                if (minutes > 0) {
                    alert += String.format("%d 分, %d 秒", minutes,
                            seconds);
                } else {
                    alert += String.format("%d 秒", seconds);
                }
            }
        }
        return alert;
    }
}

