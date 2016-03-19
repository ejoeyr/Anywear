package ph.clothesuffle.anywear.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import ph.clothesuffle.anywear.R;
import ph.clothesuffle.anywear.interfaces.AdapterInterface;
import ph.clothesuffle.anywear.models.Folder;
import ph.clothesuffle.anywear.utilities.Constants;
import ph.clothesuffle.anywear.utilities.FileUtils;

/**
 * Created by joeyramirez on 3/3/2016.
 */
public class RenameFolderDialog extends DialogFragment {


    private AdapterInterface adapterInterface;
    private Folder folder;

    public RenameFolderDialog() {


    }
    public static RenameFolderDialog newInstance(AdapterInterface adapterInterface,Bundle bundle)
    {
        RenameFolderDialog renameFolderDialog = new RenameFolderDialog();
        renameFolderDialog.setAdapterInterface(adapterInterface);
        renameFolderDialog.setArguments(bundle);

        return renameFolderDialog;
    }

    public void setAdapterInterface(AdapterInterface adapterInterface)
    {
        this.adapterInterface = adapterInterface;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.dialog_edit_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.editText_folderName);
        editText.setHint(R.string.string_rename_hint);

        folder = (Folder) getArguments().getSerializable(Constants.FRAGMENT_KEY_FOLDER);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);

        builder.setNegativeButton("Cancel", null)
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFileName = editText.getText().toString();

                        if (!newFileName.isEmpty()) {

                            if (FileUtils.renameFolder(folder.getFileName(),newFileName ))
                            {

                                Toast.makeText(getActivity(),"RENAMED",Toast.LENGTH_SHORT).show();
                                adapterInterface.loadRename();
                            }else
                            {
                                Toast.makeText(getActivity(),"NOT RENAMED",Toast.LENGTH_SHORT).show();
                            }
                        }else
                        {
                            Toast.makeText(getActivity(),"EMPTY",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setTitle("Rename \"" + folder.getFileName() + "\"");

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
}
