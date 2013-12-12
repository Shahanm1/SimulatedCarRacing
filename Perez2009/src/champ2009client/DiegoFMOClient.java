/**
 * 
 */
package champ2009client;

import champ2009client.evolution.EvolutionConstants;
import java.util.StringTokenizer;

/**
 * @author Diego Perez Liebana
 * 
 */
public class DiegoFMOClient {

	private static int UDP_TIMEOUT = 1000;
	private static int port;
	private static String host;
	private static String clientId;
	private static boolean verbose;
	private static int maxEpisodes;
	private static int maxSteps;
        private static int numCars; //pop size for GA

        private DiegoController _car;
        private DriverManager   _driver;
        
        private static int  _executionMode;

        public static int getNumCars() {
            return numCars;
        }
        
	private static void parseParameters(String[] args) {
		/*
		 * Set default values for the options
		 */
		port = 3001;
		host = "localhost";
		clientId = "championship2009";
		verbose = false;
		maxEpisodes = 1;
		maxSteps = 0;
                numCars = 0;
                _executionMode = ClientConstants.EXE_GO;

		for (int i = 1; i < args.length; i++) {
                        StringTokenizer st = new StringTokenizer(args[i], ":");
			String entity = st.nextToken();
			String value = st.nextToken();
			if (entity.equals("port")) {
				port = Integer.parseInt(value);
			}
			if (entity.equals("host")) {
				host = value;
			}
			if (entity.equals("id")) {
				clientId = value;
			}
			if (entity.equals("verbose")) {
				if (value.equals("on"))
					verbose = true;
				else if (value.equals(false))
					verbose = false;
				else {
					System.out.println(entity + ":" + value
							+ " is not a valid option");
					System.exit(0);
				}
			}
			if (entity.equals("id")) {
				clientId = value;

			}
			if (entity.equals("maxEpisodes")) {
				maxEpisodes = Integer.parseInt(value);
				if (maxEpisodes <= 0) {
					System.out.println(entity + ":" + value
							+ " is not a valid option");
					System.exit(0);
				}

			}
			if (entity.equals("maxSteps")) {
				maxSteps = Integer.parseInt(value);
				if (maxSteps < 0) {
					System.out.println(entity + ":" + value
							+ " is not a valid option");
					System.exit(0);
				}

			}

                        if (entity.equals("ga")) {
                            _executionMode = ClientConstants.EXE_GA;
                        }

                        if (entity.equals("createBase")) {
                            _executionMode = ClientConstants.EXE_WRITE_BASE_FILE;
                        }             

                        if (entity.equals("createGrid")) {
                            _executionMode = ClientConstants.EXE_WRITE_GRID_FILES;
                        }       
                        
                        if (entity.equals("run")) {
                            _executionMode = ClientConstants.EXE_RUN;
                        }        
                        
                        if (entity.equals("runCombi")) {
                            _executionMode = ClientConstants.EXE_RUN_COMBI;
                        }        
                        
                        
                        if(entity.equals("numCars")) {
				numCars = Integer.parseInt(value);
				if (numCars < 0) {
					System.out.println(entity + ":" + value
							+ " is not a valid option");
					System.exit(0);
				}
			}
		}
	}

        //NORMAL DRIVING, NO ENDS
        public void go()
        {
            SocketHandler mySocket = new SocketHandler(host, port, verbose);
            String inMsg;
            
            long curEpisode = 0;
            boolean shutdownOccurred = false;
            do {

                /*
                 * Client identification
                 */

                do {
                    mySocket.send(clientId);
                    inMsg = mySocket.receive(UDP_TIMEOUT);
                } while (inMsg == null || inMsg.indexOf("***identified***") >= 0);

                /*
                 * Start to drive
                 */
                long currStep = 0;
                while (true) {
                    /*
                     * Receives from TORCS the game state
                     */
                    inMsg = mySocket.receive(UDP_TIMEOUT);

                    if (inMsg != null) {

                        /*
                         * Check if race is ended (shutdown)
                         */
                        if (inMsg.indexOf("***shutdown***") >= 0) {
                            shutdownOccurred = true;
                            System.out.println("Server shutdown!");
                            break;
                        }

                        /*
                         * Check if race is restarted
                         */
                        if (inMsg.indexOf("***restart***") >= 0) {
                            _car.reset();
                            if (verbose) {
                                System.out.println("Server restarting!");
                            }
                            break;
                        }

                        Action action = new Action();
                        if (currStep < maxSteps || maxSteps == 0) {
                            action = _car.control(new MessageBasedSensorModel(inMsg));
                        } else {
                            action.restartRace = true;
                        }
                        currStep++;
                        mySocket.send(action.toString());
                    } else {
                        System.out.println("Server did not respond within the timeout");
                    }
                }

            } while (++curEpisode < maxEpisodes && !shutdownOccurred);

            /*
             * Shutdown the controller
             */
            _car.shutdown();
            mySocket.close();
            System.out.println("Client shutdown.");
            System.out.println("Bye, bye!");
        }
        
