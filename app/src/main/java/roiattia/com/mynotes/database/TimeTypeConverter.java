package roiattia.com.mynotes.database;

import android.arch.persistence.room.TypeConverter;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class TimeTypeConverter {

    @TypeConverter
    public static LocalTime toLocalDate(Long timeStamp){
        return timeStamp == null ? null : new LocalTime(timeStamp);
    }

    @TypeConverter
    public static Long toTimeStamp(LocalTime localTime){
        return localTime == null ? null : localTime.toDateTimeToday().getMillis();
    }
}
