package roiattia.com.mynotes.database;

import android.arch.persistence.room.TypeConverter;

import org.joda.time.LocalDate;

class DateTypeConverter {

    @TypeConverter
    public static LocalDate toLocalDate(Long timeStamp){
        return timeStamp == null ? null : new LocalDate(timeStamp);
    }

    @TypeConverter
    public static Long toTimeStamp(LocalDate localDate){
        return localDate == null ? null : localDate.toDate().getTime();
    }
}
