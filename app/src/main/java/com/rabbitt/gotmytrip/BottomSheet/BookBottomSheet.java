package com.rabbitt.gotmytrip.BottomSheet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rabbitt.gotmytrip.R;
import com.rabbitt.gotmytrip.RentalPackage.RentalActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BookBottomSheet extends BottomSheetDialogFragment {

    static String price= null;
    Button ridenow, ridelater;
    Boolean check_time = false, check_date = false;
    ImageView vehicle_icon;
    String rideLater_date, rideLater_time, pick_up_loc, v_type = "2", type, drop_loc;
    TextView price_txt;
    private BottomSheetListener mListener;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_container, container, false);
        ridenow = v.findViewById(R.id.ride_now2);
        ridelater = v.findViewById(R.id.button3);
        vehicle_icon=v.findViewById(R.id.iconcard);
        price_txt=v.findViewById(R.id.price);

        assert getArguments() != null;
        pick_up_loc = getArguments().getString("pickn");
        v_type = getArguments().getString("vehicle");
        price=getArguments().getString("base_fare");
        type = getArguments().getString("travel_type");
        drop_loc = getArguments().getString("dropn");
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


        assert v_type != null;
        switch (v_type) {
            case "Prime":
                v_type = "2";
                break;
            case "SUV":
                v_type = "3";
                break;
            default:
                v_type = "2";
        }


        ridenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pick_up_loc.equals("")) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
                    String rideNow_date = df.format(c.getTime());
                    String rideNow_time = dft.format(c.getTime());

                    rental_confirm(rideNow_date, rideNow_time);
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("No Location Found");
                    alertDialog.setMessage("Please Select Pickup Location");
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

        ridelater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pick_up_loc.equals("")) {
                    final Calendar c = Calendar.getInstance();
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {

                                @SuppressLint("DefaultLocale")
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    SimpleDateFormat dateof = new SimpleDateFormat("dd-MM-yyyy");
                                    rideLater_date = dateof.format(c.getTime());
                                    getTime();

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("No Location Found");
                    alertDialog.setMessage("Please Select Pickup Location");
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

    private void getTime() {

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog2 = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        SimpleDateFormat timeof = new SimpleDateFormat("HH:mm");
                        rideLater_time = timeof.format(c.getTime());
                        rental_confirm(rideLater_date, rideLater_time);
                    }

                }, mHour, mMinute, false);
        timePickerDialog2.show();


    }

    private void rental_confirm(String rideLater_date, String rideLater_time) {

            Intent ren = new Intent(getContext(), RentalActivity.class);
            ren.putExtra("pick", pick_up_loc);
            ren.putExtra("date", rideLater_date);
            ren.putExtra("time", rideLater_time);
            ren.putExtra("v_type", v_type);
            startActivity(ren);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}

