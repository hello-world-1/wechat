
package websocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


/**
 * Socket建立连接（握手）和断开
 */
public class HandShake implements HandshakeInterceptor {
	private static final Logger logger = LoggerFactory
			.getLogger(HandShake.class);

	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
		String user_id = servletRequest.getParameter("userName");
		attributes.put("userName",user_id);
		return true;
	}

	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
	}

}
