package com.showworld.living.presenters;


import android.os.AsyncTask;

import com.showworld.living.model.LiveInfoJson;
import com.showworld.living.presenters.viewinface.LiveListView;

import java.util.ArrayList;

/**
 * 直播列表页Presenter
 */
public class LiveListViewHelper extends Presenter {
    private LiveListView mLiveListView;
    private GetLiveListTask mGetLiveListTask;
    private int mCurPageIndex = 0;

    public LiveListViewHelper(LiveListView view) {
        mLiveListView = view;
    }


    public void getPageData(int page) {
        mCurPageIndex = page;
        mGetLiveListTask = new GetLiveListTask();
        mGetLiveListTask.execute(page, 20);
    }

    @Override
    public void onDestory() {
    }

    /**
     * 获取后台数据接口
     */
    class GetLiveListTask extends AsyncTask<Integer, Integer, ArrayList<LiveInfoJson>> {

        @Override
        protected ArrayList<LiveInfoJson> doInBackground(Integer... params) {
            return OKhttpHelper.getInstance().getLiveList(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<LiveInfoJson> result) {
            mLiveListView.showPage(result, mCurPageIndex);
        }
    }

}
