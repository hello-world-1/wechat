package entity;

public class CmsJsGrade {
	private static final long serialVersionUID = 1L;

	Integer id;
	String name;
	String siteid;
	Integer semeter_id;
	Integer sort;
	String sp_url;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public Integer getSemeter_id() {
		return semeter_id;
	}

	public void setSemeter_id(Integer semeter_id) {
		this.semeter_id = semeter_id;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getSp_url() {
		return sp_url;
	}

	public void setSp_url(String sp_url) {
		this.sp_url = sp_url;
	}
}