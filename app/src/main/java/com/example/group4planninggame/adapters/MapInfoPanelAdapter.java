package com.example.group4planninggame.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.group4planninggame.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapInfoPanelAdapter implements GoogleMap.InfoWindowAdapter {

    private final View window;

    public MapInfoPanelAdapter(Activity context) {
        window = LayoutInflater.from(context).inflate(R.layout.info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);

        if (!title.equals("")) {
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView line1 = view.findViewById(R.id.snippetLine1);
        TextView line2 = view.findViewById(R.id.snippetLine2);

        if (snippet != null) {
            String[] lines = snippet.split("\n", 2);
            if (lines.length > 0) {
                line1.setText(lines[0]);
            }
            if(lines.length > 1) {
                line2.setText(lines[1]);
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }
}
