package websocket;

import com.google.gson.Gson;
import entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import service.ImService;
import util.ResponseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Socket处理器
 */
@Component
public class MyWebSocketHandler implements WebSocketHandler {


    @Autowired
    ImService loginservice;

    private static final Logger logger = LoggerFactory
            .getLogger(MyWebSocketHandler.class);
    //用于保存HttpSession与WebSocketSession的映射关系
    public static final Map<String, WebSocketSession> userSocketSessionMap;


    static {
        userSocketSessionMap = new ConcurrentHashMap<String, WebSocketSession>();
    }

    /**
     * 建立连接后,把登录用户的id写入WebSocketSession
     */
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        String userName = session.getAttributes().get("userName").toString();
        logger.info("userName:" + userName);
        userSocketSessionMap.put(userName, session);
        //给某人发送上线消息
        Message msg = new Message();
        msg.setType("login");
        msg.setUserId(userName);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userName);
        userInfo.setNickName("柳如月");
        userInfo.setMyHeadUrl("http://downza.img.zz314.com/edu/pc/wlgj-1008/2016-06-23/64ec0888b15773e3ba5b5f744b9df16c.jpg");
        msg.setUserInfo(userInfo);
        sendMessageToUser(msg.getUserId(), ResponseUtils.renderApiJson(msg));
    }

    /**
     * 消息处理，在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理
     */
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message.getPayloadLength() == 0)
            return;
        logger.info("进行消息处理:" + message.getPayload().toString());
        Message msg = new Gson().fromJson(message.getPayload().toString(), Message.class);
        logger.info("msg_type:[" + msg.getType() + "]");

        if (msg.getType().equals("get-conversations")) {  //获取教师列表0针对教师
            //查询自己的用户状态
                //返回对应家长的列表
                List<Conversation> conversationList = new ArrayList<Conversation>();
//                List<CmsJsJiazhang>  cmsJsJiazhangList =   loginservice.getJiazhang(msg.getGrade_id());
                //获取到家长列表
                LastMsg lastMsg = new LastMsg();
                lastMsg.setType("text");
                lastMsg.setContent("周末有时间一起参加社团活动吗？");
//                for(CmsJsJiazhang jsJiazhang :cmsJsJiazhangList){
//                    Conversation conversation = new Conversation();
//                    conversation.setMsgUserId(msg.getUserId());
//                    conversation.setFriendName(jsJiazhang.getUsername()+"["+jsJiazhang.getName()+"]");
//                    conversation.setFriendId(jsJiazhang.getUname());
////                    conversation.setFriendHeadUrl("http://tx.haiqq.com/uploads/allimg/170504/0641415410-1.jpg");
//                    conversation.setFriendHeadUrl("https://yry2.xa00.cc"+jsJiazhang.getUser_img());
//                    conversation.setConversationId("-1");
//                    conversation.setLatestMsg(lastMsg);
//                    conversation.setUnread("0");
//                    conversation.setTimestamp("1533294362000");
//                    conversation.setTimeStr("19:06");
//                    conversationList.add(conversation);
//                }
                Conversation conversation1 = new Conversation();
                conversation1.setMsgUserId(msg.getUserId());
                conversation1.setFriendName("李恺新");
                conversation1.setFriendId("user_002");
                conversation1.setFriendHeadUrl("http://tx.haiqq.com/uploads/allimg/170504/0641415410-1.jpg");
                conversation1.setConversationId("-1");
                conversation1.setLatestMsg(lastMsg);
                conversation1.setUnread("1");
                conversation1.setTimestamp("1533294362000");
                conversation1.setTimeStr("19:06");
                conversationList.add(conversation1);
                msg.setConversations(conversationList);
                sendMessageToUser(msg.getUserId(), ResponseUtils.renderApiJson(msg));
        }else if(msg.getType().equals("get-conversations-js")) {
            //获取学校中所有教师的信息
            LastMsg lastMsg = new LastMsg();
            lastMsg.setType("text");
            lastMsg.setContent("周末有时间一起参加社团活动吗？");
            List<Conversation> conversationList = new ArrayList<Conversation>();
            CmsJsBangding cmsJsBangding = loginservice.getbangding(msg.getUserId());
            List<CmsJsJiaoshi> list = loginservice.getCmsJsJiaoshiList(cmsJsBangding.getSite_id());
           for(CmsJsJiaoshi jsJiaoshi:list)
           {
               Conversation conversation = new Conversation();
               conversation.setMsgUserId(msg.getUserId());
               conversation.setFriendName(jsJiaoshi.getName());
               conversation.setFriendId(jsJiaoshi.getUsername());
               conversation.setFriendHeadUrl("https://yry2.xa00.cc"+jsJiaoshi.getUser_img());
               conversation.setConversationId("-1");
               conversation.setLatestMsg(lastMsg);
               conversation.setUnread("0");
               conversation.setTimestamp("1533294362000");
               conversation.setTimeStr("19:06");
               conversationList.add(conversation);
           }
//            Conversation conversation1 = new Conversation();
//            conversation1.setMsgUserId(msg.getUserId());
//            conversation1.setFriendName("李恺新");
//            conversation1.setFriendId("user_002");
//            conversation1.setFriendHeadUrl("http://tx.haiqq.com/uploads/allimg/170504/0641415410-1.jpg");
//            conversation1.setConversationId("-1");
//            conversation1.setLatestMsg(lastMsg);
//            conversation1.setUnread("1");
//            conversation1.setTimestamp("1533294362000");
//            conversation1.setTimeStr("19:06");
//            conversationList.add(conversation1);
            msg.setConversations(conversationList);
            sendMessageToUser(msg.getUserId(), ResponseUtils.renderApiJson(msg));
        } else if (msg.getType().equals("get-history")) {//进入聊天框
//            //获取聊天的历史消息，多条
            List<JsMessage> jsMessageList = loginservice.gethistoryMessage(msg);
            for(JsMessage jsMessage :jsMessageList){
                Message chatMessage = new Message();
                chatMessage.setContent(jsMessage.getContent());
                chatMessage.setType(jsMessage.getMsgType());
                chatMessage.setConversationId(0);
                chatMessage.setUserId(jsMessage.getUserId());
                chatMessage.setFriendId(jsMessage.getFriendId());
                sendMessageToUser(msg.getUserId(), ResponseUtils.renderApiJson(chatMessage));
            }
        } else if (msg.getType().equals("get-friends")) {
            List<Friend> friends = new ArrayList<Friend>();
            Friend friend = new Friend();
            friend.setUserId("user_001");
            friend.setNickName("柳如月");
            friend.setMyHeadUrl("http://downza.img.zz314.com/edu/pc/wlgj-1008/2016-06-23/64ec0888b15773e3ba5b5f744b9df16c.jpg");
            Friend friend1 = new Friend();
            friend1.setUserId("user_002");
            friend1.setNickName("李恺新");
            friend1.setMyHeadUrl("http://tx.haiqq.com/uploads/allimg/170504/0641415410-1.jpg");
            friends.add(friend);
            friends.add(friend1);
            msg.setFriends(friends);
            sendMessageToUser(msg.getUserId(), ResponseUtils.renderApiJson(msg));
        } else {
            //存入数据中，是否已经
            loginservice.save_myMessage(msg);
            //模拟给自己发消息
            Message chatMessage = new Message();
            chatMessage.setContent(msg.getContent());
            chatMessage.setType(msg.getType());
            chatMessage.setConversationId(0);
            chatMessage.setUserId(msg.getFriendId());
            chatMessage.setFriendId(msg.getUserId());

            //目前这里默认发送给自己，正式情况下要发送给对方
            sendMessageToUser(msg.getUserId(), ResponseUtils.renderApiJson(chatMessage));

            loginservice.save_friendMessage(msg);
        }
    }

    /**
     * 消息传输错误处理
     */
    public void handleTransportError(WebSocketSession session,
                                     Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        Iterator<Entry<String, WebSocketSession>> it = userSocketSessionMap.entrySet().iterator();
        // 移除当前抛出异常用户的Socket会话
        while (it.hasNext()) {
            Entry<String, WebSocketSession> entry = it.next();
            if (entry.getValue().getId().equals(session.getId())) {
                userSocketSessionMap.remove(entry.getKey());
                logger.info("Socket会话已经移除:用户ID" + entry.getKey());
//				String username="";
//				Message msg = new Message();
////				msg.setFrom(-2L);
////				msg.setText(username);
//				this.broadcast(new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
//				break;
            }
        }
    }

    /**
     * 关闭连接后
     */
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        Iterator<Entry<String, WebSocketSession>> it = userSocketSessionMap.entrySet().iterator();
        // 移除当前用户的Socket会话
        while (it.hasNext()) {
            Entry<String, WebSocketSession> entry = it.next();
            if (entry.getValue().getId().equals(session.getId())) {
                userSocketSessionMap.remove(entry.getKey());
                logger.info("Socket会话已经移除:用户ID" + entry.getKey());
//				String username="";
//				Message msg = new Message();
//				msg.setFrom(-2L);//下线消息，用-2表示
//				msg.setText(username);
//				this.broadcast(new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
                break;
            }
        }
    }

    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     * @throws IOException
     */
    public void broadcast(final TextMessage message) throws IOException {

        logger.info("广播群消息");
        Iterator<Entry<String, WebSocketSession>> it = userSocketSessionMap.entrySet().iterator();
        //多线程群发
        while (it.hasNext()) {
            final Entry<String, WebSocketSession> entry = it.next();
            if (entry.getValue().isOpen()) {
                // entry.getValue().sendMessage(message);
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            if (entry.getValue().isOpen()) {
                                logger.info("开始发送消息，消息题" + message);
                                entry.getValue().sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }

        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessageToUser(String uid, TextMessage message) throws IOException {
        WebSocketSession session = userSocketSessionMap.get(uid);
        if (session != null && session.isOpen()) {
            session.sendMessage(message);
        }else{
            //进行通知老师，说有新消息来了
        }

    }

}
