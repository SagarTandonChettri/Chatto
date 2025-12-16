import { useEffect } from 'react'
import useChatSocket from '../Hooks/useChatSocket';
import ChatWindow from '../Components/ChatWindow';
import { useLocation } from 'react-router-dom';



export default function ChatPage() {

  const location = useLocation();
  const username = location.state?.username;
  const { connect, disconnect, messages, sendMessage } = useChatSocket(username);


  useEffect(() => {
    connect();
    return () => {disconnect();};
  },[connect, disconnect]);

  return <ChatWindow messages={messages} onSend={sendMessage} />;
}
