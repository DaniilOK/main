package bigbottleapps.fluffer1.Fragments.MainActivityFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import bigbottleapps.fluffer1.Controllers.MainActivity;
import bigbottleapps.fluffer1.R;

public class RefreshFragment2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_network_error, container, false);
        ImageButton refresh = (ImageButton)view.findViewById(R.id.refresh);
        Toast.makeText(view.getContext(), "New", Toast.LENGTH_SHORT).show();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).startNew();
            }
        });
        return view;
    }
}