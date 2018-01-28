package taygram.messageTimer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;

import java.util.ArrayList;

public class timerSendMessage extends BroadcastReceiver implements NotificationCenter.NotificationCenterDelegate {

    int msg_id = 0;
    ArrayList<Integer> dialogs = new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {

        msg_id = intent.getIntExtra("msg_id",0);
        dialogs = intent.getIntegerArrayListExtra("dialogs_ids");

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDidLoaded);
        MessagesController.getInstance().loadMessages(UserConfig.getClientUserId(), 200, 0,0, false, 1, 0, 0, 0, false, 0);


    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.messagesDidLoaded) {
            ArrayList<MessageObject> messArr = (ArrayList<MessageObject>) args[2];
            if (messArr != null)
                for (int i = 0; i < messArr.size(); i++) {
                    if (messArr.get(i).getId() == msg_id){
                        forwarder(messArr.get(i),dialogs);
                        break;
                    }

                }
        }

    }


    private void forwarder(MessageObject msg, ArrayList<Integer> dialogsId) {

        for (int i = 0; i < dialogsId.size(); i++) {
            SendMessagesHelper.getInstance().processForwardFromMyName(msg, dialogsId.get(i));
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDidLoaded);
    }
}
