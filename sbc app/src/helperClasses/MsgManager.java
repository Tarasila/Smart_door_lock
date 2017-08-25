package helperClasses;

public class MsgManager {

    public static byte[] make_msg(int msg_subject, String msg){

        StringBuilder sb = new StringBuilder();
        sb.append(msg_subject);
        sb.append("&");
        sb.append(msg);

        return sb.toString().getBytes();
    }
    
    public static byte[] make_response_msg(int msg_subject, int status){

        StringBuilder sb = new StringBuilder();
        sb.append(msg_subject);
        sb.append("&");
        sb.append(status);

        return sb.toString().getBytes();
    }

    public static String[] unpack_msg_from_client(String msg){

        return msg.split("&");
    }

}

