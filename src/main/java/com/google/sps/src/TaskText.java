public class TaskText{
  private String title;
  private String comment;

  public TaskText(String title) {
      this.title = title;
      this.comment = "";
  }
<<<<<<< HEAD
  public TaskText(String title, String comment) {
=======

  public TaskText(String title, String cmment) {
>>>>>>> c0ab75a53ab4d3504cfe2e9e755de9b94afeeecb
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