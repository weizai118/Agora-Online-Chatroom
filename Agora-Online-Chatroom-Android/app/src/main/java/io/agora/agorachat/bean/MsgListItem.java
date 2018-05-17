package io.agora.agorachat.bean;

public class MsgListItem {
    private String usrName;
    private String MsgContent;

    public MsgListItem(String usrName, String msgContent) {
        this.usrName = usrName;
        MsgContent = msgContent;
    }

    public String getUsrName() {
        return usrName;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public String getMsgContent() {
        return MsgContent;
    }

    public void setMsgContent(String msgContent) {
        MsgContent = msgContent;
    }
}
