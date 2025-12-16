import { jwtDecode } from "jwt-decode";
import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";

interface TokenPayload {
  exp: number;
}

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const [isValid, setIsValid] = useState<boolean | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");

    // Use small delay to avoid synchronous state updates warning
    setTimeout(() => {
      if (!token) {
        setIsValid(false);
        return;
      }

      try {
        const decoded = jwtDecode<TokenPayload>(token);
        const now = Math.floor(Date.now() / 1000);

        if (decoded.exp < now) {
          localStorage.removeItem("accessToken");
          setIsValid(false);
        } else {
          setIsValid(true);
        }
      } catch {
        localStorage.removeItem("accessToken");
        setIsValid(false);
      }
    }, 0);
  }, []);

  // Still loading → show nothing
  if (isValid === null) {
    return null;
  }

  // Not logged in → redirect
  if (!isValid) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;
