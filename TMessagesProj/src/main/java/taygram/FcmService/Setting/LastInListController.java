package taygram.FcmService.Setting;

import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

/**
 * Created by Saman on 8/25/2016.
 */
public class LastInListController {
    public static void add(Long id){
        String m= Setting.getLastInLists();
        m=m+"-"+String.valueOf(id);
        Setting.setLastInLists(m);
    }
    public static void add(String user){
        String m= Setting.getLastInLists();
        m=m+"-"+String.valueOf(user);
        Setting.setLastInLists(m);
    }
    public static Boolean is(String user){
        try {
            if (Setting.getLastInLists() == null || Setting.getLastInLists().length() < 1)
                return false;
            boolean m = Setting.getLastInLists().toLowerCase().contains(user.toLowerCase());
            return m;
        }catch (Exception e){
            return false;
        }
    }
    public static Boolean is(TLRPC.TL_dialog dialog){

            if(DialogObject.isChannel(dialog)){
                int diid=(int)dialog.id;
                TLRPC.Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-diid));
                if(chat.username!=null&&is(chat.username)){
                    return true;
                }
            }
        return false;
    }
}
