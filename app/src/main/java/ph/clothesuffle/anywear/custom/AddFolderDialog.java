package ph.clothesuffle.anywear.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.interfaces.NavigationDrawerInterface;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 3/2/2016.
 */
public class AddFolderDialog extends DialogFragment {


    private EditText editText_folderName;
    private View rootView;
    private AlertDialog.Builder alertDialog_builder;
    private NavigationDrawerInterface navigationDrawerInterface;

    public AddFolderDialog() {

    }

    public static AddFolderDialog newInstance(NavigationDrawerInterface navigationDrawerInterface) {
        AddFolderDialog addFolderDialog = new AddFolderDialog();
        /*Set the interface*/
        addFolderDialog.setNavigationDrawerInterface(navigationDrawerInterface);
        /*Return it*/
        return addFolderDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        rootView = View.inflate(getActivity(), R.layout.dialog_edit_text, null);

        editText_folderName = (EditText) rootView.findViewById(R.id.editText_folderName);

        alertDialog_builder = new AlertDialog.Builder
                (getActivity(), R.style.AlertDialogStyle)
                .setTitle(R.string.string_create_new_folder)
                .setPositiveButton(R.string.string_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String folderName ="";
                        folderName =  editText_folderName.getText().toString();
                        if (!folderName.isEmpty())
                        {
                            folderName = (folderName.substring(0,1).toUpperCase())+(folderName.substring(1));

                            boolean success = FileUtils.createFolder(folderName);

                            if (success)
                            {
                                navigationDrawerInterface.reloadAdapter();
                            }
                            else
                            {
                                Toast.makeText(getActivity(), R.string.string_folder_exists, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getActivity(), R.string.string_no_folder_created, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.string_cancel), null);

        alertDialog_builder.setView(rootView);

        return alertDialog_builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    public void setNavigationDrawerInterface(NavigationDrawerInterface navigationDrawerInterface) {

        this.navigationDrawerInterface = navigationDrawerInterface;
    }
}
