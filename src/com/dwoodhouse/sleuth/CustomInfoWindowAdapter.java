package com.dwoodhouse.sleuth;

import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements InfoWindowAdapter {

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	 /*
    private View view;

    public CustomInfoWindowAdapter() {
        view = getLayoutInflater().inflate(R.layout,
                null);
    }

    @Override
    public View getInfoContents(Marker marker) {

        if (MainActivity.this.marker != null
                && MainActivity.this.marker.isInfoWindowShown()) {
            MainActivity.this.marker.hideInfoWindow();
            MainActivity.this.marker.showInfoWindow();
        }
        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        MainActivity.this.marker = marker;

        String url = null;

        if (marker.getId() != null && markers != null && markers.size() > 0) {
            if ( markers.get(marker.getId()) != null &&
                    markers.get(marker.getId()) != null) {
                url = markers.get(marker.getId());
            }
        }
        final ImageView image = ((ImageView) view.findViewById(R.id.badge));

        if (url != null && !url.equalsIgnoreCase("null")
                && !url.equalsIgnoreCase("")) {
            imageLoader.displayImage(url, image, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view,
                                    loadedImage);
                            getInfoContents(marker);
                        }
                    });
        } else {
            image.setImageResource(R.drawable.ic_launcher);
        }

        final String title = marker.getTitle();
        final TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            titleUi.setText(title);
        } else {
            titleUi.setText("");
        }

        final String snippet = marker.getSnippet();
        final TextView snippetUi = ((TextView) view
                .findViewById(R.id.snippet));
        if (snippet != null) {
            snippetUi.setText(snippet);
        } else {
            snippetUi.setText("");
        }

        return view;
    }
    */
}
