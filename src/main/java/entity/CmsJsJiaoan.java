package entity;


public class CmsJsJiaoan {
	private static final long serialVersionUID = 1L;

	Integer id;
	String title;
	String username;
	String date;
	String content_sheji;
	String content_mubiao;
	String content_zhunbei;
	String content_guocheng;
	String content_fansi;
	String content_yanshen;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent_sheji() {
		return content_sheji;
	}

	public void setContent_sheji(String content_sheji) {
		this.content_sheji = content_sheji;
	}

	public String getContent_mubiao() {
		return content_mubiao;
	}

	public void setContent_mubiao(String content_mubiao) {
		this.content_mubiao = content_mubiao;
	}

	public String getContent_zhunbei() {
		return content_zhunbei;
	}

	public void setContent_zhunbei(String content_zhunbei) {
		this.content_zhunbei = content_zhunbei;
	}

	public String getContent_guocheng() {
		return content_guocheng;
	}

	public void setContent_guocheng(String content_guocheng) {
		this.content_guocheng = content_guocheng;
	}

	public String getContent_fansi() {
		return content_fansi;
	}

	public void setContent_fansi(String content_fansi) {
		this.content_fansi = content_fansi;
	}

	public String getContent_yanshen() {
		return content_yanshen;
	}

	public void setContent_yanshen(String content_yanshen) {
		this.content_yanshen = content_yanshen;
	}
	/* [CONSTRUCTOR MARKER END] */

}