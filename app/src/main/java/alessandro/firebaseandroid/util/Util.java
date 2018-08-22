package alessandro.firebaseandroid.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alessandro Barreto on 23/06/2016.
 */
public class Util {

    public static final String URL_STORAGE_REFERENCE = "gs://build-test-6d1db.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "Development/message_pictures";

    public static void initToast(Context c, String message){
        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }

    public  static boolean verificaConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }

    public static String local(String latitudeFinal,String longitudeFinal){
        return "https://maps.googleapis.com/maps/api/staticmap?center="+latitudeFinal+","+longitudeFinal+"&zoom=18&size=280x280&markers=color:red|"+latitudeFinal+","+longitudeFinal;
    }

    public static String getDisplayDateComment(long milliSeconds) {
        return new SimpleDateFormat("hh:mm a").format(new Date(milliSeconds));
    }

    public static String getTimeDifference(long milliSeconds) {
        String timeDifference = "";

        Date date = new Date(milliSeconds);

        Date nowDate = new Date(System.currentTimeMillis());

        DateTime dt1 = new DateTime(date);
        DateTime dt2 = new DateTime(nowDate);


        int daysCount = Days.daysBetween(dt1, dt2).getDays();

        if (daysCount > 0) {
            if (daysCount == 1)
                timeDifference = "YESTERDAY";
            else
                timeDifference = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date(milliSeconds));

            return timeDifference;
        }

        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);

    }

}
