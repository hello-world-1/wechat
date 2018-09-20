package service.impl;

import dbmanager.DbMnager;
import entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import service.ImService;
import util.JBDate;

import java.util.List;


@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 5)
@Service("imservice")
public class ImServiceImpl implements ImService {


    private static final Logger logger = LoggerFactory
            .getLogger(ImServiceImpl.class);

    public String getpwdbyname(String name) {
        return null;
    }

    public Long getUidbyname(String name) {
        return null;
    }

    public String getnamebyid(long id) {
        logger.info("执行到这里了");
        String sql="select * from js_user_info";
       List<CmsJsUserinfo> list =  DbMnager.getBeanic(sql,CmsJsUserinfo.class,"jdbc");
       logger.info("list.sieze"+list.size());
         return null;
    }

    public List<CmsJsJiazhang> getJiazhang(String grade_id){
        String sql = "select d.id,d.mobile,d.uid,d.username,d.uname,jc_user_ext.user_img,d.name from (\n" +
                "select c.id,c.mobile,c.uname,c.username,jc_user.user_id as uid,c.name from (\n" +
                "\n" +
                "select a.id,a.username,b.username as uname,b.name,b.mobile from  (select * from js_user_info where grade_id = "+grade_id+" ) as a \n" +
                ",js_bangding as b where a.id = b.user_id\n" +
                "\n" +
                ") as c ,\n" +
                "jc_user \n" +
                "where c.uname = jc_user.username \n" +
                "\n" +
                ") as d ,jc_user_ext where d.uid = jc_user_ext.user_id  order by id";
        return DbMnager.getBeanic(sql,CmsJsJiazhang.class,"jdbc");
    }

    public CmsJsBangding getbangding(String username ){
        String sql = "select * from js_bangding where username = '"+username+"'";
        List<CmsJsBangding> list =  DbMnager.getBeanic(sql,CmsJsBangding.class,"jdbc");
        if(list.size()>0)
        {
            return list.get(0);
        }
        return null;
    }

    public List<CmsJsBangding> getbangdingList(String site_id){
        String sql = "select * from js_bangding where site_id = '"+site_id+"'";
        return DbMnager.getBeanic(sql,CmsJsJiazhang.class,"jdbc");
    }

    public List<CmsJsJiaoshi> getCmsJsJiaoshiList(String site_id){
        String sql = "select e.username,e.user_id,e.name, e.grade_id,f.user_img from (\n" +
                "select c.username,c.user_id,c.name, c.grade_id,d.user_id as uid  from (\n" +
                "select a.username,a.user_id,b.username as name, b.grade_id from (\n" +
                "select * from js_bangding where site_id = "+site_id+" and user_type = 0 )\n" +
                "as a ,js_teacher_info b where a.user_id = b.id)\n" +
                "as c ,jc_user as d where c.username = d.username)\n" +
                "as e ,jc_user_ext f where e.uid = f.user_id";
        return DbMnager.getBeanic(sql,CmsJsJiaoshi.class,"jdbc");
    }

    public void save_myMessage(Message message){
        String sql = "insert into js_message (msgType,friendId,userId,time,timestamp,content) value('"+message.getType()+"','"+message.getFriendId()+"','"+message.getUserId()+"','"+JBDate.gettime()+"','"+JBDate.getNowDateTimeMS()+"','"+message.getContent()+"');";
        DbMnager.Execute(sql,"jdbc");
    }

    public void save_friendMessage(Message message){
        String sql = "insert into js_message (msgType,friendId,userId,time,timestamp,content) value('"+message.getType()+"','"+message.getUserId()+"','"+message.getFriendId()+"','"+JBDate.gettime()+"','"+JBDate.getNowDateTimeMS()+"','"+message.getContent()+"');";
        DbMnager.Execute(sql,"jdbc");
    }


    public List<JsMessage> gethistoryMessage(Message message){
        String sql ="select * from ( select * from js_message where  (userid = '"+message.getUserId()+"' and friendId = '"+message.getFriendId()+"' ) or (userid = '"+message.getFriendId()+"' and friendId = '"+message.getUserId()+"' )  order by time desc LIMIT 0,30 ) as c order by c.time ";
        return DbMnager.getBeanic(sql,JsMessage.class,"jdbc");
    }









}
