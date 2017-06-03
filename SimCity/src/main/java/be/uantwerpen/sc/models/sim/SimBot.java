package be.uantwerpen.sc.models.sim;

import be.uantwerpen.sc.models.sim.messages.SimBotStatus;
import be.uantwerpen.sc.services.sockets.SimSocket;
import be.uantwerpen.sc.tools.Terminal;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Thomas on 03/04/2016.
 */
// Abstract class for each simulated bot
public abstract class SimBot implements Runnable
{
    private Thread simulationThread;
    protected boolean running;
    protected int id;
    protected String type;
    protected String name;
    public String ip;
    public int port;

    protected SimBot()
    {
        this.running = false;
        this.type = "bot";
        this.ip = "localhost";
        this.port = 1994;

        this.name = "SimBot";
    }

    public SimBot(String name)
    {
        this();

        this.name = name;
    }

    public void setServerCoreAddress(String serverIP, int serverPort)
    {
        this.ip = serverIP;
        this.port = serverPort;
    }

    public boolean create()
    {
        if(!sendCreate())
        {
            return false;
        }
        return true;
    }

    public boolean start()
    {
        if(this.running)
        {
            return false;
        }

        if(!sendStart()) {
            return false;
        }

        this.simulationThread = new Thread(this);

        this.simulationThread.start();

        this.running = true;

        return true;
    }

    public boolean restart()
    {
        if(simulationThread != null)
        {
            if(this.running)
            {
                this.running = false;
            }

            while(simulationThread.isAlive() && getType() == "car")
            {
                //Wait for thread to stop
            }
        }
        if(this.sendRestart())
        {
            this.running = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean stop()
    {
        if(!this.running || this.simulationThread == null)
        {
            return false;
        }

        if(this.running)
        {
            if(sendStop()) {
                this.running = false;
                return true;
            }
        }
        return false;
    }

    public boolean remove()
    {
        if(sendRemove())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean interrupt()
    {
        if(this.simulationThread == null)
        {
            return false;
        }

        if(this.simulationThread.isAlive())
        {
            this.simulationThread.interrupt();
            this.running = false;
        }

        return true;
    }

    public boolean isRunning()
    {
        return this.running;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getType()
    {
        return this.type;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public SimBotStatus getBotStatus()
    {
        String status;

        if(this.running)
        {
            status = "RUNNING";
        }
        else
        {
            status = "OFF";
        }

        SimBotStatus simBotStatus = new SimBotStatus(this.id, this.type, this.name, status);

        return simBotStatus;
    }

    public String getLog()
    {
        return "No logging available for this bot!";
    }

    @Override
    public void run()
    {
        while(this.running)
        {
            this.simulationProcess();
        }
    }

    public abstract int getStartPoint();

    public abstract Long getSimSpeed();

    public boolean parseProperty(String property, String value) throws Exception
    {
        switch(property.toLowerCase().trim())
        {
            case "name":
                setName(value);
                return true;
            default:
                return false;
        }
    }

    public boolean parseProperty(String property) throws Exception
    {
        switch(property.toLowerCase().trim())
        {
            case "name":
                return true;
            default:
                return false;
        }
    }

    public boolean printProperty(String property)
    {
        switch(property.toLowerCase().trim())
        {
            case "name":
                Terminal.printTerminalAppend(name + "\n");
                return true;
            default:
                return false;
        }
    }

    protected boolean sendCreate()
    {
        //Create socket connection to corresponding core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("create " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when bot is successfully created in his core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Create acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Bot could not be created.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }
        return true;
    }

    protected boolean sendStart() {
        //Create socket connection to corresponding Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("run " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //System.out.println(response);
            //Receive ACK when bot is successfully started in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Start acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Startpoint property was not set.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }
        return true;
    }

    protected boolean sendStop() {
        //Create socket connection to corresponding Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("stop " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when bot is successfully stopped in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Stop acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Bot could not be stopped.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }
        return true;
    }

    protected boolean sendRestart() {
        //Create socket connection to corresponding Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("restart " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when bot is successfully restarted in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Restart acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Bot could not be restarted.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }
        return true;
    }

    protected boolean sendRemove() {
        //Create socket connection to corresponding Core
        try {
            SimSocket simSocket = new SimSocket(new Socket(this.ip, this.port));
            simSocket.setTimeOut(500);

            //Send data over socket
            simSocket.sendMessage("kill " + id + "\n");

            String response = simSocket.getMessage();
            while(response == null)
            {
                response = simSocket.getMessage();
            }
            //Receive ACK when bot is successfully removed in the core
            if(response.equalsIgnoreCase("ACK")) {
                System.out.println("Remove acknowledge received.");
                simSocket.close();
            } else if(response.equalsIgnoreCase("NACK")) {
                System.out.println("NACK received. Bot could not be removed.");
                simSocket.close();
                return false;
            } else {
                System.out.println("Unknown response received. Bot was stopped.");
                simSocket.close();
                this.stop();
                return false;
            }
        } catch (IOException e) {
            System.out.println("I/O exception occurred!");
            this.stop();
            return false;
        }
        return true;
    }

    abstract protected void simulationProcess();
}