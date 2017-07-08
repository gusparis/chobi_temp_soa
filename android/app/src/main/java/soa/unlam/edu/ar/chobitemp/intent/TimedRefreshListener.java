package soa.unlam.edu.ar.chobitemp.intent;

import soa.unlam.edu.ar.chobitemp.temp.CurrentLocalStatus;

/**
 * Created by mcurrao on 06/07/17.
 */

public interface TimedRefreshListener<T> {

    boolean isRefreshing();

    void setRefreshing(boolean refreshing);

    void onRefresh(T response);

    void onRefreshError(Exception error);

    CurrentLocalStatus getRefreshSource();
}
