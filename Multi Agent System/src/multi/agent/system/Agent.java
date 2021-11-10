/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multi.agent.system;

import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.Socket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hamed Khashehchi
 */
public class Agent extends Thread {

    private String networkHost;
    private volatile static int PORT = 6400;
    private ServerSocket serverSocket;
    private int networkPort;
    private Socket socket;
    private ArrayList<Agent> neighbors;
    private ArrayList<Agent> connections;
    private int node;
    private volatile static int count;
    private String color;
    private ArrayList<String> possibleColors;
    private ArrayList<LocalView> localview;

    public void addLocalView(LocalView v) {
        this.localview.add(v);
    }

    public void addLocalView(String color, Agent a) {
        this.localview.add(new LocalView(color, a));
    }

    public void addColor(String s) {
        possibleColors.add(s);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.networkPort;
        hash = 17 * hash + this.node;
        hash = 17 * hash + Objects.hashCode(this.color);
        return hash;
    }

    @Override
    public String toString() {
        return "{node "+this.getNode()+", on thread "+super.toString()+", neighbors "+this.neighbors+", connections "+this.connections+", host "+this.getNetworkHost()+", port "+this.getNetworkPort()+", hash "+this.hashCode()+"}"; //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Agent other = (Agent) obj;
        if (this.networkPort != other.networkPort) {
            return false;
        }
        if (this.node != other.node) {
            return false;
        }
        if (!Objects.equals(this.networkHost, other.networkHost)) {
            return false;
        }
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        if (!Objects.equals(this.neighbors, other.neighbors)) {
            return false;
        }
        if (!Objects.equals(this.connections, other.connections)) {
            return false;
        }
        if (!Objects.equals(this.possibleColors, other.possibleColors)) {
            return false;
        }
        return true;
    }

    private void handle_ok(Agent a, LocalView v) {
        System.out.println("from " + node + " to " + a.getNode() + "\nTrying to Connect: " + a.getNetworkHost() + " at port: " + a.getNetworkPort());
        try {
            socket = new Socket(a.networkHost, a.networkPort);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            message = "message from " + this.node + " to " + a.getNode() + "\nnew local view:" + v;
            writer.println(message);
            writer.flush();
            System.out.println("Echo from server: " + reader.readLine());
            socket.close();
        } catch (IOException e) {
            System.out.println("IO EXCEPTION");
            System.out.println("e = " + e);
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            e.printStackTrace();
            return;
        }
    }

    public Agent(int node) throws IOException {
        this.node = node;
        networkHost = "localhost";
        networkPort = PORT++;
        neighbors = new ArrayList<>();
        possibleColors = new ArrayList<>();
        connections = new ArrayList<>();
        serverSocket = new ServerSocket(networkPort);
        localview = new ArrayList<>();
        Thread listen = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println("");
                        System.out.println("node " + Agent.this.node + " here\nWaiting for agents on port " + Agent.this.networkPort);
                        Socket client = serverSocket.accept();
                        ConnectionHandler handler = new ConnectionHandler(client);
                        handler.start();
                    }
                } catch (Exception ex) {
                    System.out.println("Connection error: " + ex);
                }
            }
        });
        listen.start();
    }

    public void addNeighbors(Agent neighbor) {
        this.neighbors.add(neighbor);
        addConnections(neighbor);
    }

    private void addConnections(Agent connection) {
        this.connections.add(connection);
    }

    public ArrayList<Agent> getConnections() {
        return connections;
    }

    public String getNetworkHost() {
        return networkHost;
    }

    public int getNetworkPort() {
        return networkPort;
    }

    @Override
    public void run() {
        while (true) {
            agentThread();
        }
    }

    public int getNode() {
        return node;
    }

    private synchronized void agentThread() {
        connections.forEach((t) -> {
            this.handle_ok(t, new LocalView("red", t));
        });
    }

    private class ConnectionHandler extends Thread {

        private Socket client;
        BufferedReader reader;
        PrintWriter writer;

        public ConnectionHandler(Socket client) {
            this.client = client;
            System.out.println("Got connection from " + client.getInetAddress() + ":" + client.getPort());
            count++;
            System.out.println("Active Connections = " + count);
        }

        public void run() {
            connections.forEach((t) -> {
                System.out.println("an");
                Agent.this.handle_ok(t, new LocalView("red", t));
            });
            String message = null;
            try {
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream());
                writer.println("Welcome to my server, I am node " + Agent.this.getNode());
                writer.flush();
                message = reader.readLine();
                while (!(message == null || message.equalsIgnoreCase("exit"))) {
                    writer.println(message);
                    writer.flush();
                    message = reader.readLine();
                }
                client.close();
                count--;
                System.out.println("Active Connections = " + count);
            } catch (Exception ex) {
                count--;
                System.out.println("Active Connections = " + count);
            }
        }
    }

}
