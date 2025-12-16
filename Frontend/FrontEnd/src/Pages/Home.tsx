import axios from "axios"
import { useState } from "react"
import { useNavigate } from "react-router-dom";
import api from "../api/axiosClient";

const Home = () => {

  const [userName,setUsername] = useState("");
  const nav = useNavigate();

  const enterChat = async()=>{

    const token = localStorage.getItem("accessToken");

    if (!token) {
      alert("Session expired. Please login again.");
      nav("/login");
      return;
    }

    try{
      // const res = await axios.post("http://localhost:8080/profile/checkUser",
      //   { userName },
      //   {
      //     headers: {
      //       Authorization: `Bearer ${token}`
      //     }
      //   });

      const res = await api.post("/profile/checkUser", { userName },{headers:{Authorization:`Bearer ${token}`}});

      console.log("userCheck Response:",res.data);
      if(res.data.success){
        console.log("Found")
        nav("/chat", { state: { userName } });
      }else{
        alert(res.data?.message || "Wrong UserName");
      }
    }catch(error){
      if(axios.isAxiosError(error)){
        console.log("userName Error:", error.response?.data);
        alert(error.response?.data?.message || "Login failed");
        if(error.response?.status === 401 || error.response?.status === 403){
          localStorage.removeItem("accessToken");
          nav("/login");
        }
        return;
      }else{
        console.error("Unexpected error:", error);
      }
    }
  }

  return (
    <div className="bg-black h-svh w-full flex-col p-5 flex justify-center items-center">
      <div className="w-1/3">
      <p className="text- text-white font-mono font-light">Enter UserName to talk...</p>
        <input className='bg-gray-900 p-1 w-full border-none focus:outline-none focus:border-none text-center border text-white ' 
          type="Enter UserName"
          placeholder="UserName"
          value={userName}
          onChange={(e) => setUsername(e.target.value)}
        />
        
        <button className="bg-red-700 hover:bg-gray-950 rounded-sm w-full text-amber-400 mt-1 hover:text-amber-200 font-extralight font-mono p-1 font-sm duration-700 ease-in" 
          onClick={enterChat}
        >
          ENTER
        </button>
      </div>
    </div>
  )
}

export default Home
