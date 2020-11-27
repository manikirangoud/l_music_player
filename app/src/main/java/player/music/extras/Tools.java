package player.music.extras;



public  class Tools {

//GETTING TIME FUNCTION
    public static String getSongTime(long time) {
        String seconds = String.valueOf((time % 60000) / 1000);
        String minutes = String.valueOf(time /60000);
        if(seconds.length() == 1) {
            seconds = "0" + seconds;
        }
        return minutes + ":" + seconds;
    }

    public static String getSongMinutes(long time) {
        return String.valueOf(time /60000);
    }

    public static String getSongSeconds(long time) {
        return String.valueOf((time %60000) / 1000);
    }

    public static int getCompleteSeconds(long time) {
        String seconds = String.valueOf((time % 60000) / 1000);
        String minutes = String.valueOf(time /60000);
        int secs = Integer.parseInt(seconds);
        int mins = Integer.parseInt(minutes);
        int totalSeconds = (60*mins) + secs;
        return totalSeconds;
    }


    public static int getCompleteSeconds(int minutes, int seconds){
        return ((60*minutes) + seconds);
    }
}
