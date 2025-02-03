package fit.nlu.adapter.spiner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import fit.nlu.adapter.spiner.model.BaseSpinnerItem;
import fit.nlu.main.R;

public class CustomSpinnerAdapter<T extends BaseSpinnerItem> extends ArrayAdapter<T> {

    public CustomSpinnerAdapter(@NonNull Context context,
                                int selectedLayout,
                                @NonNull List<T> objects) {
        super(context, selectedLayout, objects);
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_spinner_selected, parent, false);
        TextView tvSelected = convertView.findViewById(R.id.tvSpinnerItem);

        T item = this.getItem(position);
        if (item != null) {
            tvSelected.setText(item.getDisplayText());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_spinner, parent, false);
        TextView tvItem = convertView.findViewById(R.id.tvSpinnerItem);
        T item = getItem(position);
        if (item != null) {
            tvItem.setText(item.getDisplayText());
        }
        return convertView;
    }
}
