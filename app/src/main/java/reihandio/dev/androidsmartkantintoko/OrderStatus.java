package reihandio.dev.androidsmartkantintoko;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import reihandio.dev.androidsmartkantintoko.Common.Common;
import reihandio.dev.androidsmartkantintoko.Interface.ItemClickListener;
import reihandio.dev.androidsmartkantintoko.Model.MyResponse;
import reihandio.dev.androidsmartkantintoko.Model.Notification;
import reihandio.dev.androidsmartkantintoko.Model.Request;
import reihandio.dev.androidsmartkantintoko.Model.Sender;
import reihandio.dev.androidsmartkantintoko.Model.Token;
import reihandio.dev.androidsmartkantintoko.Remote.APIService;
import reihandio.dev.androidsmartkantintoko.ViewHolder.OrderViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference requests;

    MaterialSpinner spinner;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);



        //Firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        //Inisiasi Notifikasi
        mService = Common.getFCMClient();

        //Inisiasi
        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(); //load order list

    }

    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View vie, int position, boolean isLongClick) {
                        Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = model;
                        startActivity(orderDetail);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        else if(item.getTitle().equals(Common.DELETE))
                deleteOrder(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Pesanan");
        alertDialog.setMessage("Pilih Status Pesanan");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);

        spinner = (MaterialSpinner)view.findViewById(R.id.statusSpinner);
        spinner.setItems("Rincian Dikirim", "Diproses", "Dikirim");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);

                sendOrderStatusToUser(localKey,item);
            }
        });

        alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    private void sendOrderStatusToUser(final String key, final Request item) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapShot.getValue(Token.class);

                            //Membuat payload baru
                            Notification notification = new Notification("Smart-Kantin UMB", "Pesanan Anda "+key+" telah diupdate");
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if(response.body().success==1)
                                            {
                                                Toast.makeText(OrderStatus.this, "Status Pesanan Telah Diupdate!", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(OrderStatus.this, "Status Pesanan Telah Diupdate, namun Gagal Mengirim Notifikasi!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
