package reihandio.dev.androidsmartkantintoko.Common;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

import reihandio.dev.androidsmartkantintoko.Model.Request;
import reihandio.dev.androidsmartkantintoko.Model.User;
import reihandio.dev.androidsmartkantintoko.Remote.APIService;
import reihandio.dev.androidsmartkantintoko.Remote.FCMRetrofitClient;
import retrofit2.Retrofit;
import reihandio.dev.androidsmartkantintoko.Remote.RetrofitClient;

public class Common {
    public static User currentUser;
    public static Request currentRequest;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static final String fcmUrl = "https://fcm.googleapis.com/";

    public static String convertCodeToStatus(String code)
    {
        if (code.equals("0"))
            return "Rincian Dikirim";
        else if (code.equals("1"))
            return "Diproses";
        else
            return "Dikirim";
    }

    public static APIService getFCMClient(){
        return FCMRetrofitClient.getClient(fcmUrl).create(APIService.class);
    }

    public static String getDate (long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm"
                ,calendar)
                .toString());
        return date.toString();
    }

}
