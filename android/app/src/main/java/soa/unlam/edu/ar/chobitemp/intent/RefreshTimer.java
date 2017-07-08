package soa.unlam.edu.ar.chobitemp.intent;

import android.app.Activity;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Timer;
import java.util.TimerTask;

import soa.unlam.edu.ar.chobitemp.ChobiConstants;
import soa.unlam.edu.ar.chobitemp.temp.RemoteStatus;

/**
 * Created by mcurrao on 06/07/17.
 */

public class RefreshTimer {

    private Timer timer; // Self refreshing timer
    private TimerTask task;
    private TimedRefreshListener<RemoteStatus> refreshListener;
    private Activity context;

    public RefreshTimer(Activity context, TimedRefreshListener<RemoteStatus> timedRefreshListener) {
        this.refreshListener = timedRefreshListener;
        this.context = context;
        timer = null;
        task = buildTimerTask();
    }

    private TimerTask buildTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (!refreshListener.isRefreshing()) {
                    refreshListener.setRefreshing(true);

                    try {
                        GetRemoteStatusTask task = new GetRemoteStatusTask(context, new Response.Listener<RemoteStatus>(){
                            public void onResponse(final RemoteStatus response) {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshListener.onRefresh(response);
                                        refreshListener.setRefreshing(false);
                                    }
                                });
                            }
                        }, new Response.ErrorListener(){
                            public void onErrorResponse(final VolleyError error) {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshListener.onRefreshError(error);
                                        refreshListener.setRefreshing(false);
                                    }
                                });
                            }
                        });

                        task.fetch(refreshListener.getRefreshSource());
                    } catch (Exception e) {
                        refreshListener.onRefreshError(e);
                        refreshListener.setRefreshing(false);
                    }
                }
            }
        };
    }

    public void callNow() {
        task.run();
    }

    public synchronized boolean isActive() {
        return timer != null;
    }

    public synchronized void schedule() {
        if(!isActive()) {
            timer = new Timer();
            task = buildTimerTask();
            timer.schedule(task, 0, ChobiConstants.SELFT_REFRESH_RATE);
        }
    }

    public synchronized void unschedule() {
        if(isActive()) {
            // Lo seteo en null porque no puedo reusar ni timers ni tareas
            timer.cancel();
            timer = null;
        }
    }
}
