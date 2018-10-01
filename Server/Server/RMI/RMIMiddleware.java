package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.util.*;
import java.rmi.RemoteException;
import java.io.*;

public class RMIMiddleware implements IResourceManager{


    private static ResourceManager carRm;
    private static ResourceManager flightRm;
    private static ResourceManager roomRm;

	private static String s_serverName = "Middleware";
	private static String s_rmiPrefix = "group17_";


	public static void main(String args[])
	{

        //follow poertrs from given rmi 
        int mainPort = 1099;
        int carPort = 1100;
        int hotelPort = 1101;
        int flightPort = 1102;

        //must have 4 args to bind to each server 
        if (args.length == 4) {

            String carServer = args[1];
            String hotelServer = args[2];
            String flightServer = args[3];

		
            // Create the RMI server entry following given rmi 
            try {
                // Create a new Server object
                RMIMiddleware middleware= new RMIMiddleware();

                // Dynamically generate the stub (client proxy)
                IResourceManager resourceManager = (IResourceManager) UnicastRemoteObject.exportObject(middleware, 0);

                // Bind the remote object's stub in the registry
                Registry l_registry;
                
                try {
                    l_registry = LocateRegistry.createRegistry(1099);
                } catch (RemoteException e) {
                    l_registry = LocateRegistry.getRegistry(1099);
                }
                final Registry registry = l_registry;
                registry.rebind(s_rmiPrefix + s_serverName, resourceManager);

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            registry.unbind(s_rmiPrefix + s_serverName);
                            System.out.println("'" + s_serverName + "' middleware unbound");
                        }
                        catch(Exception e) {
                            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
                            e.printStackTrace();
                        }
                    }
                });                                       
                System.out.println("'" + s_serverName + "' middleware server ready and bound to '" + s_rmiPrefix + s_serverName + "'");

                //bind middleware to correct ressource managers

                Registry flightReg = LocateRegistry.getRegistry(flightServer, flightPort);
                flightRm = (ResourceManager) flightregistry.lookup(s_rmiPrefix + s_serverName);
                if (flightRm != null) {
                    System.out.println("Middleware connected to flight manager");
                } else {
                    System.out.println("Err: Middleware failed to connect to flight manager");
                }
                
                Registry carReg = LocateRegistry.getRegistry(carServer, carPort);
                carRm = (ResourceManager) carregistry.lookup(s_rmiPrefix + s_serverName);
                if (carRm != null) {
                    System.out.println("Middleware connected to car manager");
                } else {
                    System.out.println("Err: Middleware failed to connect to car manager");
                }
    
                Registry roomReg = LocateRegistry.getRegistry(hotelServer, hotelPort);
                roomRm = (ResourceManager) hotelregistry.lookup(s_rmiPrefix + s_serverName);
                if (roomRm != null) {
                    System.out.println("Middleware connected to room manager");
                } else {
                    System.out.println("Err: Middleware failed to connect to room manager");
                }
    

            }
            catch (Exception e) {
                System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
                e.printStackTrace();
                System.exit(1);
            }

        }
    }


    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException{
        return flightRm.addFlight(id, flightNum, flightSeats, flightPrice);
    }
    
    /**
     * Add car at a location.
     *
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    public boolean addCars(int id, String location, int numCars, int price) 
	throws RemoteException{
        return carRm.addCars(id, location, numCars, price);
    } 
   
    /**
     * Add room at a location.
     *
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    public boolean addRooms(int id, String location, int numRooms, int price) 
	throws RemoteException{
        return roomRm.addRooms(id, location, count, price);
    }
			    
    /**
     * Add customer.
     *
     * @return Unique customer identifier
     */
    public int newCustomer(int id) 
	throws RemoteException{
        return customerRm.newCustomer(id);
    }
    
    /**
     * Add customer with id.
     *
     * @return Success
     */
    public boolean newCustomer(int id, int cid)
        throws RemoteException{
            return customerRm.newCustomer(id, customerID);
        }

    /**
     * Delete the flight.
     *
     * deleteFlight implies whole deletion of the flight. If there is a
     * reservation on the flight, then the flight cannot be deleted
     *
     * @return Success
     */   
    public boolean deleteFlight(int id, int flightNum) 
	throws RemoteException{
        return flightRm.deleteFlight(id, flightNum);
    }
    
    /**
     * Delete all cars at a location.
     *
     * It may not succeed if there are reservations for this location
     *
     * @return Success
     */		    
    public boolean deleteCars(int id, String location) 
	throws RemoteException{
        return carRm.deleteCars(id, location);
    }

    /**
     * Delete all rooms at a location.
     *
     * It may not succeed if there are reservations for this location.
     *
     * @return Success
     */
    public boolean deleteRooms(int id, String location) 
	throws RemoteException{
        return roomRm.deleteRooms(id, location);
    }
    
    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    public boolean deleteCustomer(int id, int customerID) 
	throws RemoteException{
        return customerRm.deleteCustomer(id, customerID);
    }

    /**
     * Query the status of a flight.
     *
     * @return Number of empty seats
     */
    public int queryFlight(int id, int flightNumber) 
	throws RemoteException{
        return flightRm.queryFlight(id, flightNumber);
    }

    /**
     * Query the status of a car location.
     *
     * @return Number of available cars at this location
     */
    public int queryCars(int id, String location) 
	throws RemoteException{
        return carsRm.queryCars(id,location);
    }

    /**
     * Query the status of a room location.
     *
     * @return Number of available rooms at this location
     */
    public int queryRooms(int id, String location) 
	throws RemoteException{
        return roomRm.queryRooms(id,location);
    }

    /**
     * Query the customer reservations.
     *
     * @return A formatted bill for the customer
     */
    public String queryCustomerInfo(int id, int customerID) 
	throws RemoteException{
        return customerRm.queryCustomerInfo(id, customerID);
    }
    
    /**
     * Query the status of a flight.
     *
     * @return Price of a seat in this flight
     */
    public int queryFlightPrice(int id, int flightNumber) 
	throws RemoteException{
        return flightRm.queryFlightPrice(id, flightNum);
    }

    /**
     * Query the status of a car location.
     *
     * @return Price of car
     */
    public int queryCarsPrice(int id, String location) 
	throws RemoteException{
        return carsRm.queryCarsPrice(id, location);
    }

    /**
     * Query the status of a room location.
     *
     * @return Price of a room
     */
    public int queryRoomsPrice(int id, String location) 
	throws RemoteException{
        return roomRm.queryRoomsPrice(id, location);
    }

    /**
     * Reserve a seat on this flight.
     *
     * @return Success
     */
    public boolean reserveFlight(int id, int customerID, int flightNumber) 
	throws RemoteException{
        return flightRm.reserveFlight(id, customerID, flightNum);
    }

    /**
     * Reserve a car at this location.
     *
     * @return Success
     */
    public boolean reserveCar(int id, int customerID, String location) 
	throws RemoteException{
        return carRm.reserveCar(id, customerID, location);
    }

    /**
     * Reserve a room at this location.
     *
     * @return Success
     */
    public boolean reserveRoom(int id, int customerID, String location) 
	throws RemoteException{
        return roomRm.reserveRoom(id, customerID, location);
    }

    /**
     * Reserve a bundle for the trip.
     *
     * @return Success
     */
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room)
	throws RemoteException{
        // what is this ???????
        return true;
    }

    /**
     * Convenience for probing the resource manager.
     *
     * @return Name
     */
    public String getName()
        throws RemoteException{
            return s_serverName;
        }










}