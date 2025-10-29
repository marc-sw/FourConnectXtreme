package cb;

import cb.bots.BotAi;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CustomWebsocketClient extends WebSocketClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final BotAi bot;

    public CustomWebsocketClient(URI serverUri, BotAi ai) {
        super(serverUri);
        bot = ai;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection to Four Connect Xtreme opened");
    }

    @Override
    public void onMessage(String s) {
        System.out.println("on message string");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        //long start = System.currentTimeMillis();
        System.out.println("on message bytes");
        try {
            String decodedMessage = new String(bytes.array(), StandardCharsets.UTF_8)
                    .replace("'", "\"");
            System.out.println(decodedMessage);
            PlayState response = mapper.readValue(decodedMessage, PlayState.class);
            //System.out.printf("parsing took: %dms%n", System.currentTimeMillis() - start);
            //start = System.currentTimeMillis();
            if (response.getBot().equals(bot.getName())) {
                Map<String, Object> responseMap = Map.of(
                        "state", "play",
                        "column", bot.play(response)
                );
                //System.out.printf("move evalutaion took: %dms%n", System.currentTimeMillis() - start);
                //start = System.currentTimeMillis();
                String jsonResponse = mapper.writeValueAsString(responseMap);
                send(jsonResponse);
                //System.out.printf("sending took: %dms%n", System.currentTimeMillis() - start);
            }
        } catch (Exception e) {
            if (e instanceof JsonParseException) {
                System.out.println("Got ping message");
            } else {
                e.printStackTrace(System.out);
            }
        }
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Verbindung zum Server geschlossen: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Fehler: " + ex.getMessage());
    }
}