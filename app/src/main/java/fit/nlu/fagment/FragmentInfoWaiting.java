package fit.nlu.fagment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fit.nlu.main.R;

public class FragmentInfoWaiting extends Fragment {
    private String playerName;

    public FragmentInfoWaiting(String playerName) {
        this.playerName = playerName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_waiting, container, false);

        TextView txtInfo = view.findViewById(R.id.txtInfo);
        txtInfo.setText(playerName + " đang chọn từ...");

        return view;
    }
}
