package roiattia.com.mynotes.utils;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;

public class TextFormat {

    private TextFormat(){}

    public static String getDateTimeStringFormat(LocalDateTime dateTime){
        if(dateTime != null) {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy - HH:mm");
            return dtf.print(dateTime);
        }
        return "";
    }

    public static String getDateStringFormat(LocalDate date){
        if(date != null) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
            return fmt.print(date);
        }
        return "";
    }

    public static String getTimeStringFormat(LocalTime time){
        if(time != null) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm");
            return fmt.print(time);
        }
        return "";
    }
}
