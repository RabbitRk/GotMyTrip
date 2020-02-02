package com.rabbitt.gotmytrip.CityPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rabbitt.gotmytrip.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class cityBottomsheet extends BottomSheetDialogFragment {

    private static String pick_up_loc= null;
    private static String drop_loc= null;
    private static String v_type= null;
    private static String type= null;
    private static String ori_lat= null;
    private static String ori_lng = null;
    private static String dest_lat = null;
    private static String dest_lng = null;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.city_bottom_sheet, container, false);

        Button rideNow = v.findViewById(R.id.ride_now);
        ImageView vehicle_icon = v.findViewById(R.id.iconcard);
        TextView price_txt = v.findViewById(R.id.price);

        assert getArguments() != null;
        pick_up_loc = getArguments().getString("pickn");
        v_type = getArguments().getString("vehicle");
        type = getArguments().getString("travel_type");
        drop_loc = getArguments().getString("dropn");
        String price = getArguments().getString("base_fare");
        ori_lat = getArguments().getString("ori_lat");
        ori_lng = getArguments().getString("ori_lng");
        dest_lat = getArguments().getString("dest_lat");
        dest_lng = getArguments().getString("dest_lng");
        price_txt.setText(price);

        switch (v_type){
            case "Auto":
                vehicle_icon.setImageResource(R.drawable.ic_auto_rickshaw);
                break;
            case "Prime":
                vehicle_icon.setImageResource(R.drawable.ic_taxi);
                break;
            case "SUV":
                vehicle_icon.setImageResource(R.drawable.ic_booking_car_model);
                break;
        }

        rideNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pick_up_loc.equals("")&&!drop_loc.equals("")) {
                    //to collapse bottomsheet
                    dismiss();
                    Intent tocity = new Intent(getContext(), CityActivity.class);
                    tocity.putExtra("pick_up", pick_up_loc);
                    tocity.putExtra("drop", drop_loc);
                    tocity.putExtra("v_type", v_type);
                    tocity.putExtra("travel_type", type);
                    tocity.putExtra("ori_lat", ori_lat);
                    tocity.putExtra("ori_lng", ori_lng);
                    tocity.putExtra("dest_lat", dest_lat);
                    tocity.putExtra("dest_lng", dest_lng);
                    tocity.putExtra("date", currentDate());
                    tocity.putExtra("time", currentTime());
                    startActivity(tocity);
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("No Location Found");
                    alertDialog.setMessage("Please Select Valid Location");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            }
        });

        return v;
    }

    public static String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // get current date time with Date()
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        // don't print it, but save it!
        return dateFormat.format(date);
    }

    public static String currentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        // get current date time with Date()
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        // don't print it, but save it!
        return dateFormat.format(date);
    }


}
