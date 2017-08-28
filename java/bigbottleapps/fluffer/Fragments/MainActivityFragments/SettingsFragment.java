package bigbottleapps.fluffer.Fragments.MainActivityFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bigbottleapps.fluffer.CityFragment;
import bigbottleapps.fluffer.Controllers.MainActivity;
import bigbottleapps.fluffer.Controllers.RegisterOrLogInActivity;
import bigbottleapps.fluffer.R;


public class SettingsFragment extends Fragment {

    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    public static final String APP_PREFERENCES_CITY = "city";
    private SharedPreferences mSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_settings, container, false);
        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        final Button loginB = (Button)view.findViewById(R.id.log_in);
        final Button logoutB = (Button)view.findViewById(R.id.log_out);
        final Button changepassB = (Button)view.findViewById(R.id.change_pass);
        final Button sendmsgtB = (Button)view.findViewById(R.id.send_msg);
        final Button changeCityB = (Button)view.findViewById(R.id.change_city);
        String cities[] = getResources().getStringArray(R.array.cities);

        if (mSettings.contains(APP_PREFERENCES_LOE))
            loginB.setVisibility(View.GONE);
        else {
            logoutB.setVisibility(View.GONE);
            changepassB.setVisibility(View.GONE);
        }

        logoutB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettings.edit().clear().commit();
                loginB.setVisibility(View.VISIBLE);
                logoutB.setVisibility(View.GONE);
                changepassB.setVisibility(View.GONE);
            }
        });

        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginB.setVisibility(View.GONE);
                changepassB.setVisibility(View.VISIBLE);
                logoutB.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getActivity().getApplicationContext(), RegisterOrLogInActivity.class);
                intent.putExtra("from","settings");
                startActivity(intent);
            }
        });

        changepassB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setForgetFragment();
            }
        });

        sendmsgtB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setSendTextFragment();
            }
        });

        changeCityB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setDialog();
            }
        });
        return view;
    }

    public void setDialog(){
        DialogFragment dialog = new CityFragment();
        dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }


}