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

    public static String getStringFormatFromInt(int amount){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(amount);
    }
}
