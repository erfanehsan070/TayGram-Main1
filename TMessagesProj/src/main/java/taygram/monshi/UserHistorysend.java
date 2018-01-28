package taygram.monshi;



import org.telegram.messenger.exoplayer2.source.hls.HlsChunkSource;

import java.util.Date;
import java.util.HashMap;

public class UserHistorysend {
    private static HashMap<Long, Long> list;

    static {
        list = new HashMap();
    }

    public static boolean isok(Long userid) {
        if (list.size() == 0) {
            return true;
        }
        if (list.containsKey(userid)) {
            return false;
        }
        if (( list.get(userid)).longValue() >= new Date().getTime()) {
            return false;
        }
        list.remove(userid);
        return true;
    }

    public static void add(long userid) {
        list.remove(Long.valueOf(userid));
        list.put(Long.valueOf(userid), Long.valueOf(new Date().getTime() + HlsChunkSource.DEFAULT_MAX_BUFFER_TO_SWITCH_DOWN_MS));
    }
}
