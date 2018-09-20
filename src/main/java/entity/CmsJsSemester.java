package entity;


public class CmsJsSemester {
	private static final long serialVersionUID = 1L;

	Integer id;
	String name;
	Integer siteid;
	String date;
	String datetime;
	Integer curr;


//			`curr` int(1) DEFAULT NULL,

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}




	public Integer getSiteid() {
		return siteid;
	}

	public void setSiteid(Integer siteid) {
		this.siteid = siteid;
	}


	/* [CONSTRUCTOR MARKER END] */


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public Integer getCurr() {
		return curr;
	}

	public void setCurr(Integer curr) {
		this.curr = curr;
	}


}