package org.md2k.ema;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.md2k.datakitapi.time.DateTime;

public abstract class ActivityAbstractInterview extends Activity {
    int state;
    static final int AT_START = 0;
    static final int POST_DELAY = 1;
    static final int DELAYED = 2;
    static final int CONDUCTING = 3;
    static final int TIMED_OUT = 4;
    static final int DONE = 5;
    long remainingTime;

    // handles timing events
    Handler handler;
    // prompt control
    int promptTimes;
    static final int VOLUME = 100;
    static final int PROMPT_INTERVAL = 500;
    static final int PROMPT_REPEAT = 5;

    QuestionAnswers questionAnswers;
    String emaType;
    static final String START_MESSAGE = "Do you want to start the interview now?";
    static final String START_STRING = "Start";
    int beepCount;
    AlertDialog startDialog;
    EMA_Info ema_info;

    abstract void setupInterviewUI();

    abstract void setupInitialUI();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ema_info = EMA_Info.getInstance(this);

        initQuestionAnswer();
        initInterviewState();
        handler = new Handler();
        manageState();

    }

    void manageState() {
        long curTime = DateTime.getDateTime();
        switch (state) {
            case AT_START:
                setupInitialUI();
                long promptTime = questionAnswers.getPromptTime();
                handler.removeCallbacks(setAlarm);
                handler.removeCallbacks(promptUser);
                handler.removeCallbacks(launchStartDialog);
                handler.removeCallbacks(timeoutInterview);
                updateUI();
                if (promptTime == -1) {
                    beepCount = 0;
                    questionAnswers.setPromptTime(curTime);
                    handler.post(launchStartDialog);
                    handler.post(setAlarm);
                    handler.postDelayed(timeoutInterview, ema_info.ema_timeout.start_timeout * 1000);
                } else {
                    // returning from interrupt
                    if (curTime - promptTime >= ema_info.ema_timeout.start_timeout * 1000) {
                        // timed out
                        handler.post(timeoutInterview);
                    } else {
                        // interrupted in the middle of the start dialog
                        handler.postDelayed(timeoutInterview, ema_info.ema_timeout.start_timeout * 1000 - (curTime - promptTime));
                        handler.post(launchStartDialog);
                        handler.post(setAlarm);
                    }
                }
                break;
            case POST_DELAY:
                handler.removeCallbacks(setAlarm);
                handler.removeCallbacks(promptUser);
                handler.removeCallbacks(launchStartDialog);
                handler.removeCallbacks(timeoutInterview);
                handler.removeCallbacks(updateRemainingTime);
                handler.post(launchStartDialog);
                handler.post(setAlarm);
                handler.postDelayed(timeoutInterview, ema_info.ema_timeout.start_timeout * 1000);
                break;
            case CONDUCTING:
                handler.removeCallbacks(updateRemainingTime);
                handler.removeCallbacks(setAlarm);
                handler.removeCallbacks(promptUser);
                handler.removeCallbacks(timeoutInterview);
                handler.removeCallbacks(launchStartDialog);
                handler.postDelayed(timeoutInterview, ema_info.ema_timeout.interview_timeout * 1000);
                handler.postDelayed(startInterview, 2000);
                questionAnswers.setStartTime(DateTime.getDateTime());
                updateUI();
                break;
            case TIMED_OUT:
                setupInitialUI();
                updateUI();
                handler.post(promptUser);
                handler.postDelayed(stopInterview, 2000);

                break;
            case DONE:
                handler.removeCallbacks(timeoutInterview);
                questionAnswers.setEndTime(DateTime.getDateTime());
                updateUI();
                handler.postDelayed(stopInterview, 2000);
                //TODO: save & quit
                break;
            case DELAYED:
                questionAnswers.setDelayStartTime(DateTime.getDateTime());
                handler.removeCallbacks(setAlarm);
                handler.removeCallbacks(promptUser);
                handler.removeCallbacks(launchStartDialog);
                handler.removeCallbacks(timeoutInterview);
                updateUI();
                handler.postDelayed(timeoutInterview, ema_info.ema_timeout.user_delay * 1000);
                remainingTime = ema_info.ema_timeout.user_delay;
                handler.post(updateRemainingTime);
                break;
        }
    }

    void initQuestionAnswer() {
        emaType = getIntent().getStringExtra("ema_type");
        QuestionManager.getInstance(this,emaType).clear();
        questionAnswers = QuestionManager.getInstance(this, emaType).questionAnswers;
    }

    private void initInterviewState() {
        state = AT_START;
        promptTimes = PROMPT_REPEAT;
    }

    /*
     * This runnable object is posted to launch the start dialog box
     */
    private Runnable launchStartDialog = new Runnable() {
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAbstractInterview.this);
            builder.setMessage(START_MESSAGE)
                    .setCancelable(false)
                    .setOnKeyListener(new OnKeyListener() {
                        // need to handle the hardware keys here as well as in the Interview activity
                        public boolean onKey(DialogInterface dialog, int keyCode,
                                             KeyEvent keyEvent) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    // disable the back button while in the interview
                                }
                                if (keyCode == KeyEvent.KEYCODE_CALL) {
                                    // disable the call button
                                    return true;
                                }
                                if (keyCode == KeyEvent.KEYCODE_CAMERA) {
                                    // disable camera
                                    return true;
                                }
                                if (keyCode == KeyEvent.KEYCODE_POWER) {
                                    // don't know if this can be disabled here
                                    return true;
                                }
                                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                                    // disable search
                                    return true;
                                }
                            }
                            // all other key presses are not handled
                            return false;
                        }
                    })
                    .setPositiveButton(START_STRING, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if (state == POST_DELAY)
                                questionAnswers.setDelayStopTime(DateTime.getDateTime());
                            state = CONDUCTING;
                            manageState();
                        }
                    });
            if (state != POST_DELAY)
                builder.setNegativeButton("Delay " + Long.toString(ema_info.ema_timeout.user_delay / 60) + " Minutes"
                        , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        state = DELAYED;
                        questionAnswers.setDelayStartTime(System.currentTimeMillis());
                        handler.removeCallbacks(timeoutInterview);
                        handler.postDelayed(launchStartDialog, ema_info.ema_timeout.user_delay * 1000);
                        manageState();
                    }
                });
            startDialog = builder.create();
            startDialog.show();
        }
    };

    /*
     * This runnable is used to run the prompting behavior of the application
	 */
    void updateUI() {
        switch (state) {
            case AT_START:
                findViewById(R.id.button_start_interview).setVisibility(View.INVISIBLE);
                findViewById(R.id.textView_remaining_time).setVisibility(View.INVISIBLE);
                updateTextView(null);
                break;
            case CONDUCTING:
                findViewById(R.id.button_start_interview).setVisibility(View.INVISIBLE);
                findViewById(R.id.textView_remaining_time).setVisibility(View.INVISIBLE);

                updateTextView("Please wait...questions will appear shortly...");
                break;
            case DELAYED:
                findViewById(R.id.button_start_interview).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.button_start_interview)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        questionAnswers.setDelayStopTime(DateTime.getDateTime());
                        state = CONDUCTING;
                        manageState();
                    }
                });
                findViewById(R.id.textView_remaining_time).setVisibility(View.VISIBLE);
                updateTextView("Interview is delayed.");
                break;
            case TIMED_OUT:
                findViewById(R.id.button_start_interview).setVisibility(View.INVISIBLE);
                findViewById(R.id.textView_remaining_time).setVisibility(View.INVISIBLE);
                updateTextView("The interview has timed out.");
                break;
            case DONE:
                setupInitialUI();
                findViewById(R.id.button_start_interview).setVisibility(View.INVISIBLE);
                findViewById(R.id.textView_remaining_time).setVisibility(View.INVISIBLE);
                updateTextView("Interview Completed.  Thank you!");
                break;
        }
    }

    private void prompt() {
        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
        ToneGenerator tone = new ToneGenerator(android.media.AudioManager.STREAM_DTMF, VOLUME);
        tone.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, 500);
        tone.release();
    }

    private Runnable setAlarm = new Runnable() {
        public void run() {
            promptTimes = PROMPT_REPEAT;
            handler.post(promptUser);
            beepCount++;
            if (beepCount < ema_info.ema_notification[0].beep_count)
                handler.postDelayed(this, ema_info.ema_notification[0].beep_delay * 1000);
        }
    };
    private Runnable updateRemainingTime = new Runnable() {
        public void run() {
            TextView textView = (TextView) findViewById(R.id.textView_remaining_time);
            textView.setText("Interview Resumed after: " + String.format("%02d:%02d", remainingTime / 60, remainingTime % 60));
            remainingTime--;
            handler.postDelayed(this, 1000);
        }
    };

    private Runnable promptUser = new Runnable() {
        public void run() {
            if (--promptTimes > 0) {
                prompt();
                handler.postDelayed(this, PROMPT_INTERVAL);
            }
        }
    };
    private Runnable timeoutInterview = new Runnable() {
        public void run() {
            handler.removeCallbacks(setAlarm);
            handler.removeCallbacks(promptUser);
            handler.removeCallbacks(timeoutInterview);
            handler.removeCallbacks(launchStartDialog);
            switch (state) {
                case AT_START:
                    if (startDialog != null && startDialog.isShowing())
                        startDialog.cancel();
                    questionAnswers.setEmaStatus(Constants.EMA_MISSED);
                    state = TIMED_OUT;
                    manageState();
                    break;
                case DELAYED:
                    if (startDialog != null && startDialog.isShowing())
                        startDialog.cancel();
                    state = POST_DELAY;
                    manageState();
                case POST_DELAY:
//                    handler.post(updateView);
                    handler.post(setAlarm);
                case CONDUCTING:
                    questionAnswers.setEmaStatus(Constants.EMA_ABANDONED);
                    state = TIMED_OUT;
                    manageState();
                    break;
                case TIMED_OUT:
///**/                    if (currQuestion > 0)
                    //entry.setAbandoned();
///**/                        questionsAnswers.setEmaStatus(Constants.EMA_ABANDONED);
///**/                    else
///**/                        questionsAnswers.setEmaStatus(Constants.EMA_MISSED);
                case DONE:
                default:
                    questionAnswers.setEmaStatus(Constants.EMA_COMPLETED);

                    // final finish the interview activity
                    finish();
                    break;
            }
        }
    };
    private Runnable startInterview = new Runnable() {
        public void run() {
            setupInterviewUI();
        }
    };
    private Runnable stopInterview = new Runnable() {
        public void run() {
            finish();
        }
    };

    void updateTextView(String text) {
        TextView textView = (TextView) findViewById(R.id.textView_text);
        if (text == null) {
            textView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);

        }
    }
}
