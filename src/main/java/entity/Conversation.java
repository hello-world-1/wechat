package entity;

public class Conversation {

    String friendId;
    String msgUserId;
    String friendHeadUrl;
    String conversationId;
    String friendName;
    LastMsg latestMsg;
    String unread;
    String timestamp;
    String timeStr;


//    friendId: item.msgUserId,
//    msgUserId: item.msgUserId,//消息的所有人id
//    friendHeadUrl: item.msgUserHeadUrl,//好友头像
//    conversationId: -1,//会话id，目前未用到
//    friendName: item.msgUserName,//好友昵称
//    latestMsg: item.content,//最新一条消息
//    unread: this._unreadObj[userId],//未读消息计数
//    timestamp: item.timestamp,//最新消息的时间戳
//    timeStr: item.timeStr//最新消息的时间

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }


    public String getMsgUserId() {
        return msgUserId;
    }

    public void setMsgUserId(String msgUserId) {
        this.msgUserId = msgUserId;
    }

    public String getFriendHeadUrl() {
        return friendHeadUrl;
    }

    public void setFriendHeadUrl(String friendHeadUrl) {
        this.friendHeadUrl = friendHeadUrl;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public LastMsg getLatestMsg() {
        return latestMsg;
    }

    public void setLatestMsg(LastMsg latestMsg) {
        this.latestMsg = latestMsg;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }




}
