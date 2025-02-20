package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;

public class Myftpserver {
    private ServerSocket serverSocket;


    public Myftpserver(int portNumber) {
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Create a new thread to handle the client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Myftpserver ftpServer = new Myftpserver(8000);
        ftpServer.startServer();
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String currentDirectory;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.currentDirectory = "/home/myid/sg27405";   //initialize your default directory in the server
    }


    private void sendResponse(DataOutputStream output, String response) {
        try {
            // Send response to the client
            output.writeBytes(response + "\r\n");
            // Flush the output stream to ensure the response is sent immediately
            //output.flush();
        } catch (IOException e) {
            System.err.println("Error sending response to client: " + e.getMessage());
        }
    }


    private void receiveFile(String filename, DataInputStream input, DataOutputStream output) {
        try {
            // Set the path where the file will be saved on the server
            String filePath = currentDirectory + File.separator + filename;
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            // Create a FileOutputStream to write the incoming file data
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            // Read from the input stream and write to the file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            // Close the FileOutputStream
            fileOutputStream.close();

            // Send a response indicating successful file transfer
            sendResponse(output, "226 File transferred successfully: " + filename);
        } catch (IOException e) {
            // Handle exceptions related to file I/O or send an appropriate error response
            e.printStackTrace();
            sendResponse(output, "550 File transfer failed: " + e.getMessage());
        }
    }


    private void sendFile(String filename, DataOutputStream output) {
        try {
            File file = new File(filename);
            System.out.println(file);
            if (file.exists() && file.isFile()) {
                //System.out.println("FILE EXISTS");
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;

                // Send a response indicating the start of the file transfer
                //sendResponse(output, "150 Opening data connection");
                System.out.println("STARTING FILE TRANSFER");
                // Read and send the file content to the client
                try{
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    //System.out.println(output);
                    output.write(buffer, 0, bytesRead);
                }

                // Close the file input stream
                fileInputStream.close();}
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                // Send a response indicating successful file transfer
                sendResponse(output, "226 File transferred successfully");
            } else {
                // Send a response indicating that the file doesn't exist
                sendResponse(output, "550 File not found");
            }
        } catch (IOException e) {
            // Handle exceptions related to file I/O or send an appropriate error response
            System.err.println("Error sending file: " + e.getMessage());
            sendResponse(output, "550 File transfer failed");
        }
    }




    private StringBuilder accumulatedResponse = new StringBuilder();


    @Override
    public void run() {
        // Implement FTP server logic here
        // You can create a separate class or method for handling client requests
        // For simplicity, let's just print a message indicating the client connection

        try {
            // Handle client communication here
            System.out.println("Handling client in a separate thread: " + clientSocket.getInetAddress());
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            //sendResponse(output, "220 Welcome to FTP server");
            String clientMessage;

            //INCOMPLETE
            String command = null;

            // Continuously receive commands from the client until "quit" is received
            while ((clientMessage = input.readLine()) != null && !clientMessage.equalsIgnoreCase("quit")) {
                // Process the received command

                    // Receive and return command from the client
                    command = clientMessage;

                    // Send an acknowledgment response to the client
                    //output.writeBytes("Received " + command + "\r\n");
                 /*catch (IOException e) {
                    System.err.println("Error receiving command from client: " + e.getMessage());
                }*/
                if (command.startsWith("get")) {
                    // Extract the filename from the GET command
                    String[] parts = command.split("\\s+");
                    if (parts.length == 2) {
                        String filename = parts[1];
                        //System.out.println(filename);
                        // Implement logic to send the file content to the client
                        sendFile(filename, output);
                    } else {
                        sendResponse(output, "500 Invalid GET command format");
                        output.flush();
                    }
                }
                else if(command.equals("ls"))
                {
                    try {
                        // Get the list of files and directories in the current directory
                        File current = new File(currentDirectory);
                        File[] files = current.listFiles();
                        System.out.println(current + "is current directory");
                        // Send the list to the client
                        if (files != null) {
                            for (File file : files) {
                                output.writeBytes(file.getName() + "\r\n");
                            }
                                // Send response to the client
                            output.writeBytes("Successful\r\n");
                                // Flush the output stream to ensure the response is sent immediately
                                //output.flush();

                            //sendResponse(output, "226 List of files sent successfully");
                        } else {
                            output.writeBytes("List of files sent unsuccessfully \r\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendResponse(output, "Error listing files");
                    }

                }
                else if(command.startsWith("cd "))
                {
                    String directoryName = command.substring(3).trim();
                    try {
                        File newDirectory = new File(currentDirectory, directoryName);
                        if (newDirectory.exists() && newDirectory.isDirectory()) {
                            currentDirectory = newDirectory.getCanonicalPath();

                            sendResponse(output, "Directory changed to " + currentDirectory);
                        } else {
                            sendResponse(output, "Directory not found");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendResponse(output, "Error changing directory");
                    }

                }
                else if (command.startsWith("mkdir ")) {
                    String directoryName = command.substring(6).trim();
                    try {
                        File newDirectory = new File(currentDirectory, directoryName);

                        if (!newDirectory.exists()) {
                            if (newDirectory.mkdir()) {
                                sendResponse(output, "Directory created successfully: " + newDirectory.getCanonicalPath());
                            } else {
                                sendResponse(output, "Failed to create directory");
                            }
                        } else {
                            sendResponse(output, "Directory already exists");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendResponse(output, "550 Error creating directory");
                    }

                }
                else if(command.equals("pwd"))
                {
                    try {
                        sendResponse(output,   currentDirectory );
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendResponse(output, "550 Error getting current working directory");
                    }

                }
                else if(command.startsWith("delete "))
                {
                    String filename = command.substring(7).trim();
                    try {
                        File fileToDelete = new File(currentDirectory, filename);
                        output.writeBytes(String.valueOf(fileToDelete));
                        System.out.println("File to delete is: "+ fileToDelete);
                        if (fileToDelete.exists() && fileToDelete.isFile()) {
                            if (fileToDelete.delete()) {
                                sendResponse(output, "File deleted successfully");
                            } else {
                                sendResponse(output, "Failed to delete file");
                            }
                        } else {
                            sendResponse(output, "File not found");
                        }
                    }
                     catch (Exception e) {
                        e.printStackTrace();
                        sendResponse(output, "550 Error deleting file");
                    }

                }
                else if (command.startsWith("put ")) {
                    String filename = command.substring(4).trim();
                    DataInputStream input1= new DataInputStream(clientSocket.getInputStream());
                    receiveFile(filename, input1, output);

                }



            }

            // Send the accumulated response to the client


            // Close the client socket
            clientSocket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }

