package com.example.borja.mapas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class InsertPlaceDialogFragment extends DialogFragment {

    private static final String ARG_OKLISTENER = "okListener";
    private static final String ARG_CANCELLISTENER = "cancelListener";
    private static final String ARG_LATLNG = "latLng";

    private OnOKInsertPlaceListener okListener;
    private OnCancelInsertPlaceListener cancelListener;
    private LatLng latlng;

    public InsertPlaceDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param okListener Parameter 1.
     * @param cancelListener Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    public static InsertPlaceDialogFragment newInstance(OnOKInsertPlaceListener okListener, OnCancelInsertPlaceListener cancelListener, LatLng latlng) {

        InsertPlaceDialogFragment fragment = new InsertPlaceDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OKLISTENER, okListener);
        args.putSerializable(ARG_CANCELLISTENER, cancelListener);
        args.putParcelable(ARG_LATLNG, latlng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            okListener = (OnOKInsertPlaceListener) getArguments().getSerializable(ARG_OKLISTENER);
            cancelListener = (OnCancelInsertPlaceListener)getArguments().getSerializable(ARG_CANCELLISTENER);
            latlng = (LatLng)getArguments().getParcelable(ARG_LATLNG);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.fragment_insert_place_dialog,null);
        builder.setView(contentView);

        final EditText nameEditText = (EditText)contentView.findViewById(R.id.insert_place_title);
        final EditText descriptionEditText = (EditText)contentView.findViewById(R.id.insert_place_description);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                    if (okListener != null) {
                        okListener.operationAccept(nameEditText.getText().toString(),descriptionEditText.getText().toString(), latlng);
                    }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                 if (cancelListener != null) {
                     cancelListener.operationCancel();
                 }
            }
        });

        return builder.create();
    }


    @Override
    public void onDetach() {
        super.onDetach();

        okListener = null;
        cancelListener = null;
    }


    public interface OnOKInsertPlaceListener extends Serializable {
        void operationAccept(String name, String description, LatLng latLng);
    }

    public interface OnCancelInsertPlaceListener extends Serializable {
        void operationCancel();
    }
}
