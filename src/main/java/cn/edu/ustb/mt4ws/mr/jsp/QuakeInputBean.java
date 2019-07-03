package cn.edu.ustb.mt4ws.mr.jsp;

/**
 * 地震查询各个变量的输入
 * 
 * @author qing.wen
 * 
 */
@SuppressWarnings("serial")
public class QuakeInputBean implements java.io.Serializable {

	// 蜕变关系ID
	private int mrID;
	// 输入maxd
	private String maxd;
	// mind
	private String mind;
	// maxlng
	private String maxlng;
	// minlng
	private String minlng;
	// maxlat
	private String maxlat;
	// minlat
	private String minlat;
	//maxmag
	private String maxmag;
	//minmag
	private String minmag;
	//maxdepth
	private String maxdepth;
	//mindepth
	private String mindepth;
	
	
	
	
	
/**
 * 构造函数
 * @param mrID
 * @param maxd
 * @param mind
 * @param maxlng
 * @param minlng
 * @param maxlat
 * @param minlat
 * @param maxmag
 * @param minmag
 * @param maxdepth
 * @param mindepth
 */

	public QuakeInputBean(int mrID, String maxd, String mind, String maxlng,
			String minlng, String maxlat, String minlat, String maxmag,
			String minmag, String maxdepth, String mindepth) {
		setMrID(mrID);
		setMaxd(maxd);
		setMind(mind);
		setMaxlng(maxlng);
		setMinlng(minlng);
		setMaxlat(maxlat);
		setMinlat(minlat);
		setMaxmag(maxmag);
		setMinmag(minmag);
		setMaxdepth(maxdepth);
		setMindepth(mindepth);
	}

	/*
	 * set和get方法
	 */
	

	public void setMindepth(String mindepth) {
		this.mindepth = mindepth;
	}

	public String getMindepth() {
		return mindepth;
	}

	public void setMaxdepth(String maxdepth) {
		this.maxdepth = maxdepth;
	}

	public String getMaxdepth() {
		return maxdepth;
	}

	public void setMinmag(String minmag) {
		this.minmag = minmag;
	}

	public String getMinmag() {
		return minmag;
	}

	public void setMaxmag(String maxmag) {
		this.maxmag = maxmag;
	}

	public String getMaxmag() {
		return maxmag;
	}

	


	public int getMrID() {
		return mrID;
	}

	public void setMrID(int mrID) {
		this.mrID = mrID;
	}

	public String getMaxd() {
		return maxd;
	}

	public void setMaxd(String maxd) {
		this.maxd = maxd;
	}

	public String getMind() {
		return mind;
	}

	public void setMind(String mind) {
		this.mind = mind;
	}

	public String getMaxlng() {
		return maxlng;
	}

	public void setMaxlng(String maxlng) {
		this.maxlng = maxlng;
	}

	public String getMinlng() {
		return minlng;
	}

	public void setMinlng(String minlng) {
		this.minlng = minlng;
	}

	public String getMaxlat() {
		return maxlat;
	}

	public void setMaxlat(String maxlat) {
		this.maxlat = maxlat;
	}

	public String getMinlat() {
		return minlat;
	}

	public void setMinlat(String minlat) {
		this.minlat = minlat;
	}
}
