package client;

import com.google.gson.Gson;
import dto.ClientRequest;
import dto.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Console client for the Gospel Music Catalogue.
 * Connects to the server via socket and sends JSON requests.
 * @author D00276269
 */
public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = 5001;
    private final Gson gson = new Gson();

    public void start() {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to Gospel Music Catalogue Server");

            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        sendRequest(writer, reader, "GET_ALL_SONGS", null);
                        break;

                    case "2":
                        System.out.print("Enter song ID: ");
                        String songId = scanner.nextLine().trim();
                        sendRequest(writer, reader, "GET_SONG_BY_ID", "{\"id\":" + songId + "}");
                        break;

                    case "3":
                        System.out.print("Title: ");
                        String title = scanner.nextLine().trim();
                        System.out.print("Artist: ");
                        String artist = scanner.nextLine().trim();
                        System.out.print("Album: ");
                        String album = scanner.nextLine().trim();
                        System.out.print("Year released: ");
                        String year = scanner.nextLine().trim();
                        System.out.print("Duration (seconds): ");
                        String duration = scanner.nextLine().trim();
                        System.out.print("BPM: ");
                        String bpm = scanner.nextLine().trim();
                        String addPayload = "{\"title\":\"" + title + "\",\"artist\":\"" + artist + "\",\"album\":\"" + album + "\",\"yearReleased\":" + year + ",\"durationSeconds\":" + duration + ",\"bpm\":" + bpm + "}";
                        sendRequest(writer, reader, "ADD_SONG", addPayload);
                        break;

                    case "4":
                        System.out.print("Song ID to update: ");
                        String updateId = scanner.nextLine().trim();
                        System.out.print("New title: ");
                        String newTitle = scanner.nextLine().trim();
                        System.out.print("New artist: ");
                        String newArtist = scanner.nextLine().trim();
                        System.out.print("New album: ");
                        String newAlbum = scanner.nextLine().trim();
                        System.out.print("New year released: ");
                        String newYear = scanner.nextLine().trim();
                        System.out.print("New duration (seconds): ");
                        String newDuration = scanner.nextLine().trim();
                        System.out.print("New BPM: ");
                        String newBpm = scanner.nextLine().trim();
                        String updatePayload = "{\"id\":" + updateId + ",\"song\":{\"title\":\"" + newTitle + "\",\"artist\":\"" + newArtist + "\",\"album\":\"" + newAlbum + "\",\"yearReleased\":" + newYear + ",\"durationSeconds\":" + newDuration + ",\"bpm\":" + newBpm + "}}";
                        sendRequest(writer, reader, "UPDATE_SONG", updatePayload);
                        break;

                    case "5":
                        System.out.print("Song ID to delete: ");
                        String deleteId = scanner.nextLine().trim();
                        sendRequest(writer, reader, "DELETE_SONG", "{\"id\":" + deleteId + "}");
                        break;

                    case "6":
                        sendRequest(writer, reader, "GET_ALL_SINGERS", null);
                        break;

                    case "7":
                        sendRequest(writer, reader, "GET_ALL_PERFORMANCES", null);
                        break;

                    case "8":
                        sendRequest(writer, reader, "DISCONNECT", null);
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid option, try again.");
                }
            }

        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    private void sendRequest(PrintWriter writer, BufferedReader reader, String requestType, String payload) {
        try {
            ClientRequest request = new ClientRequest(requestType, payload);
            writer.println(gson.toJson(request));

            String responseJson = reader.readLine();
            ServerResponse response = gson.fromJson(responseJson, ServerResponse.class);

            System.out.println("\nStatus: " + response.getStatus());
            System.out.println("Message: " + response.getMessage());

            if (response.getData() != null) {
                String prettyJson = new com.google.gson.GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(response.getData());
                System.out.println("Data:\n" + prettyJson);
            }
            System.out.println();

        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }

    private void printMenu() {
        System.out.println("\n--- Gospel Music Catalogue ---");
        System.out.println("1. View all songs");
        System.out.println("2. Find song by ID");
        System.out.println("3. Add a song");
        System.out.println("4. Update a song");
        System.out.println("5. Delete a song");
        System.out.println("6. View all singers");
        System.out.println("7. View all performances");
        System.out.println("8. Disconnect");
        System.out.print("Choice: ");
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}