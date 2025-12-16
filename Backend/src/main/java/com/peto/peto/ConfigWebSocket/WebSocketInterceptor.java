package com.peto.peto.ConfigWebSocket;

import com.peto.peto.JwtFile.JwtUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        try {
            // Get Authorization header
            List<String> authHeaders = request.getHeaders().get("Authorization");
            String token = null;

            // Check Authorization header first
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                if (authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            // If no token in header, check query parameters
            if (token == null) {
                String query = request.getURI().getQuery();
                if (query != null) {
                    // Parse query parameters manually
                    String[] params = query.split("&");
                    for (String param : params) {
                        if (param.startsWith("accessToken=")) {
                            token = param.substring("accessToken=".length()); // Length of "accessToken="
                            break;
                        }
//                        if (param.startsWith("token=")) {
//                            token = param.substring(6); // Length of "token="
//                            break;
//                        }
                    }
                }
            }

            // Validate token
            if (token == null || token.isEmpty()) {
                System.err.println("WebSocket handshake rejected: No token provided");
                return false;
            }


            String username = jwtUtil.validateToken(token);

            if (username == null) {
                System.err.println("WebSocket handshake rejected: Invalid token");
                return false;
            }


            System.out.println("WebSocket handshake accepted for user: " + username);
            attributes.put("username", username);
            return true;

        } catch (Exception e) {
            System.err.println("Error during WebSocket handshake: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        if (exception != null) {
            System.err.println("After handshake error: " + exception.getMessage());
        }
    }
}



//public class WebSocketInterceptor implements HandshakeInterceptor {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//
////        List<String> authHeaders = request.getHeaders().get("Authorization");
//
//        //deepseek
//
//        try {
//            // Get Authorization header
//            List<String> authHeaders = request.getHeaders().get("Authorization");
//            String token = null;
//
//        //deepseek
//
//
//
//        if(authHeaders == null || authHeaders.isEmpty()){
//            String query = request.getURI().getQuery();
//            if (query != null && query.startsWith("token=")) {
//                authHeaders = List.of("Bearer " + query.substring(6));
//            }
//        }
//
//        if (authHeaders == null || authHeaders.isEmpty()) {
//            return false;
//        }
//
//        String token = authHeaders.get(0).substring(7);
//        String username = jwtUtil.validateToken(token);
//
//        if(username == null){
//            return false;
//        }
//
//        attributes.put("username",username);
//        return true;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {
//
//    }
//}
