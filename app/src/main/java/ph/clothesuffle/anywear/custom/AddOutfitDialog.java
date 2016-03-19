package ph.clothesuffle.anywear.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 3/15/2016.
 */
public class AddOutfitDialog extends DialogFragment {
    View root;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        final ArrayList<String> args = getArguments().getStringArrayList(Constants.ACTIVITY_KEY_IMAGES);
        root = View.inflate(getActivity(), R.layout.dialog_edit_text, null);
        final EditText editText = (EditText) root.findViewById(R.id.editText_folderName);
        editText.setHint(R.string.string_outfit_name);
        builder.setTitle("Name this outfit                ")
                .setView(root)
                .setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        String outFitName = "";

                        outFitName = editText.getText().toString();

                        if (outFitName.isEmpty())
                        {

                            Toast.makeText(getActivity(), "Name can't be empty!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            outFitName = outFitName.substring(0, 1).toUpperCase() + outFitName.substring(1);

                            String ROOT = Constants.FOLDER_OUTFITS + "/";

                            if (FileUtils.createFolder(ROOT + outFitName))
                            {
                                FileUtils.copyFile(args, ROOT + outFitName);
                                snack("OUTFIT ADDED!");
                            }
                            else
                            {
                                snack("OUTFIT NAME ALREADY TAKEN!");
                            }

                        }

                    }
                }).setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    private void snack(String s) {
        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
}
