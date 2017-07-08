package soa.unlam.edu.ar.chobitemp.settings;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;

import soa.unlam.edu.ar.chobitemp.ChobiConstants;
import soa.unlam.edu.ar.chobitemp.R;

/**
 * Created by mcurrao on 17/06/17.
 */

public abstract class AlertDialogBuilder {

    public static AlertDialog serverSetup(final Context context, final OnConfirmListener onConfirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.input_server_url_title));

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);

        SharedPreferences prefs = context.getSharedPreferences(ChobiConstants.SharedPreferences.CHOBI_PREFERENCES, Application.MODE_PRIVATE);
        String currentEndpointUrl = prefs.getString(ChobiConstants.SharedPreferences.CHOBI_SERVICE_ENDPOINT, null);
        if(currentEndpointUrl != null) {
            input.setText(currentEndpointUrl);
        }

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               onConfirmListener.onConfirm(dialog, context, input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.show();
    }

    public static abstract class OnConfirmListener {
        public abstract void onConfirm(DialogInterface dialog, Context context, String input);
    }

}
