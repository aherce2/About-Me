

import React from 'react';
import LoginForm from "../components/loginForm";


// This component acts as a container for the login form
const LoginPage = ({ onLoginSuccess }) => {
  return (
    <div className="login-page">
      <div className="login-container">
        
        <h3>Welcome to DineDash</h3>
  
        <LoginForm onLoginSuccess={onLoginSuccess} />
      </div>
    </div>
  );
};

export default LoginPage;
