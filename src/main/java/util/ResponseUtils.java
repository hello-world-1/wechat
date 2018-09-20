package util;

import com.google.gson.GsonBuilder;
import entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.security.acl.LastOwnerException;


/**
 * HttpServletResponse帮助类
 */
public final class ResponseUtils {
	public static final Logger log = LoggerFactory
			.getLogger(ResponseUtils.class);

	public static TextMessage renderApiJson(Message msg) {
		log.info("回复消息:"+new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg));
		return  new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg));
	}



}
