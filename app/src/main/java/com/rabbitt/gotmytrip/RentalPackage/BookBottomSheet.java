package com.rabbitt.gotmytrip.RentalPackage;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookBottomSheet extends BottomSheetDialogFragment {

    Boolean check_time = false, check_date = false;
    private String rideLater_date, rideLater_time, pick_up_loc, v_type = "2";

    private int mYear;
    private int mMonth;
    private int mDay;
    private static String ori_lat = null;
    private static String ori_lng = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_container, container, false);


        Button ridenow = v.findViewById(R.id.ride_now2);
        Button ridelater = v.findViewById(R.id.button3);
        ImageView vehicle_icon = v.findViewById(R.id.iconcard);
        TextView price_txt = v.findViewById(R.id.price);

        assert getArguments() != null;
        pick_up_loc = getArguments().getString("pickn");
        v_type = getArguments().getString("vehicle");
        String price = getArguments().getString("base_fare");
        ori_lat = getArguments().getString("ori_lat");
        ori_lng = getArguments().getString("ori_lng");

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
        if ("SUV".equals(v_type)) {
            v_type = "3";
        } else {
            v_type = "2";
        }


        ridenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pick_up_loc.equals("")) {
                    Calendar c = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
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

        final int finalMYear = mYear;
        final int finalMMonth = mMonth;
        final int finalMDay = mDay;

        ridelater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pick_up_loc.equals("")) {
//                    final Calendar c = Calendar.getInstance();
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @SuppressLint("DefaultLocale")
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    SimpleDateFormat dateof = new SimpleDateFormat("yyyy-MM-dd");
//                                    rideLater_date = dateof.format(c.getTime());
                                    getTime();

                                }
                            }, finalMYear, finalMMonth, finalMDay);
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

    private void getCurrentDateTime() {
//        Calendar c = Calendar.getInstance();
////        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
////        @SuppressLint("SimpleDateFormat") SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
////        String rideNow_date = df.format(c.getTime());
////        String rideNow_time = dft.format(c.getTime());
//        dateonTxt.setText(rideNow_date);
//        timeatTxt.setText(rideNow_time);
    }

    private void getTime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog2 = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat timeof = new SimpleDateFormat("HH:mm");
                        rideLater_time = timeof.format(c.getTime());
                        rental_confirm(rideLater_date, rideLater_time);
                    }
                }, mHour, mMinute, false);
        timePickerDialog2.show();
    }

    private void rental_confirm(String rideLater_date, String rideLater_time) {
        //to collapse bottomsheet
            dismiss();
            Intent ren = new Intent(getContext(), RentalActivity.class);
            ren.putExtra("pick", pick_up_loc);
            ren.putExtra("date", rideLater_date);
            ren.putExtra("time", rideLater_time);
            ren.putExtra("v_type", v_type);
            ren.putExtra("ori_lat", ori_lat);
            ren.putExtra("ori_lng", ori_lng);
            startActivity(ren);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            BottomSheetListener mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}

