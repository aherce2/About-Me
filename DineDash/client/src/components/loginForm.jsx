

import React from "react";
import { useState,useEffect } from 'react';
import axios from 'axios';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import 'bootstrap/dist/css/bootstrap.min.css'; 
import "./login.css"
import { Link } from 'react-router-dom';


const LoginForm = ({ onLoginSuccess }) => {
  const [userId, setUserId] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  
  useEffect(() => {
    // Update localStorage when userId or email changes
    localStorage.setItem('userId',userId);
    localStorage.setItem('email', email);

  }, [userId, email]);
  
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {

      const response = await axios.post('http://localhost:5000/api/login', {
        user_id: parseInt(userId, 10),
        email
      });

      const data = response.data;

      if (response.status === 200 && data.success) {
        onLoginSuccess();
      } else {
        setError(data.message || 'Invalid credentials');
      }
    } catch (err) {
      setError('An error occurred. Please try again.');
    }
  };

  return (
    <Form onSubmit={handleSubmit}>
      {/* UserID */}
      <Form.Group className="mb-3">
        <Form.Control
          type="text"
          className="form-control"
          placeholder="Enter UserID"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        />
      </Form.Group>
      
      {/* Email Address */}
      <Form.Group className="mb-3">
        <Form.Control
          type="email"
          className="form-control"
          placeholder="Enter Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
      </Form.Group>
      
      <Button className="button" variant="primary" type="submit">
        Login
      </Button>
      {error && <p className="text-danger">{error}</p>}
      <p className="forgot-password text-right">
        {/* Link href to API to create account */}
        New User? <Link to="/createaccount">Create Account</Link>
      </p>
      
    </Form>
  );
};

export default LoginForm;