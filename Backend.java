// --== CS400 File Header Information ==--
// Name: Leonardo Alfaro
// Email: butani@wisc.edu
// Team: KE Red
// Role: Backend Developer
// TA: Keren Chen
// Lecturer: Gary Dahl
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.zip.DataFormatException;

import java.util.List;

public class Backend implements BackendInterface {
  
  private CS400Graph<Airport> flightGraph;
  private ArrayList<Airport> airports;
  
  private ArrayList<List<Airport>> flights; // all flights booked by the user
  
  private ArrayList<Integer> distances; // distances of all booked flights, the index of each distance 
  //corresponds with the index of its flight in ArrayList<List<Airport>> flights
  
  /**
  * Constructor that takes in a reader.
  * 
  * @param reader - includes the file being read.
  */
  public Backend(Reader reader) {
    DummyFlightReader flightReader = new DummyFlightReader(); 
    try {
      this.flightGraph = flightReader.readDataSet(reader);
      this.airports = new ArrayList<Airport>();
      this.flights = new ArrayList<List<Airport>>();
      distances = new ArrayList<Integer>();
      for (Airport airport : this.flightGraph.vertices.keySet()) {
        this.airports.add(airport);
      }
      this.airports.sort(null);
    } catch (IOException | DataFormatException e) {
      e.printStackTrace();
    }
  }
  /**
  * Constructor that takes in a reader.
  * 
  * @param args 
  */
  public Backend(String[] args) {
    FileReader fr;
    try {
      fr = new FileReader(new File(args[0]));
      //
      //
      //
      DummyFlightReader flightReader = new DummyFlightReader(); //  Switch this to the real one
      //
      //
      //
      this.flightGraph = flightReader.readDataSet(fr);
      this.airports = new ArrayList<Airport>();
      this.flights = new ArrayList<List<Airport>>();
      distances = new ArrayList<Integer>();
      for (Airport airport : this.flightGraph.vertices.keySet()) {
        this.airports.add(airport);
      }
    } catch (IOException | DataFormatException e) {
      e.printStackTrace();
    }
  }
  /**
   * Books a singular flight based off the name of two locations.
   * 
   * @param start - airport name of the starting location.
   * @param end - airport name of the ending location.
   * @return true or false depending on if the flight could be booked.
   */
  @Override
  public boolean bookSingularFlight(String start, String end) {
    try {
      if(!this.getAirport(start).getAvailability() || !this.getAirport(end).getAvailability())
        return false;
      if(this.getAirport(start) == null || this.getAirport(end) == null)
        throw new NoSuchElementException();
      Airport starting = this.getAirport(start);
      Airport ending = this.getAirport(end);
      List<Airport> path = flightGraph.shortestPath(starting, ending);
      distances.add(flightGraph.getPathCost(starting, ending));
      flights.add(path);   
      return true;
    } catch(NoSuchElementException e) {
      return false;
    }
  }
  /**
   * Books a trip flight based off the name of a starting aiport, and a list of other aiports that must be visited.
   * 
   * @param start - airport name of the starting location.
   * @param multipleStops - list of airport names that must be visited.
   * @return true or false depending on if the flight could be booked.
   */
  @Override
  public boolean bookTripFlight(String start, ArrayList<String> multipleStops) { 
    try {
      if(!this.getAirport(start).getAvailability())
        return false;
      if(multipleStops.contains(start)) // you cannot go travel to the starting airport from the starting airport
        return false;
      Airport starting = getAirport(start);
      ArrayList<List<Airport>> miniFlights = new ArrayList<List<Airport>>();
      int totalDistance = 0;
      for(int i = 0; i < multipleStops.size(); i++ ) {
        if(!this.getAirport(multipleStops.get(i)).getAvailability())
          return false;
        miniFlights.add(flightGraph.shortestPath(starting, this.getAirport(multipleStops.get(i))));
        totalDistance += flightGraph.getPathCost(starting, this.getAirport(multipleStops.get(i)));
        starting = this.getAirport(multipleStops.get(i));
      }
      List<Airport> newTrip = new ArrayList<Airport>();
      for(int i = 0; i < miniFlights.size(); i++) {
        for(int j = 1; j < miniFlights.get(i).size(); j++) {
          if(j == 1 && i == 0)
            newTrip.add(miniFlights.get(i).get(0));
          newTrip.add(miniFlights.get(i).get(j));
        }
      }
      flights.add(newTrip);  
      distances.add(totalDistance);
      return true;
    } catch(NoSuchElementException e) {
      return false;
    }
  
  }
  /**
   * Returns the top three flights based off the flights booked by the user.
   * 
   * @return a string list of the top three shortest flights booked
   * by the user.
   */
  @Override
  public ArrayList<String> getMultipleFlights() {
    if(this.distances.size() == 0) {
      ArrayList<String> nothing = new ArrayList<String>();
      nothing.add("You Haven't Booked Any Flights Yet!");
      return nothing;
    }
    PriorityQueue<Integer> weights = new PriorityQueue<Integer>();
    for(int i = 0; i < distances.size(); i++) {
      weights.add(distances.get(i));
    }
    ArrayList<String> topFlights = new ArrayList<String>();
    int amount = 0;
    if(distances.size() >= 3) {
      amount = 3;
    } else {
      amount = distances.size();
    }
    int x = 0;
    while(x < amount) {
      for(int i = 0; i < distances.size(); i++) {
        if(weights.peek() == distances.get(i)) {
          String temp = "";
          for(int j = 0; j < flights.get(i).size(); j++) {
            if(j == 0) {
              temp += flights.get(i).get(j).getName() + " to ";
            } else {
              if(j == flights.get(i).size() - 1) {
                temp += flights.get(i).get(j).getName() + ": ";
              } else {
                temp += flights.get(i).get(j).getName() + " to ";              
              }              
            }
          }
          temp += distances.get(i) + " miles\n";
          topFlights.add(temp);
          break;
        }
      }
      weights.poll();
      x++;
    }
    return topFlights;
  }
  /**
   * Closes a random airport.
   * 
   */
  @Override
  public void closeRandomAirport() {
    Random temp = new Random();
    int index = temp.nextInt(airports.size());
    flightGraph.removeVertex(airports.get(index));
    airports.get(index).setAvailability(false);
  }
  /**
   * Returns all closed airports.
   * 
   * @return a string that displays all currently closed airports.
   */
  @Override
  public String getClosedAirports() {
    String closedAirports = "---"
        + "The Following Airports Are Currently Not Available Due To Severe Weather Conditions---\n";
    for(int i = 0; i < airports.size(); i++) {
      if(airports.get(i).getAvailability() == false) {
        closedAirports += airports.get(i).getName() + "\n";
      }
    }
    if(closedAirports.equals("---"
        + "The Following Airports Are Currently Not Available Due To Severe Weather Conditions---\n"))
      return "All Airports Are Available! Thank You Mother Nature!";   
    
    return closedAirports;
  }
  /**
   * returns an airport based off of the name given.
   * 
   * @param name - name of the airport that is being searched for.
   * @throws NoSuchElementException when no airport is found.
   * @return Airport - airport based off of the name given.
   */
  @Override
  public Airport getAirport(String name) throws NoSuchElementException {
    if(name == null) {
      throw new NoSuchElementException("An Airport With That Name Is Not Available!");
    }
    for(int i = 0; i < this.airports.size(); i++) {
      if(this.airports.get(i).getName().toUpperCase().equals(name.toUpperCase())) {
        return this.airports.get(i);
      }
    }
    throw new NoSuchElementException("An Airport With That Name Is Not Available!");
  }
  @Override
  public ArrayList<Airport> getAllAirports() {
    return this.airports;
  }
 /**
  * Returns a String that displays all the flights booked by the user.
  * 
  * @return String - contains all the flights booked by the user.
  */
  @Override
  public String getFlightItinerary() {
    if(this.flights.size() <= 0) {
      return "You Have Not Booked Any Flights Yet!";
    }     
    String intinerary = "~~~---Flights Booked So Far---~~~\n";
    for(int i = 0; i < flights.size(); i++) {
      if(i > 0)
        intinerary += "\n";
      for(int j = 0; j < flights.get(i).size(); j++) {
        if(j == flights.get(i).size() - 1) {
          intinerary += flights.get(i).get(j).getName();
        } else {
        intinerary += flights.get(i).get(j).getName() + " to ";    
        }
      }
      intinerary += ": " + distances.get(i) + " miles";
    }   
    return intinerary;
  }
}
