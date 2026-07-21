package server;

import com.google.gson.Gson;
import dao.DaoFactory;
import dao.Dao;
import dto.ClientRequest;
import dto.ServerResponse;
import exception.EntityNotFoundException;
import model.Song;
import model.Singer;
import model.Performance;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Handles communication with a single connected client on a separate thread.
 * Reads JSON requests, processes them via the DAO layer, and sends back JSON responses.
 * @author D00276269
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Gson gson;
    private final Dao<Song, Integer> songDao;
    private final Dao<Singer, Integer> singerDao;
    private final Dao<Performance, Integer> performanceDao;

    /**
     * Constructs a ClientHandler for the given socket.
     * @param clientSocket the connected client socket
     */
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.gson = new Gson();
        System.out.println("Initialising DAOs...");
        this.songDao = DaoFactory.getSongDao();
        this.singerDao = DaoFactory.getSingerDao();
        this.performanceDao = DaoFactory.getPerformanceDao();
        System.out.println("DAOs initialised successfully.");
    }

    /**
     * Runs the client handler — reads requests and sends responses until client disconnects.
     */
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            String requestJson;
            while ((requestJson = reader.readLine()) != null) {
                System.out.println("Received: " + requestJson);
                String responseJson = handleRequest(requestJson);
                writer.println(responseJson);
            }

        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    /**
     * Processes a JSON request string and returns a JSON response string.
     * @param requestJson the raw JSON request from the client
     * @return JSON response string
     */
    private String handleRequest(String requestJson) {
        try {
            ClientRequest request = gson.fromJson(requestJson, ClientRequest.class);
            String requestType = request.getRequestType();

            switch (requestType) {
                case "GET_ALL_SONGS":
                    return handleGetAllSongs();
                case "GET_SONG_BY_ID":
                    return handleGetSongById(request.getPayload());
                case "ADD_SONG":
                    return handleAddSong(request.getPayload());
                case "UPDATE_SONG":
                    return handleUpdateSong(request.getPayload());
                case "DELETE_SONG":
                    return handleDeleteSong(request.getPayload());
                case "GET_ALL_SINGERS":
                    return handleGetAllSingers();
                case "GET_ALL_PERFORMANCES":
                    return handleGetAllPerformances();
                case "DISCONNECT":
                    System.out.println("Client requested disconnect.");
                    return gson.toJson(new ServerResponse<>("OK", "Disconnected successfully", null));
                default:
                    return gson.toJson(new ServerResponse<>("ERROR", "Unknown request type: " + requestType, null));
            }

        } catch (EntityNotFoundException e) {
            return gson.toJson(new ServerResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return gson.toJson(new ServerResponse<>("ERROR", "Server error: " + e.getMessage(), null));
        }
    }

    // ---- Song Handlers ----

    private String handleGetAllSongs() {
        List<Song> songs = songDao.getAll();
        return gson.toJson(new ServerResponse<>("OK", "Retrieved " + songs.size() + " songs", songs));
    }

    private String handleGetSongById(String payload) {
        int id = gson.fromJson(payload, IdPayload.class).id;
        Song song = songDao.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Song", id));
        return gson.toJson(new ServerResponse<>("OK", "Song retrieved successfully", song));
    }

    private String handleAddSong(String payload) {
        Song song = gson.fromJson(payload, Song.class);
        Song inserted = songDao.insert(song);
        return gson.toJson(new ServerResponse<>("OK", "Song added successfully", inserted));
    }

    private String handleUpdateSong(String payload) {
        SongUpdatePayload songUpdate = gson.fromJson(payload, SongUpdatePayload.class);
        Song updated = songDao.update(songUpdate.id, songUpdate.song);
        return gson.toJson(new ServerResponse<>("OK", "Song updated successfully", updated));
    }

    private String handleDeleteSong(String payload) {
        int id = gson.fromJson(payload, IdPayload.class).id;
        boolean deleted = songDao.deleteById(id);
        if (deleted) {
            return gson.toJson(new ServerResponse<>("OK", "Song deleted successfully", null));
        } else {
            return gson.toJson(new ServerResponse<>("ERROR", "Song not found for id " + id, null));
        }
    }

    // ---- Singer Handlers ----

    private String handleGetAllSingers() {
        List<Singer> singers = singerDao.getAll();
        return gson.toJson(new ServerResponse<>("OK", "Retrieved " + singers.size() + " singers", singers));
    }

    // ---- Performance Handlers ----

    private String handleGetAllPerformances() {
        List<Performance> performances = performanceDao.getAll();
        return gson.toJson(new ServerResponse<>("OK", "Retrieved " + performances.size() + " performances", performances));
    }

    // ---- Inner helper classes for payload parsing ----

    /**
     * Helper class for parsing id-only payloads.
     */
    private static class IdPayload {
        int id;
    }

    /**
     * Helper class for parsing song update payloads.
     */
    private static class SongUpdatePayload {
        int id;
        Song song;
    }
}