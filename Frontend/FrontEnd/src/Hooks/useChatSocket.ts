import { useCallback, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import type { IFrame, IMessage } from "@stomp/stompjs";


interface ChatMessage {
  sender: string;
  content: string;
  type: "JOIN" | "CHAT" | "LEAVE";
}

export default function useChatSocket(username: string) {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const stompClient = useRef<Client | null>(null);

  // 🔌 Connect WebSocket
  const connect = useCallback(() => {
    console.log(username)
    const token = localStorage.getItem("accessToken");

    if(!token){
      console.log("❌ No accessToken found in localStorage");
      return;
    }

    const client = new Client({
      brokerURL: `ws://localhost:8080/ws?accessToken=${token}`,
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });

    // Attach token for STOMP CONNECT frame
    client.connectHeaders = {
      Authorization: `Bearer ${token}`,
    };

    client.onConnect = () => {
      console.log("WS Connected ✔️");

      client.subscribe("/topic/public", (msg: IMessage) => {
        const body: ChatMessage = JSON.parse(msg.body);
        setMessages((prev) => [...prev, body]);
      });

      client.publish({
        destination: "/app/chat.addUser",
        body: JSON.stringify({ sender: username, type: "JOIN" }),
      });
    };

    // ✨ Very important logs
    client.onStompError = (frame: IFrame) => {
      console.error("❌ STOMP ERROR:", frame);
    };

    client.onWebSocketClose = (evt) => {
      console.warn("⚠️ WebSocket closed:", evt.reason || evt.code);
    };

    client.onWebSocketError = (evt) => {
      console.error("❌ WebSocket transport error:", evt);
    };

    stompClient.current = client;
    client.activate();
  },[username]);

  // 📩 SEND MESSAGE
   const sendMessage = useCallback(
    (content: string) => {
      if (!content || !stompClient.current) return;

      const msg: ChatMessage = {
        sender: username,
        content,
        type: "CHAT",
      };

      stompClient.current.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify(msg),
      });
    },
    [username]
  );

  // ❌ DISCONNECT
  const disconnect = useCallback(() => {
    if(stompClient.current){
      stompClient.current.deactivate();
      stompClient.current = null;
      console.log("WS DISCONNECTED ❌");
    }
  },[]);

  return { connect, disconnect, messages, sendMessage };
}
