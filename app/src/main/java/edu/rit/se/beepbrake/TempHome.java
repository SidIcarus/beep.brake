package edu.rit.se.beepbrake;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.rit.se.beepbrake.Analysis.AnalysisActivity;
import edu.rit.se.beepbrake.MockStream.MockActivity;
import edu.rit.se.beepbrake.Segment.SegmentActivity;

public class TempHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_home);

        //set buttons
        Button b1 = (Button) findViewById(R.id.livefeedbutton);
        Button b2 = (Button) findViewById(R.id.mockvideobutton);
        Button b3 = (Button) findViewById(R.id.segmentpreview);

        b1.setOnClickListener(new SwitchActivity(this, AnalysisActivity.class));
        b2.setOnClickListener(new SwitchActivity(this, MockActivity.class));
        b3.setOnClickListener(new SwitchActivity(this, SegmentActivity.class));
    }


    public class SwitchActivity implements View.OnClickListener{

        private Context currentActivity;
        private Class nextActivity;

        public SwitchActivity(Context context, Class nextActivity){
            this.currentActivity = context;
            this.nextActivity = nextActivity;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(currentActivity, nextActivity);
            currentActivity.startActivity(i);
        }
    }

}
