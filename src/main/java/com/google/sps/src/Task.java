
public class Task {
  private Time time;
  private TaskText taskText;
  private Place place;

  Task(Time time, TaskText taskText, Place place) {
    this.time = time;
    this.taskText = taskText;
    this.place = place;
  }

  Task(Time time, TaskText taskText)
  {
    this.time = time;
    this.taskText = taskText;
  }

  void setComment(String message) {
    taskText.setComment(message);
  }

  void setPlace(Place place) {
    this.place = place;
  }

  void setTime(Time time) {
    this.time = time;
  }

  TaskText getTaskText() {
    return taskText;
  }

  Time getTime() {
    return time;
  }

  Place getPlace() {
    return place;
  }
}