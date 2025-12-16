import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";


const Login = () => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const nav = useNavigate();

    const loginUser = async () => {
        try {
            const res = await axios.post("http://localhost:8080/profile/login", {
                username,      // or username if your backend accepts it
                password
            });

            console.log("LOGIN RESPONSE:", res.data);

            if (res.data.success) {

              const token = res.data.data.token;
              localStorage.setItem("accessToken", token);
              
              console.log("ACCESS TOKEN SAVED:", token);
              
              console.log("enter");
              nav("/home"); // <--- redirect
            }else {
                alert(res.data?.message || "Login failed");
            }
        } catch (error) {
            if (axios.isAxiosError(error)) {
                    console.log("REGISTER ERROR:", error.response?.data);
                    alert(error.response?.data?.message || "Login failed");
                    return;
                }else{
                    console.error("Unexpected error:", error);
                }
        }
    };

  return (
    <div className='bg-black h-svh w-full flex-col p-5 flex justify-center items-center'>
      <div className=' p-1'>
        <h1 className='text-white text-2xl font-light'>Welcome.. to <span className='text-red-800 text-3xl font-medium hover:text-yellow-500 duration-800 ease-in'>Peto</span></h1>
      </div>
      <div className='bg-yellow-100 items-center p-1 flex flex-col'>
        <p className='font-mono'>Type Your UserName To Enter Chat</p>
        <input className='bg-amber-300 w-full text-center border ' 
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}/>

        <input className='bg-amber-300 w-full text-center border ' type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}/>
        
        <button
            onClick={loginUser}
             className="bg-red-700 hover:bg-gray-950 rounded-sm w-full text-amber-400 mt-1 hover:text-amber-200 font-extralight font-mono p-1 font-sm duration-700 ease-in" >
                Login
        </button>
      </div>
      <div className="hover:border-amber-400 mt-0.5 duration-400 ease-in">
        <button
            onClick={() => nav("/register")}
             className="bg-yellow-500 hover:bg-gray-950 rounded-sm w-full text-red-800 mt-1 hover:text-red-600 font-extralight font-mono p-1 font-sm duration-700 ease-in" >
                Register
        </button>
      </div>
    </div>
  )
}

export default Login
