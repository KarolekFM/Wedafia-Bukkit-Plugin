package eu.devy.wedafia.http;

import eu.devy.wedafia.Wedafia;
import eu.devy.wedafia.json.PlayerJSON;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.HttpHandler;

import org.json.simple.JSONObject;

import java.util.Objects;
import java.util.Optional;

public class HandlersManager {
    private static String api_key;

    public static abstract class ApiCall {
        public abstract JSONObject run(Request request, JSONObject json);
    }

    public static void setWedafiaInstance(Wedafia wedafia) {
        api_key = wedafia.getConfig().getString("api_key");
        PlayerJSON.setOpenInvInstance(wedafia.openinv);
    }

    private static HttpHandler newApiHandler(ApiCall apiCall) {
        return new HttpHandler() {
            public void service(Request request, Response response) throws Exception {
                String api_key_param = request.getParameter("api");

                JSONObject json = new JSONObject();

                if (Objects.equals(api_key, api_key_param)) {
                    json = apiCall.run(request, json);
                } else {
                    json.put("error", "Invalid API KEY");
                }

                response.setContentType("application/json");
                response.getWriter().write(json.toString());
            }
        };
    }

    protected static HttpHandler getRootHandler() {
        ApiCall apiCall = new ApiCall() {
            @Override
            public JSONObject run(Request request, JSONObject json) {
                json.put("message", "OK");

                return json;
            }
        };

        return newApiHandler(apiCall);
    }

    protected static HttpHandler getPlayerHandler() {
        ApiCall apiCall = new ApiCall() {
            @Override
            public JSONObject run(Request request, JSONObject json) {
                String player_param = request.getParameter("player");

                if (player_param != null) {
                    json.put("content", PlayerJSON.getPlayer(player_param));
                } else {
                    json.put("error", "Player not specified");
                }

                return json;
            }
        };

        return newApiHandler(apiCall);
    }

    protected static HttpHandler getPlayerStatusHandler() {
        ApiCall apiCall = new ApiCall() {
            @Override
            public JSONObject run(Request request, JSONObject json) {
                String player_param = request.getParameter("player");

                if (player_param != null) {
                    json.put("content", PlayerJSON.getPlayerStatus(
                            Optional.of(player_param), Optional.empty()
                    ));
                } else {
                    json.put("error", "Player not specified");
                }

                return json;
            }
        };

        return newApiHandler(apiCall);
    }

    protected static HttpHandler getPlayerInvHandler() {
        ApiCall apiCall = new ApiCall() {
            @Override
            public JSONObject run(Request request, JSONObject json) {
                String player_param = request.getParameter("player");

                if (player_param != null) {
                    json.put("content", PlayerJSON.getPlayerInv(
                            Optional.of(player_param), Optional.empty()
                    ));
                } else {
                    json.put("error", "Player not specified");
                }

                return json;
            }
        };

        return newApiHandler(apiCall);
    }
}
