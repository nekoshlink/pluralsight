import React from "react";
import { useAuth } from "react-oidc-context";

const Nav = () => {
    const auth = useAuth();

    return (
        <div>
            <div className="top-0 w-full flex flex-wrap">
                <section className="x-auto">
                    <nav className="flex justify-between bg-gray-200 text-blue-800 w-screen">
                        <div className="px-5 xl:px-12 py-6 flex w-full items-center">
                            <h1 className="text-3xl font-bold font-heading">
                                NekoShlink Dashboard
                            </h1>
                            <ul className="hidden md:flex px-4 mx-auto font-semibold font-heading space-x-12">
                                <li>
                                    <a className="hover:text-blue-800" href="/">
                                        Home
                                    </a>
                                </li>
                                <li>
                                    <a className="hover:text-blue-800" href="/domains">
                                        Domains
                                    </a>
                                </li>
                                <li>
                                    <a className="hover:text-blue-800" href="/short-urls">
                                        Short URLs
                                    </a>
                                </li>
                                <li>
                                    <a className="hover:text-blue-800" href="/tags">
                                        Tags
                                    </a>
                                </li>
                            </ul>
                            <div className="hidden xl:flex items-center space-x-5">
                                <div className="hover:text-gray-200">
                                    {!auth.isAuthenticated && (
                                        <button
                                            type="button"
                                            className="text-blue-800"
                                            onClick={() => auth.signinRedirect()}
                                        >
                                            Login
                                        </button>
                                    )}

                                    {auth.isAuthenticated && (
                                        <button
                                            type="button"
                                            className="text-blue-800"
                                            onClick={() => auth.signoutRedirect({post_logout_redirect_uri: "http://localhost:3000"})}
                                        >
                                            Logout ({auth.user?.profile.preferred_username})
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    </nav>
                </section>
            </div>
        </div>
    );
};

export default Nav;
