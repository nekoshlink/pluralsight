import React from 'react';
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Nav from "./components/Nav";
import WelcomePage from "./pages/Homepage";
import Domains from "./pages/Domains";
import ShortUrls from "./pages/ShortUrls";
import Tags from "./pages/Tags";
import PrivateRoute from "./helpers/PrivateRoute";
import './App.css';

import { AuthProvider } from 'react-oidc-context';
import oidcConfig from './oidc.json';

function App() {
  return (
      <div>
          <AuthProvider {...oidcConfig}>
              <Nav />
              <BrowserRouter>
                  <Routes>
                      <Route path="/" element={<WelcomePage />} />
                      <Route path="/domains/*" element={<PrivateRoute><Domains /></PrivateRoute>} />
                      <Route path="/short-urls/*" element={<PrivateRoute><ShortUrls /></PrivateRoute>} />
                      <Route path="/tags/*" element={<PrivateRoute><Tags /></PrivateRoute>} />
                  </Routes>
              </BrowserRouter>
          </AuthProvider>
      </div>
  );
}

export default App;
