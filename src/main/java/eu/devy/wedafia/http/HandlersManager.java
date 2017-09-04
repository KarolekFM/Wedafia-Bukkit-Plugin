package eu.devy.wedafia.http;

import eu.devy.wedafia.Wedafia;
import eu.devy.wedafia.json.PlayerJSON;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.HttpHandler;

import org.json.simple.JSONObject;

import java.util.Objects;

public class HandlersManager {
    private static String api_key;

    public static void setWedafiaInstance(Wedafia wedafia) {
        api_key = wedafia.getConfig().getString("api_key");
        PlayerJSON.setOpenInvInstance(wedafia.openinv);
    }

    protected static HttpHandler getPlayerInvHandler() {
        return new HttpHandler() {
            public void service(Request request, Response response) throws Exception {
                String player_param = request.getParameter("player");
                String api_key_param = request.getParameter("api");

                JSONObject json = new JSONObject();

                if (Objects.equals(api_key, api_key_param)) {
                    if(player_param != null) {
                        json = PlayerJSON.getPlayeyInfo(player_param);
                    } else {
                        json.put("error", "Player not specified");
                    }
                } else {
                    json.put(api_key_param, api_key);
                    json.put("error", "Invalid API KEY");
                }

                response.setContentType("application/json");
                response.getWriter().write(json.toString());

            }
        };
    }
}
