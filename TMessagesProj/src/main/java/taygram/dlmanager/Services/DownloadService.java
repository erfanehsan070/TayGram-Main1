package taygram.dlmanager.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.User;

public class DownloadService extends Service implements MediaController.FileDownloadProgressListener {
    private ArrayList<MessageObject> messageObjects = new ArrayList();

    public void onCreate() {
        super.onCreate();
        this.messageObjects.addAll(DM_LoadMessages());
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        startDownloading(this.messageObjects);
        return START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        cancelDownloading();
    }

    public void onFailedDownload(String fileName) {
    }

    public void onSuccessDownload(String fileName) {
        startDownloading(this.messageObjects);
    }

    public void onProgressDownload(String fileName, float progress) {
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return 0;
    }

    public TLObject getDownloadObject(MessageObject messageObject) {
        TLRPC.MessageMedia media = messageObject.messageOwner.media;
        if (media != null) {
            if (media.document != null) {
                return media.document;
            }
            if (media.webpage != null && media.webpage.document != null) {
                return media.webpage.document;
            }
            if (media.webpage != null && media.webpage.photo != null) {
                return FileLoader.getClosestPhotoSizeWithSize(media.webpage.photo.sizes, AndroidUtilities.getPhotoSize());
            }
            if (media.photo != null) {
                return FileLoader.getClosestPhotoSizeWithSize(media.photo.sizes, AndroidUtilities.getPhotoSize());
            }
        }
        return new TLRPC.TL_messageMediaEmpty();
    }

    private void loadFile(TLObject attach) {
        if (attach instanceof TLRPC.PhotoSize) {
            FileLoader.getInstance().loadFile((TLRPC.PhotoSize) attach, null, false);
        } else if (attach instanceof TLRPC.Document) {
            FileLoader.getInstance().loadFile((TLRPC.Document) attach, true, false);
        }
    }

    private void startDownloading(ArrayList<MessageObject> messageObjects) {
        Iterator it = messageObjects.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            TLObject attach = getDownloadObject(messageObject);
            loadFile(attach);
            File pathToMessage = FileLoader.getPathToMessage(messageObject.messageOwner);
            if (pathToMessage != null && !pathToMessage.exists()) {
                MediaController.getInstance().addLoadingFileObserver(FileLoader.getAttachFileName(attach), this);
                return;
            }
        }
    }

    private void cancelDownloading() {
        for (int i = 0; i < this.messageObjects.size(); i++) {
            MessageObject messageObject = (MessageObject) this.messageObjects.get(i);
            if (messageObject != null) {
                TLObject attach = getDownloadObject(messageObject);
                if (attach instanceof TLRPC.PhotoSize) {
                    FileLoader.getInstance().cancelLoadFile((TLRPC.PhotoSize) attach);
                } else if (attach instanceof TLRPC.Document) {
                    FileLoader.getInstance().cancelLoadFile((TLRPC.Document) attach);
                }
            }
        }
    }

    public ArrayList<MessageObject> DM_LoadMessages() {
        final ArrayList<MessageObject> objects = new ArrayList();
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                HashMap<Integer, User> usersDict;
                HashMap<Integer, TLRPC.Chat> chatsDict;
                int a;
                TLRPC.TL_messages_messages res = new TLRPC.TL_messages_messages();
                SQLiteCursor cursor = null;
                try {
                    cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT * FROM turbo_idm ORDER BY date DESC"));
                    while (cursor.next()) {
                        NativeByteBuffer data = cursor.byteBufferValue(3);
                        if (data != null) {
                            TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                            message.id = cursor.intValue(0);
                            message.dialog_id = (long) cursor.intValue(1);
                            message.date = cursor.intValue(2);
                            res.messages.add(message);
                        }
                    }
                    if (cursor != null) {
                        cursor.dispose();
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                    usersDict = new HashMap();
                    chatsDict = new HashMap();
                    for (a = 0; a < res.users.size(); a++) {
                        User u = (User) res.users.get(a);
                        usersDict.put(Integer.valueOf(u.id), u);
                    }
                    for (a = 0; a < res.chats.size(); a++) {
                        TLRPC.Chat c = (TLRPC.Chat) res.chats.get(a);
                        chatsDict.put(Integer.valueOf(c.id), c);
                    }
                    for (a = 0; a < res.messages.size(); a++) {
                        objects.add(new MessageObject((TLRPC.Message) res.messages.get(a), usersDict, chatsDict, true));
                    }
                } finally {
                    if (cursor != null) {
                        cursor.dispose();
                    }
                }
                usersDict = new HashMap();
                chatsDict = new HashMap();
                for (a = 0; a < res.users.size(); a++) {
                    User u2 = (User) res.users.get(a);
                    usersDict.put(Integer.valueOf(u2.id), u2);
                }
                for (a = 0; a < res.chats.size(); a++) {
                    TLRPC.Chat c2 = (TLRPC.Chat) res.chats.get(a);
                    chatsDict.put(Integer.valueOf(c2.id), c2);
                }
                for (a = 0; a < res.messages.size(); a++) {
                    objects.add(new MessageObject((TLRPC.Message) res.messages.get(a), usersDict, chatsDict, true));
                }
            }
        });
        return objects;
    }
}
