package com.showworld.living.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.showworld.living.R;
import com.showworld.living.model.MemberInfo;
import com.showworld.living.presenters.viewinface.LiveView;
import com.showworld.living.utils.SWLLog;
import com.showworld.living.views.customviews.MembersDialog;

import java.util.ArrayList;


/**
 * 成员列表适配器
 */
public class MembersAdapter extends ArrayAdapter<MemberInfo> {
    private static final String TAG = MembersAdapter.class.getSimpleName();
    private LiveView mLiveView;
    private MembersDialog membersDialog;

    public MembersAdapter(Context context, int resource, ArrayList<MemberInfo> objects, LiveView liveView, MembersDialog dialog) {
        super(context, resource, objects);
        mLiveView = liveView;
        membersDialog = dialog;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.members_item_layout, null);
            holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.item_name);
            holder.videoCtrl = (TextView) convertView.findViewById(R.id.video_chat_ctl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MemberInfo data = getItem(position);
        final String selectId = data.getUserId();
        holder.id.setText(selectId);
        if (data.isOnVideoChat() == true) {
            holder.videoCtrl.setBackgroundResource(R.drawable.btn_video_disconnect);

        } else {
            holder.videoCtrl.setBackgroundResource(R.drawable.btn_video_connection);

        }
        holder.videoCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SWLLog.i(TAG, "select item:  " + selectId);

                if (data.isOnVideoChat() == false) {//不在房间中，发起邀请
                    if (mLiveView.showInviteView(selectId)) {
//                        data.setIsOnVideoChat(true);
                        view.setBackgroundResource(R.drawable.btn_video_disconnect);

                    }
                } else {
                    mLiveView.cancelInviteView(selectId);
//                    data.setIsOnVideoChat(false);
                    view.setBackgroundResource(R.drawable.btn_video_connection);
                    mLiveView.cancelMemberView(selectId);
                }
                membersDialog.dismiss();

            }
        });


        return convertView;
    }

    public final class ViewHolder {
        public TextView id;
        public TextView videoCtrl;
    }

}
