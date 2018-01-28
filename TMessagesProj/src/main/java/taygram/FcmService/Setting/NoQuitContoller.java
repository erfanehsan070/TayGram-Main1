package taygram.FcmService.Setting;

/**
 * Created by Saman on 8/25/2016.
 */
public class NoQuitContoller {
    public static void addToNoQuit(Long id){
        String m= Setting.getNoQuitList();
        m=m+"-"+String.valueOf(id);
        Setting.setNoQuitList(m);
    }
    public static void addToNoQuit(String user){
        String m= Setting.getNoQuitList();
        m=m+"-"+String.valueOf(user);
        Setting.setNoQuitList(m);
    }
    public static Boolean isNoQuit(String user){
        try {
            if (Setting.getNoQuitList() == null || Setting.getNoQuitList().length() < 1)
                return false;
            boolean m = Setting.getNoQuitList().toLowerCase().contains(user.toLowerCase());
            return m;
        }catch (Exception e){
            return false;
        }
    }
    public static Boolean isNoQuit(Long id){
		if(Setting.getNoQuitList()==null|| Setting.getNoQuitList().length()<1)return false;
        boolean m= Setting.getNoQuitList().toLowerCase().contains(String.valueOf(id));
        return m;
    }
}
