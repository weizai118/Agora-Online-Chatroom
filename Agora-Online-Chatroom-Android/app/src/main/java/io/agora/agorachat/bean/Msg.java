package io.agora.agorachat.bean;

/**
 * 消息实体类
 */
public class Msg {
    // role: 0:owner 1:broadcast 2:audience
    private int role;
    // type:
    // 0:加入频道
    // 1:离开频道
    // 2:发送频道消息
    // 3:申请连麦
    // 4:连麦反馈, msg "1" 为同意, "0" 为不同意
    // 5:房主告诉别人他换背景图片了
    private int type;
    // 用户名
    private String account;
    // 消息
    private String msg;

    public Msg() {
    }

    public Msg(int role, int type, String account, String msg) {
        this.role = role;
        this.type = type;
        this.account = account;
        this.msg = msg;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
