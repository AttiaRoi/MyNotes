package roiattia.com.mynotes.database;

import android.arch.persistence.room.TypeConverter;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class DateTimeTypeConverter {

    @TypeConverter
    public static LocalDateTime toLocalDateTime(Long timeStamp){
        return timeStamp == null ? null : new LocalDateTime(timeStamp);
    }

    @TypeConverter
    public static Long toTimeStamp(LocalDateTime localDateTime){
        return localDateTime == null ? null : localDateTime.toDate().getTime();
    }
}
