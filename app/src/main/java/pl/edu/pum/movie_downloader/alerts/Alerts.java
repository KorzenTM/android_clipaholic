package pl.edu.pum.movie_downloader.alerts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import pl.edu.pum.movie_downloader.R;

public class Alerts
{
    private final Context mContext;
    private final Activity mActivity;
    private AlertDialog alertDialog;

    public Alerts(Context context, Activity activity)
    {
        this.mContext = context;
        this.mActivity = activity;
    }

    public void showExitFromApplicationAlert()
    {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Exit application");
        alertDialog.setMessage("Do you really want to leave the application?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mActivity.finish();
                        System.exit(0);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showNoActivatedAccountAlert(AlertDialogState alertDialogState)
    {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Account not activated");
        alertDialog.setMessage("In order to log in, you must activate your account.\n" +
                "You will find the link to do this in the message sent\n" +
                "after creating your account.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Send activation e-mail again",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        alertDialogState.onSendEmailAgainButtonClicked(true);
                    }
                });
        alertDialog.show();
    }

    public void showWrongUserDataAlert()
    {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Login failure");
        alertDialog.setMessage("An error occurred during sign in.\n" +
                "Please check your login details or try again later.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showErrorAlert()
    {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Register failure");
        alertDialog.setMessage("An error occurred during sign in.\n" +
                "Please check your registration details or try again later.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void showSendEmailErrorAlert()
    {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Sending error email");
        alertDialog.setMessage("An error occurred during sending an email.\n" +
                "Please check your detail or try again later.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
