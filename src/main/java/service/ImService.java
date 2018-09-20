package service;


import entity.*;

import java.util.List;

public interface ImService {
	String getpwdbyname(String name);
	Long getUidbyname(String name);
	String getnamebyid(long id);
	List<CmsJsJiazhang> getJiazhang(String grade_id);
	CmsJsBangding getbangding(String username );
	List<CmsJsBangding> getbangdingList(String site_id);
	List<CmsJsJiaoshi> getCmsJsJiaoshiList(String site_id);
	void save_myMessage(Message message);
	void save_friendMessage(Message message);
	List<JsMessage> gethistoryMessage(Message message);
}
