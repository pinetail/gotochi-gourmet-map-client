package jp.pinetail.android.gotochi_gourmet_map.dto;


public class CommentsDto {

	public String NickName = null;
	public String VisitDate = null;
	public String ReviewDate = null;
	private String UseType = null;
	private String Situations = null;
	private String TotalScore = null;
	private String TasteScore = null;
	private String ServiceScore = null;
	private String MoodScore = null;
	private String DinnerPrice = null;
	private String LunchPrice = null;
	public String Title = null;
	public String Comment = null;
	public String PcSiteUrl = null;
	public String MobileSiteUrl = null;
	
    public void setData(String key, String value) {

        if (key.compareTo("NickName") == 0) {
            this.NickName = value;
        } else if (key.compareTo("VisitDate") == 0) {
            this.VisitDate = value;
        } else if (key.compareTo("ReviewDate") == 0) {
            this.ReviewDate = value;
        } else if (key.compareTo("UseType") == 0) {
            this.UseType = value;
        } else if (key.compareTo("Situations") == 0) {
            this.Situations = value;
        } else if (key.compareTo("TotalScore") == 0) {
            this.TotalScore = value;
        } else if (key.compareTo("TasteScore") == 0) {
            this.TasteScore = value;
        } else if (key.compareTo("ServiceScore") == 0) {
            this.ServiceScore = value;
        } else if (key.compareTo("MoodScore") == 0) {
            this.MoodScore = value;
        } else if (key.compareTo("DinnerPrice") == 0) {
            this.DinnerPrice = value;
        } else if (key.compareTo("LunchPrice") == 0) {
            this.LunchPrice = value;
        } else if (key.compareTo("Title") == 0) {
            this.Title = value;
        } else if (key.compareTo("Comment") == 0) {
            this.Comment = value;
        } else if (key.compareTo("PcSiteUrl") == 0) {
            this.PcSiteUrl = value;
        } else if (key.compareTo("MobileSiteUrl") == 0) {
            this.MobileSiteUrl = value;
        }
    }

}
