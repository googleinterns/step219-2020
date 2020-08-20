public class TaskText{
  private String title;
  private String comment;

  public TaskText(String title) {
      this.title = title;
      this.comment = "";
  }
  public TaskText(String title, String cmment) {
      this.title = title;
      this.comment = comment;
  }
  public TaskText() {
      this("New Task");
  }
  public void setComment(String comment) {
      this.comment = comment;
  }
  public String getComment() {
      return comment;
  }
  public String getTitle() {
      return title;
  }
}