package de.kablion.qsnake.utils;

public class UtilCalc {

    /**
     *
     * @param timeInSeconds
     * @param format d=days, h=hours, M=minutes, s=seconds, m=milliseconds Example: "dMs"
     * @param forceShow force every time-interval even if 0 (only those listed in format)
     * @return
     */
    public static String secondsToString(float timeInSeconds, String format, boolean forceShow) {

        int days, hours, minutes, seconds, milliseconds;

        final int secondsPerDay = 86400;
        final int secondsPerHour = 3600;
        final int secondsPerMinute = 60;

        days = (int)timeInSeconds/secondsPerDay;
        timeInSeconds -= days*secondsPerDay;

        hours = (int)timeInSeconds/secondsPerHour;
        timeInSeconds -= hours*secondsPerHour;

        minutes = (int)timeInSeconds/secondsPerMinute;
        timeInSeconds -= minutes*secondsPerMinute;

        seconds = (int)timeInSeconds;
        timeInSeconds -= seconds;

        milliseconds = (int)(timeInSeconds*1000);

        String result = "";

        for (int i = 0; i < format.length(); i++){
            char interval = format.charAt(i);
            switch (interval) {
                case 'd':
                    if(days > 0 || forceShow) {
                        result += days + " Days ";
                    }
                    break;
                case 'h':
                    if(hours > 0 || forceShow) {
                        result += hours + " Hours ";
                    }
                    break;
                case 'M':
                    if(minutes > 0 || forceShow) {
                        result += minutes + " Minutes ";
                    }
                    break;
                case 's':
                    if(seconds > 0 || forceShow) {
                        result += seconds + " Seconds ";
                    }
                    break;
                case 'm': {
                    if(milliseconds > 0 || forceShow) {
                        result += milliseconds + "Milliseconds ";
                    }
                    break;
                }

            }
        }

        return result;
    }

}
