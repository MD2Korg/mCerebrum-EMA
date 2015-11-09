package org.md2k.ema;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.utilities.Report.Log;

public abstract class ActivityAbstractInterview extends Activity {
    private static final String TAG = ActivityAbstractInterview.class.getSimpleName();
    int state;
    long timeout;
    String display_name;
    String id;
    String file_name;
    static final int AT_START = 0;
    static final int TIMED_OUT = 1;
    static final int DONE = 2;

    // handles timing events
    Handler handler;

    QuestionAnswers questionAnswers;

    abstract void updateUI();

//    abstract void setupInitialUI();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        manageState();
    }

    void manageState() {
        long curTime = DateTime.getDateTime();
        switch (state) {
            case AT_START:
                questionAnswers.setStartTime(curTime);
                handler.removeCallbacks(timeoutInterview);
                handler.postDelayed(timeoutInterview, timeout * 1000);
                updateUI();
                break;
            case TIMED_OUT:
                handler.postDelayed(stopInterview, 4000);
                questionAnswers.setStatus(Constants.EMA_ABANDONED);
                updateUI();
                break;
            case DONE:
                handler.removeCallbacks(timeoutInterview);
                questionAnswers.setEndTime(DateTime.getDateTime());
                questionAnswers.setStatus(Constants.EMA_COMPLETED);
                handler.postDelayed(stopInterview, 4000);
                updateUI();
                break;
        }
    }

    void initQuestionAnswer() {
        QuestionManager.getInstance(this,id,file_name).clear();
        questionAnswers = QuestionManager.getInstance(this, id,file_name).questionAnswers;
    }

    public void initInterviewState() {
        state = AT_START;
    }


    /*
     * This runnable is used to run the prompting behavior of the application
	 */

    private Runnable timeoutInterview = new Runnable() {
        public void run() {
            handler.removeCallbacks(timeoutInterview);
            state=TIMED_OUT;
            manageState();
        }
    };
    private Runnable stopInterview = new Runnable() {
        public void run() {
            finish();
        }
    };
}
