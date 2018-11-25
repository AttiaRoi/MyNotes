package roiattia.com.mynotes.ui.folder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import roiattia.com.mynotes.R;

import static roiattia.com.mynotes.utils.Constants.FOLDER_ID_KEY;

public class FolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        ButterKnife.bind(this);

        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(FOLDER_ID_KEY)){
                //TODO: load folder data
                // TODO: load notes data
            }
        }
    }
}
