package project.taras.ua.bleutoothodroid.helperClasses;

/**
 * Created by Taras on 21.08.2017.
 */

public class MsgManager {

    public static byte[] make_msg(int msg_subject, String msg){

        StringBuilder sb = new StringBuilder();
        sb.append(msg_subject);
        sb.append("&");
        sb.append(msg);

        return sb.toString().getBytes();
    }

    public static String[] unpack_msg_from_odroid(String msg){

        return msg.split("&");
    }

}
