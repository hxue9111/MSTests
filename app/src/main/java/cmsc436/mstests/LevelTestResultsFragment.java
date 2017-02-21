package cmsc436.mstests;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LevelTestResultsFragment extends Fragment {


    public LevelTestResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_level_test, container, false);
        Double leftHandScore = getArguments().getDouble("leftHandScore");
        Double rightHandScore = getArguments().getDouble("rightHandScore");
        TextView text = (TextView) view.findViewById(R.id.display_score);
        text.setText("Left Hand Score: " + leftHandScore +
                            "\nRight Hand Score: " + rightHandScore);
        view.setBackgroundColor(getResources().getColor(android.R.color.white));

        return view;
    }

}
