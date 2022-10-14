import { useAuth } from "react-oidc-context";
import React from "react";

const PrivateRoute = ({ children }: any) => {
    const auth = useAuth();

    const isLoggedIn = auth.user != null;

    // return children;
    return isLoggedIn ? children : (
        <div>
            <h1 className="m-5 text-red-600 text-3xl">Access Denied to this content!</h1>
            <p className="m-5 text-black text-md">Please <button className="text-gray-400 hover:text-gray-800" onClick={() => auth.signinRedirect()}>log in</button> if you want to be able to view this page</p>
        </div>
    );
};

export default PrivateRoute;
