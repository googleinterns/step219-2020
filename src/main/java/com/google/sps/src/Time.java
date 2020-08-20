import java.util.Calendar; 

public class Time {
  private Calendar calendar;

  public Time() {
    calendar = Calendar.getInstance());
  }

  public Time(int year, int month, int date, int hour, int minute) {
    calendar = Calendar.getInstance());
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DATE, date);
    calendar.set(Calendar.HOUR, hour);
    calendar.set(Calendar.MINUTE, minute);
  }

  public DATE getTime() {
    return calendar.getTime()
  }

  public String getTimeAsString() {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
      String strDate = dateFormat.format(calendar.getTime());  
    return strDate
  }
}