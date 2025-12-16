import axios from "axios";
import { useState } from "react";


const Register = () => {

        const [email, setEmail] = useState("");
        const [username, setUsername] = useState("");
        const [password, setPassword] = useState("");

        const registerUser = async () => {
            try {
                console.log("User Data:",{email,username,password})

                const res = await axios.post("http://localhost:8080/profile/register", {
                    email,
                    username,
                    password
                });

                console.log("REGISTER RESPONSE:", res.data);
                // navigate("/login") if you want
            } catch (error) {
                if (axios.isAxiosError(error)) {
                    console.log("REGISTER ERROR:", error.response?.data);
                    alert(error.response?.data?.data?.join("\n"));
                    return;
                }

                console.error("Unexpected error:", error);
            }
    }


  return (
    <div className='bg-black h-svh w-full flex-col p-5 flex justify-center items-center'>
        <div className='p-1'>
            <h1 className='text-white text-2xl font-light'>Welcome.. to <span className='text-red-800 text-3xl font-mono font-medium hover:text-yellow-500 duration-800 ease-in'>Peto</span></h1>
            <h2 className="text-amber-100 font-bold font-mono text-center text-5xl hover:text-amber-400 ease-in duration-400">Join Us</h2>
            <p className="text-sm p-0.5  text-center font-light text-gray-400">I Miss You ... Talk to me.</p>
        </div>
        <div className='p-1'>
            <div className="p-1 rounded-xl flex flex-col gap-1 w-80">
    
                <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="bg-amber-300 font-extralight font-mono  w-full text-center border-none focus:outline-none focus:border-none "
                />

                <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="bg-amber-300 w-full font-extralight font-mono  text-center border-none focus:outline-none focus:border-none  "
                />

                <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="bg-amber-300 w-full font-extralight font-mono text-center border-none focus:outline-none focus:border-none "
                />

                <button
                onClick={registerUser}
                className="bg-red-700 hover:bg-gray-950 rounded-sm text-amber-400 hover:text-amber-200 font-extralight font-mono p-1 font-sm duration-700 ease-in"
                >
                Register
                </button>

            </div>
        </div>
    </div>
  )
}

export default Register