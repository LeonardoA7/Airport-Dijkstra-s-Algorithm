public class Airport implements Comparable<Airport> {
  
  private boolean available;
  
  private String description;
  private String name;

  public Airport(String data, boolean availability) {
    this.name = data.toUpperCase();
    if(availability) {
      this.available = true;
      this.description = "OPEN";
    } else {
      this.available = false;
      this.description = "CLOSED";
    }
    this.description = this.name  + this.description;
  }
  
  public void setAvailability(boolean availability) {
    this.available = availability;
  }
  
  public boolean getAvailability() {
    return this.available;
  }
  
  public String getDescription() {
    return this.description;
  }

  @Override
  public int compareTo(Airport otherAirport) {
    return this.toString().compareTo(otherAirport.toString());
  }

  @Override
  public String toString() {
    return this.description;
  }
  public String getName() {
    return this.name;
  }
}

