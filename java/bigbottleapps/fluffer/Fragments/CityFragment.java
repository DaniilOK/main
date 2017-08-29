package bigbottleapps.fluffer.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import bigbottleapps.fluffer.Controllers.MainActivity;
import bigbottleapps.fluffer.Fragments.MainActivityFragments.SettingsFragment;
import bigbottleapps.fluffer.R;


public class CityFragment extends DialogFragment {
    int city = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.your_city))
                .setSingleChoiceItems(R.array.cities, getActivity().getSharedPreferences("users", Context.MODE_PRIVATE).getInt("city", -1),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                ((MainActivity)getActivity()).setCity(item);
                                dialog.dismiss();
                            }
                        })
//                .setPositiveButton("Оk", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        ((MainActivity)getActivity()).setCity(city);
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                })
        .setCancelable(false);

        return builder.create();
    }
}
