public class TaskText{
  private String title;
  private String comment;

  public TaskText(String title) {
      this.title = title;
      this.comment = "";
  }
  public TaskText(String title, String comment) {
      this.title = title;
      this.comment = comment;
  }
  public Place() {
      this("New Task");
  }
  public String getComment() {
      return comment;
  }
  public String getTitle() {
      return title;
  }
}