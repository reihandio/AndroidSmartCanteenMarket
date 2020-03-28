package reihandio.dev.androidsmartkantintoko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import reihandio.dev.androidsmartkantintoko.Common.Common;
import reihandio.dev.androidsmartkantintoko.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView order_id, order_phone, order_adress, order_total, order_comment;
    String order_id_value="";
    RecyclerView listFoods;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id = (TextView)findViewById(R.id.order_id);
        order_phone = (TextView)findViewById(R.id.order_phone);
        order_adress = (TextView)findViewById(R.id.order_address);
        order_total = (TextView)findViewById(R.id.order_total);
        order_comment = (TextView)findViewById(R.id.order_comment);

        listFoods = (RecyclerView)findViewById(R.id.listFoods);
        listFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listFoods.setLayoutManager(layoutManager);

        //menetapkan nilai awal
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_adress.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getFood());
        adapter.notifyDataSetChanged();
        listFoods.setAdapter(adapter);

        if(getIntent()!=null)
            order_id_value = getIntent().getStringExtra("OrderId");

    }
}
