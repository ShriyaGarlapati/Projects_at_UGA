package src;

import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.net.Socket;

public class Myftp{
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String currentDirectory;
    public Myftp(String serverAddress, int serverPort) {
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + clientSocket.getInetAddress());

            // Initialize input and output streams for communication
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            // set default path to current directory currentDirectory = ""
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendCommand(String command) {
        try {
            // Send FTP command to the server
            writer.println(command);

            // Flush the writer to ensure the command is sent immediately
            writer.flush();

            System.out.println("Sent command to server: " + command);
        } catch (Exception e) {
            System.err.println("Error sending command to server: " + e.getMessage());
        }
    }

    /*public String receiveResponse() {
        try {
            // Receive and return response from the server
            String response = reader.readLine();
            if (response == null) {
                throw new IOException("Server closed the connection.");
            }
            return response;
        } catch (IOException e) {
            System.err.println("Error receiving response from server: " + e.getMessage());
            return null;
        }
    }*/

    // Add the following method to send a file to the server
    private void sendFile(String localFilePath, DataOutputStream output) {
        try {
            // Open the local file for reading
            FileInputStream fileInputStream = new FileInputStream(localFilePath);
            //System.out.println("Sendfile client: "+ localFilePath);
            // Read from the file and write to the output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                System.out.println("Client");
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
            // Close the FileInputStream
            fileInputStream.close();
        } catch (IOException e) {
            // Handle exceptions related to file I/O or send an appropriate error response
            e.printStackTrace();
            System.err.println("Error sending file: " + e.getMessage());
        }
    }

    public void startClient() {
        System.out.println("FTP client connected to the server.");
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Display a prompt and read user commands until "quit" is entered
            String userCommand;
            do {
                System.out.print("Enter FTP command (or 'quit' to exit): ");
                userCommand = userInput.readLine();
                //sendCommand(userCommand);
                if(userCommand.equalsIgnoreCase("quit"))
                    break;
                // Only handle the server response for the "get" command
                if (userCommand.startsWith("get")) {
                    sendCommand(userCommand);
                    String name= userCommand.substring(4).trim();
                    // Read and save the file received from the server
                    FileOutputStream fileOutputStream = new FileOutputStream(name);

                    InputStream inputStream = clientSocket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                     try {
                         while ((bytesRead = inputStream.read(buffer)) != -1) {
                             fileOutputStream.write(buffer, 0, bytesRead);
                         }

                         fileOutputStream.close();
                     }catch(Exception e)
                     {
                         e.printStackTrace();
                     }
                    System.out.println("File received and saved: " + name);
                }
                else if(userCommand.equals("ls"))
                {
                    try {
                        // Send the "ls" command to the server
                        sendCommand("ls");

                        // Receive and display the list of files and directories from the server
                        BufferedReader serverResponseReader = reader;
                        String line;
                        System.out.println("List of files and directories on the server:");
                        while ((line = serverResponseReader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (userCommand.startsWith("cd ")) {
                    // Handle the "cd" command
                    String directoryName = userCommand.substring(3).trim();

                        try {
                            // Send the "cd" command to the server
                            sendCommand(userCommand);

                            // Read and display the server's response
                            String response = reader.readLine();
                            System.out.println(response);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                // Update the startClient() method to handle mkdir command
                else if (userCommand.startsWith("mkdir ")) {
                    String directoryName = userCommand.substring(6).trim();
                    sendCommand(userCommand);
                    // Read and display the server's response for mkdir command
                    String response = reader.readLine();
                    System.out.println(response);
                }
                else if(userCommand.equals("pwd"))
                {
                    sendCommand(userCommand);
                    String response=reader.readLine();
                    System.out.println(currentDirectory);
                    System.out.println(response);
                }
                // Update the startClient() method to handle delete command
                else if (userCommand.startsWith("delete ")) {
                    String filename = userCommand.substring(7).trim();
                    sendCommand(userCommand);
                    // Read and display the server's response for delete command
                    String response = reader.readLine();
                    System.out.println(response);
                }
                // Handle "put" command
                else if (userCommand.startsWith("put ")) {
                    String[] parts = userCommand.split("\\s+", 2);
                    if (parts.length == 2) {
                        String localFilePath = parts[1];
                        String x= System.getProperty("user.dir");
                        System.out.println("The source file should be in the directory: "+ x);
                        File filename=new File(localFilePath);
                        // Read the server response
                        //String response = reader.readLine();
                        //System.out.println(response);
                        //System.out.println("filename at client " +currentDirectory);
                        // If the response indicates success, send the file
                        //if (response.startsWith("226")) {
                        sendCommand(userCommand);
                            sendFile(String.valueOf(filename), new DataOutputStream(clientSocket.getOutputStream()));
                        //}
                    } else {
                        System.out.println("Invalid command format: put <local_filename>");
                    }
                }



            }while(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopClient();
        }
    }

    public void stopClient() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Client disconnected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Myftp ftpClient = new Myftp("128.192.101.137", 8000);
        //Myftp ftpClient = new Myftp("localhost", 8080);
        ftpClient.startClient();
    }
}
