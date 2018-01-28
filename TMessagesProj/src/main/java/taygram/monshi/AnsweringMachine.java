package taygram.monshi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.view.PointerIconCompat;

import ir.taygram.messenger.R;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC.User;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AnsweringMachine {
    public static ArrayList<MessageObject> ListOfMsgs;
    static Lock lock = new ReentrantLock();

    /* renamed from: org.telegram.hoshyar.Helper.AnsweringMachine.1 */
    static class C08511 implements Runnable {
        final /* synthetic */ MessageObject val$m;

        C08511(MessageObject messageObject) {
            this.val$m = messageObject;
        }

        public void run() {
            AnsweringMachine.ProcessMsg(this.val$m);
        }
    }

    static {
        ListOfMsgs = new ArrayList();
    }

    public static boolean ProcessMsg(MessageObject messageObject) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        String str = preferences.getString("seretory_ch", ApplicationLoader.applicationContext.getString(R.string.txtanswering));
        boolean answer = preferences.getBoolean("getAnsweringMachine", false);
        if (answer) {
            if (str.length() > 0) {
                long userid = messageObject.getDialogId();
                User user = MessagesController.getInstance().getUser(Integer.valueOf((int) userid));
                if (!(userid <= 0 || user == null || user.bot)) {
                    String msg = str;

                    lock.lock();
                    try  {
                        if (UserHistorysend.isok(Long.valueOf(userid))) {

                            SendText(msg, userid, messageObject);
                            UserHistorysend.add(userid);
                        }
                        lock.unlock();
                    } catch (Exception e) {
                        lock.unlock();
                    }
                }
            }
        }
        return false;
    }

    public static void SendText(String msg, long userid, MessageObject msgforreplay) {
        SendMessagesHelper.getInstance().sendMessage(msg, userid, null, null, true, null, null, null);
        MessagesController.getInstance().markMessageContentAsRead(msgforreplay);
    }

    public static void ProcessMsgs(ArrayList<MessageObject> listofdialogs) {
        for (int i = 0; i < listofdialogs.size(); i++) {
            new Handler().postDelayed(new C08511(listofdialogs.get(i)), (long) (i * PointerIconCompat.TYPE_DEFAULT));
        }
    }
}
