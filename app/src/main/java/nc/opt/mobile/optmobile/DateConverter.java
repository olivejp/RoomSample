package nc.opt.mobile.optmobile;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 2761oli on 23/10/2017.
 */

public class DateConverter {
    private static final String TAG = DateConverter.class.getName();

    public enum DatePattern {
        PATTERN_DTO("dd/MM/yyyy HH:mm:ss"),
        PATTERN_DTO_WITHOUT_HOUR("dd/MM/yyyy"),
        PATTERN_ENTITY("yyyyMMddHHmmss"),
        PATTERN_UI("dd MMM yyyy à HH:mm"),
        PATTERN_AFTER_HIP("yyyy-MM-dd'T'HH:mm:ss");

        private final String value;

        private DatePattern(String value) {
            this.value = value;
        }

        public String getDatePattern() {
            return this.value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static final SimpleDateFormat simpleDtoDateFormat = new SimpleDateFormat(DatePattern.PATTERN_DTO.getDatePattern(), Locale.FRANCE);
    public static final SimpleDateFormat simpleDtoDateFormatWithoutHour = new SimpleDateFormat(DatePattern.PATTERN_DTO_WITHOUT_HOUR.getDatePattern(), Locale.FRANCE);
    public static final SimpleDateFormat simpleUiDateFormat = new SimpleDateFormat(DatePattern.PATTERN_UI.getDatePattern(), Locale.FRANCE);
    public static final SimpleDateFormat simpleEntityDateFormat = new SimpleDateFormat(DatePattern.PATTERN_ENTITY.getDatePattern(), Locale.FRANCE);

    private DateConverter() {
    }

    /**
     * Transformation d'une date de type yyyyMMddHHmmss vers le format dd MMM yy HH:mm
     *
     * @param dateEntity
     * @return
     */
    public static String convertDateEntityToUi(Long dateEntity) {
        if (dateEntity != null) {
            try {
                return simpleUiDateFormat.format(simpleEntityDateFormat.parse(String.valueOf(dateEntity)));
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * @param dateEntity
     * @return
     */
    public static String howLongFromNow(Long dateEntity) {
        if (dateEntity != null) {
            return howLongFromNow(DateConverter.convertDateEntityToDto(dateEntity));
        }
        return null;
    }

    /**
     * @param dateDto
     * @return
     */
    private static String howLongFromNow(String dateDto) {
        if (dateDto == null) {
            return null;
        }
        try {
            Date now = Calendar.getInstance().getTime();
            Date date = simpleDtoDateFormat.parse(dateDto);
            long duration = now.getTime() - date.getTime();

            long nbSecond = duration / 1000;
            long nbMinute = nbSecond / 60;
            long nbHour = nbMinute / 60;
            long nbDay = nbHour / 24;
            long nbWeek = nbDay / 7;
            long nbMonth = nbDay / 30;
            long nbYear = nbMonth / 12;

            // On est au dessus de l'année on affiche les années
            if (nbYear >= 1) {
                return String.valueOf(nbYear).concat(" années");
            }
            // On est en dessous de l'année on affiche les mois
            if (nbMonth >= 1) {
                return String.valueOf(nbMonth).concat(" mois");
            }
            // On est en dessous du mois on affiche les semaines
            if (nbWeek >= 1) {
                return String.valueOf(nbWeek).concat(" sem");
            }
            // On est en dessous de la semaine on affiche les jours
            if (nbDay >= 1) {
                return String.valueOf(nbDay).concat(" j");
            }
            // On est en dessous de la journée on affiche les heures
            if (nbHour >= 1) {
                return String.valueOf(nbHour).concat(" hr");
            }
            // On est en dessous de l'heure on affiche les minutes
            if (nbMinute >= 1) {
                return String.valueOf(nbMinute).concat(" min");
            }
            // On est inférieur à la minute, on affiche les secondes
            return String.valueOf(nbSecond).concat(" sec");
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Transformation d'une date de type dd/MM/yyyy HH:mm:ss vers le format yyyyMMddHHmmss en Long
     *
     * @param dateDto
     * @return Long
     */
    public static Long convertDateDtoToEntity(String dateDto) {
        try {
            String dateConverted = simpleEntityDateFormat.format(simpleDtoDateFormat.parse(dateDto));
            return Long.parseLong(dateConverted);
        } catch (ParseException e) {
            Log.e(TAG, "Date parse exception : Date is not in " + simpleDtoDateFormat.toPattern() + " format. We will try with " + simpleDtoDateFormatWithoutHour.toPattern() + " format.", e);

            try {
                String dateConverted = simpleEntityDateFormat.format(simpleDtoDateFormatWithoutHour.parse(dateDto));
                return Long.parseLong(dateConverted);
            } catch (ParseException e1) {
                Log.e(TAG, "Date parse exception : Date is not in " + simpleDtoDateFormatWithoutHour.toPattern() + " format. Unknown date format for this string " + dateDto, e);
            }
        } catch (NullPointerException e1) {
            Log.e(TAG, e1.getMessage(), e1);
        }
        return 0L;
    }

    /**
     * Transformation d'une date de type yyyyMMddHHmmss vers le format dd/MM/yyyy HH:mm:ss en Long
     *
     * @param dateEntity
     * @return String
     */
    public static String convertDateEntityToDto(Long dateEntity) {
        try {
            Date dateConverted = simpleEntityDateFormat.parse(String.valueOf(dateEntity));
            return simpleDtoDateFormat.format(dateConverted);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Va renvoyer la date du jour au format yyyyMMddHHmmss en Long
     *
     * @return Long
     */
    public static Long getNowEntity() {
        Calendar cal = Calendar.getInstance();
        return Long.parseLong(simpleEntityDateFormat.format(cal.getTime()));
    }
}