	private static DiegoController load(String name) {
		DiegoController controller=null;
		try {
			controller = (DiegoController) (Object) Class.forName(name)
					.newInstance();
		} catch (ClassNotFoundException e) {
			System.out
					.println(name
							+ " is not a class name");
			System.exit(0);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return controller;
	}

        public void parseAndLoad(String[] args)
        {
            DiegoFMOClient.parseParameters(args);
            
            //CAR AND DRIVER
            _car = load(args[0]);
            _driver = new DriverManager(_car, this);
            _car.setDriverManager(_driver);
        }
        
        public void evaluate(int port)
        {
            SocketHandler mySocket = new SocketHandler(host, port, verbose);
            String inMsg;

            long curEpisode = 0;
            boolean shutdownOccurred = false;
            boolean endBySteps = true;
            double endDistance = 0;

            if(maxSteps == 0)
            {
                endBySteps = false;
                endDistance = EvolutionConstants.DISTANCE[port - 3001];
            }
            
            
            do {
                mySocket.send(clientId);
                inMsg = mySocket.receive(UDP_TIMEOUT);
            } while (inMsg == null || inMsg.indexOf("***identified***") >= 0);

            long currStep = 0;
            while (true) {
         
                inMsg = mySocket.receive(UDP_TIMEOUT);
                if (inMsg != null) {

                    if (inMsg.indexOf("***shutdown***") >= 0) {
                        shutdownOccurred = true;
                        System.out.println("Server shutdown!");
                        break;
                    }

                    //Check if race is restarted
                    if (inMsg.indexOf("***restart***") >= 0) {
                        _car.reset();
                        if (verbose) {
                            System.out.println("Server restarting!");
                        }
                        break;
                    }

                    Action action = new Action();
                    MessageBasedSensorModel sens = new MessageBasedSensorModel(inMsg); 
                    double distRaced = sens.getDistanceRaced();
                    if ((endBySteps && currStep >= maxSteps) || (!endBySteps && distRaced >= endDistance) ) {
                        action.restartRace = true;
                    } else {
                        action = _car.control(sens);
                    }
                    
                    currStep++;
                    mySocket.send(action.toString());
                } else {
                    System.out.println("Server did not respond within the timeout");
                }
            }
        }

        public void run()
        {
            switch(_executionMode)
            {
                case ClientConstants.EXE_GO:
                    go();
                    break;
                case ClientConstants.EXE_GA:
                    _driver.startGA();
                    break;
                case ClientConstants.EXE_WRITE_BASE_FILE:
                    _driver.createBaseFile();
                    break;
                case ClientConstants.EXE_WRITE_GRID_FILES:
                    _driver.createGridFiles();
                    break;
                case ClientConstants.EXE_RUN:
                    _driver.run();
                    break;
                case ClientConstants.EXE_RUN_COMBI:
                    _driver.runCombi();
                    break;
            }
            
            
        }

        
	/**
	 * @param args
	 *            is used to define all the options of the client.
	 *            <port:N> is used to specify the port for the connection (default is 3001)
	 *            <host:ADDRESS> is used to specify the address of the host where the server is running (default is localhost)  
	 *            <id:ClientID> is used to specify the ID of the client sent to the server (default is championship2009) 
	 *            <verbose:on> is used to set verbose mode on (default is off)
	 *            <maxEpisodes:N> is used to set the number of episodes (default is 1)
	 *            <maxSteps:N> is used to set the max number of steps for each episode (0 is default value, that means unlimited number of steps)
	 *            - 
	 */
	public static void main(String[] args) {
            DiegoFMOClient dfmo = new DiegoFMOClient();
            //dfmo.go(args);
            dfmo.parseAndLoad(args);
            //dfmo.go();
            dfmo.run();
            
            
	}
}
