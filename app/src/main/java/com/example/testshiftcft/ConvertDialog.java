package com.example.testshiftcft;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ConvertDialog extends AppCompatDialogFragment {
    private EditText etConvertValue;
    private Button btnConvert;
    private TextView result;
    private Double rubVal, othVal, resVal;
    private String name, index;

    public ConvertDialog(String name, String index, Double othVal) {
        this.othVal = othVal;
        this.name = name;
        this.index = index;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle(name)
                .setNegativeButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        etConvertValue = view.findViewById(R.id.convertValue);
        btnConvert = view.findViewById(R.id.btnConvert);
        result = view.findViewById(R.id.finalResult);

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(etConvertValue.getText().toString())){
                    rubVal = Double.parseDouble(etConvertValue.getText().toString());
                    resVal = rubVal * othVal;
                    result.setText(rubVal + " RUB - " + resVal + " " + index);

                } else {
                    Toast.makeText(getActivity(), "Enter a number", Toast.LENGTH_SHORT).show();
                }

//                result.setText("WTF?");
            }
        });

        return builder.create();
    }
}
