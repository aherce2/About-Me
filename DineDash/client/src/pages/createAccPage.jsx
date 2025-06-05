
import React from 'react';
import CreateAccForm from '../components/createAccForm';

// This component acts as a container for the login form
const CreateAccPage = ({ onLoginSuccess }) => {
    return (
      <div className="login-page">
        <div className="login-container">
          
          <h3>Welcome to DineDash</h3>
          <p>Please create an account to continue</p>
          <CreateAccForm onLoginSuccess={onLoginSuccess} />
        </div>
      </div>
    );
  };
  
  export default CreateAccPage;