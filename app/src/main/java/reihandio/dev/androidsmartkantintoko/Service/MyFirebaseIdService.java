package reihandio.dev.androidsmartkantintoko.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import reihandio.dev.androidsmartkantintoko.Common.Common;
import reihandio.dev.androidsmartkantintoko.Model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        updateToServer(refreshedToken);
    }

    private void updateToServer(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(refreshedToken,true);//true karena ada akses admin
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }
}
