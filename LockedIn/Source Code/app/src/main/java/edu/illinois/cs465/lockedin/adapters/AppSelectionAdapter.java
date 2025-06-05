package edu.illinois.cs465.lockedin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import edu.illinois.cs465.lockedin.R;
import edu.illinois.cs465.lockedin.models.AppItem;

public class AppSelectionAdapter extends ArrayAdapter<AppItem> {
    private Context context;
    private List<AppItem> appList;

    public AppSelectionAdapter(Context context, List<AppItem> appList) {
        super(context, 0, appList);
        this.context = context;
        this.appList = appList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dialog_item_app_selection, parent, false);
        }

        AppItem appItem = appList.get(position);

        ImageView appIcon = convertView.findViewById(R.id.app_icon);
        TextView appName = convertView.findViewById(R.id.app_name);

        appIcon.setImageDrawable(appItem.getIcon());
        appName.setText(appItem.getName());

        return convertView;
    }
}


