package bigbottleapps.fluffer1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by mikhail on 12.08.17.
 */

public class RefreshFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_network_error, container, false);
        Toast.makeText(view.getContext(), "Home", Toast.LENGTH_SHORT).show();
        ImageButton refresh = (ImageButton)view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setActionList();
            }
        });
        return view;
    }
}
