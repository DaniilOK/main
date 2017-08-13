package bigbottleapps.fluffer1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bigbottleapps.fluffer1.R;


public class SettingsFragment extends Fragment {
    public static final String APP_PREFERENCES = "users";
    public static final String APP_PREFERENCES_LOE = "loe";
    public static final String APP_PREFERENCES_LOGGED = "logged";

    private SharedPreferences mSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        Button button = (Button)view.findViewById(R.id.log_out);
        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettings.contains(APP_PREFERENCES_LOE)) {
                    mSettings.edit().clear().commit();
                }
            }
        });
        Button clearLogged = (Button)view.findViewById(R.id.clear_logged);
        clearLogged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSettings.contains(APP_PREFERENCES_LOGGED)){
                    mSettings.edit().remove(APP_PREFERENCES_LOGGED).commit();
                }
            }
        });
        return view;
    }
}
