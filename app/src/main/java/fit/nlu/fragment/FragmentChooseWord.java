package fit.nlu.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fit.nlu.main.RoomActivity;
import fit.nlu.main.R;

public class FragmentChooseWord extends Fragment {
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_word, container, false);
//
//        // Giả lập 3 từ để chọn
//        Button btnWord1 = view.findViewById(R.id.btnWord1);
//        Button btnWord2 = view.findViewById(R.id.btnWord2);
//        Button btnWord3 = view.findViewById(R.id.btnWord3);
//
//        View.OnClickListener wordClickListener = v -> {
//            // Khi chọn từ xong, chuyển sang giao diện vẽ
//            ((RoomActivity) requireActivity()).replaceFragment(new FragmentDraw());
//        };
//
//        btnWord1.setOnClickListener(wordClickListener);
//        btnWord2.setOnClickListener(wordClickListener);
//        btnWord3.setOnClickListener(wordClickListener);

        return view;
    }
}
