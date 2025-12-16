import axios from 'axios'

const api = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken");
    if(token){
        config.headers = config.headers || {};
        config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    (res) => res,
    async(err) => {
        const original = err.config;

        if(err.response?.status === 401 && !original._retry){
            original._retry = true;

            try{
                const refreshRes = await axios.post(
                    "http://localhost:8080/auth/refresh",
                    {},
                    {withCredentials:true}
                );

                const newAccessToken = refreshRes.data.accessToken;
                localStorage.setItem("accessToken",newAccessToken);

                original.headers["Authorization"] = "Bearer "+ newAccessToken;
                return api(original);
            } catch {
                console.error("Refresh Failed");
                localStorage.clear();
                window.localStorage.href = "/login";
            }
        }

        return Promise.reject(err);
    }
);

export default api;
