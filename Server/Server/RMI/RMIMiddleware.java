package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.util.*;
import java.rmi.RemoteException;
import java.io.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class RMIMiddleware implements IResourceManager{


    private static IResourceManager carRm;
    private static IResourceManager flightRm;
    private static IResourceManager roomRm;
    private static IResourceManager customerRm;

    
	private static String s_serverName = "Middleware";
    private static String s_rmiPrefix = "group17_";
    //all rms run on same port
    private static int port= 1099;

    /*
        Method for connecting resource managers
    */
    private static boolean connectRm(IResourceManager rm,String rmName,String server){
        try{
            Registry reg = LocateRegistry.getRegistry(server, port);
            rm = (IResourceManager) reg.lookup(s_rmiPrefix + rmName);
            System.out.println("Middleware connected to " + rmName + " Resource Manager");
            return true;
        } catch (Exception e) {
            System.out.println("Err: Middleware failed to connect to " + rmName + " Manager");
            System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }
	public static void main(String args[])
	{


        //must have 4 args to bind to each server 
        if (args.length == 4) {

            String carServer = args[0];
            String hotelServer = args[1];
            String flightServer = args[2];
            String customerServer = args[3];
		
            // Create the RMI server entry following given rmi 
            try {
                // Create a new Server object
                RMIMiddleware middleware= new RMIMiddleware();

                // Dynamically generate the stub (client proxy)
                IResourceManager resourceManager = (IResourceManager) UnicastRemoteObject.exportObject(middleware, 0);

                // Bind the remote object's stub in the registry
                Registry l_registry;
                
                try {
                    l_registry = LocateRegistry.createRegistry(port);
                } catch (RemoteException e) {
                    l_registry = LocateRegistry.getRegistry(port);
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
                System.out.println("Middleware server '" + s_rmiPrefix + s_serverName + "'");

                //bind middleware to correct ressource managers
                connectRm(flightRm,"Flights",flightServer);
                connectRm(carRm,"Cars",carServer);
                connectRm(roomRm,"Hotels",hotelServer);
                connectRm(customerRm,"Customers",customerServer);

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
        return roomRm.addRooms(id, location, numRooms, price);
    }
			    
    /**
     * Add customer.
     *
     * @return Unique customer identifier
     */
    public int newCustomer(int id) 
	throws RemoteException{
        int nid = customerRm.newCustomer(id);
        carRm.newCustomer(id, nid );
        flightRm.newCustomer(id, nid );
        roomRm.newCustomer(id, nid );
        return nid;
    }
    
    /**
     * Add customer with id.
     *
     * @return Success
     */
    public boolean newCustomer(int id, int cid)
        throws RemoteException{
            return  carRm.newCustomer(id, cid) && 
            flightRm.newCustomer(id, cid) &&
            roomRm.newCustomer(id, cid) &&
            customerRm.newCustomer(id, cid);
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
        return  carRm.deleteCustomer(id, customerID) && 
        flightRm.deleteCustomer(id, customerID) && 
        roomRm.deleteCustomer(id, customerID) && 
        customerRm.deleteCustomer(id, customerID);
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
        return carRm.queryCars(id,location);
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
        return flightRm.queryFlightPrice(id, flightNumber);
    }

    /**
     * Query the status of a car location.
     *
     * @return Price of car
     */
    public int queryCarsPrice(int id, String location) 
	throws RemoteException{
        return carRm.queryCarsPrice(id, location);
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
        return flightRm.reserveFlight(id, customerID, flightNumber);
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

        boolean rF = true;

        for(int i =0; i <flightNumbers.size();i++){
            int flightNum = Integer.parseInt(flightNumbers.get(i));
            rF = rF && flightRm.reserveFlight(id, customerID, flightNum);
        }
        return carRm.reserveCar(id, customerID, location) && roomRm.reserveRoom(id, customerID, location) && rF;
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