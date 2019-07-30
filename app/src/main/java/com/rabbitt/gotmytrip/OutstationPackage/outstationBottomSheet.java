


package com.rabbitt.gotmytrip.OutstationPackage;

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
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rabbitt.gotmytrip.RentalPackage.BookBottomSheet;
import com.rabbitt.gotmytrip.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class outstationBottomSheet extends BottomSheetDialogFragment {

    private static String pick_up_loc = null;
    private static String drop_loc = null;
    private static String v_type = null;
    private static String type = null;
    private static String ori_lat = null;
    private static String ori_lng = null;
    private static String dest_lat = null;
    private static String dest_lng = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.outstation_bottom_sheet, container, false);

        Button rideNow = v.findViewById(R.id.ride_now2);
        Button rideLater = v.findViewById(R.id.rideLater);
        ImageView vehicle_icon = v.findViewById(R.id.iconcard);

        assert getArguments() != null;
        pick_up_loc = getArguments().getString("pickn");
        v_type = getArguments().getString("vehicle");
        type = getArguments().getString("travel_type");
        drop_loc = getArguments().getString("dropn");
        ori_lat = getArguments().getString("ori_lat");
        ori_lng = getArguments().getString("ori_lng");
        dest_lat = getArguments().getString("dest_lat");
        dest_lng = getArguments().getString("dest_lng");

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

//        need attention to this warning
        rideNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pick_up_loc.equals("")&&!drop_loc.equals("")) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
                    String rideNow_date = df.format(c.getTime());
                    String rideNow_time = dft.format(c.getTime());

                    Intent tocity = new Intent(getContext(), OutstationAcitvity.class);
                    tocity.putExtra("pick_up", pick_up_loc);
                    tocity.putExtra("drop", drop_loc);
                    tocity.putExtra("v_type", v_type);
                    tocity.putExtra("travel_type", type);
                    tocity.putExtra("ori_lat", ori_lat);
                    tocity.putExtra("ori_lng", ori_lng);
                    tocity.putExtra("dest_lat", dest_lat);
                    tocity.putExtra("dest_lng", dest_lng);
                    tocity.putExtra("date", rideNow_date);
                    tocity.putExtra("time", rideNow_time);

                    startActivity(tocity);
                } else {
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

        rideLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pick_up_loc.equals("")&&!drop_loc.equals("")) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = 0;
                    int mMonth = 0;
                    int mDay = 0;
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {

                                @SuppressLint("DefaultLocale")
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    SimpleDateFormat dateof = new SimpleDateFormat("dd-MM-yyyy");
                                    String rideLater_date = dateof.format(c.getTime());
                                    getTime(rideLater_date);

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

    private void getTime(final String ridedate) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog2 = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        SimpleDateFormat timeof = new SimpleDateFormat("HH:mm");
                        String rideLater_time = timeof.format(c.getTime());
                        rideLaterIntent(ridedate, rideLater_time);
                    }

                }, mHour, mMinute, false);
        timePickerDialog2.show();
    }

    public void rideLaterIntent(String dateon, String timeat)
    {
        Intent tocity = new Intent(getContext(), OutstationAcitvity.class);
        tocity.putExtra("pick_up", pick_up_loc);
        tocity.putExtra("drop", drop_loc);
        tocity.putExtra("v_type", v_type);
        tocity.putExtra("travel_type", type);
        tocity.putExtra("ori_lat", ori_lat);
        tocity.putExtra("ori_lng", ori_lng);
        tocity.putExtra("dest_lat", dest_lat);
        tocity.putExtra("dest_lng", dest_lng);
        tocity.putExtra("date", dateon);
        tocity.putExtra("time", timeat);

        startActivity(tocity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            BookBottomSheet.BottomSheetListener mListener = (BookBottomSheet.BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}


