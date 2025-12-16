import React, { useState } from "react";

type MessageType = "CHAT" | "JOIN" | "LEAVE";

interface ChatMessage {
  sender: string;
  content: string;
  type: MessageType;
}

interface ChatWindowProps {
  messages: ChatMessage[];
  onSend: (content: string) => void;
}

const ChatWindow: React.FC<ChatWindowProps> = ({ messages, onSend }) => {
  const [input, setInput] = useState("");

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (input.trim() === "") return;

    onSend(input);
    setInput(""); // Clear input
  };

  return (
    <div className="p-4 bg-black h-screen text-white flex flex-col">
      <div className="flex-1 overflow-y-auto bg-gray-900 p-4 mb-3 rounded">
        {messages.map((msg, i) => (
          <div key={i} className="mb-2">
            {msg.type === "CHAT" ? (
              <>
                <strong>{msg.sender || "Unknown"}: </strong>{" "}
                <span>{msg.content}</span>
              </>
            ) : (
              <em className="text-gray-400">
                {msg.sender}{" "}
                {msg.type === "JOIN" ? "joined" : "left"} the chat
              </em>
            )}
          </div>
        ))}
      </div>

      <form onSubmit={handleSend} className="flex gap-2">
        <input
          className="flex-1 p-2 bg-gray-800 rounded"
          placeholder="Type here..."
          value={input}
          onChange={(e) => setInput(e.target.value)}
        />
        <button type="submit" className="px-4 bg-blue-600 rounded">
          Send
        </button>
      </form>
    </div>
  );
};

export default ChatWindow;
