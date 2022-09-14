import java.util.ArrayList;
import java.util.NoSuchElementException;

public interface BackendInterface {

  public boolean bookSingularFlight(String start, String end);
  // set a path from a starting airport to end airport

  public boolean bookTripFlight(String start, ArrayList<String> multipleStops);
  // set a path from a starting airport to one or more midway stops before reaching an end airport

  public ArrayList<String> getMultipleFlights();
  // returns up to three of the shortest flights, based on the flights the user has booked.

  public void closeRandomAirport();
  // closes an airport using Random “weather”.

  public String getClosedAirports();
  // gets closed airports due to Random “weather”.

  public Airport getAirport(String name) throws NoSuchElementException;
  
  public ArrayList<Airport> getAllAirports();
  // returns all airports, with descriptions on each

  public String getFlightItinerary();
  // returns the data sequence of given points

}