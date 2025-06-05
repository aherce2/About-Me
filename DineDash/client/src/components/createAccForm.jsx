

import React from "react";
import { useState,useEffect } from 'react';
import axios from 'axios';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import 'bootstrap/dist/css/bootstrap.min.css'; 
import "./login.css"
import { Link } from 'react-router-dom';


const CreateAccForm = ({ onLoginSuccess }) => {
  const [zipcode, setZipcode] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [userId, setUserId] = useState('');

  useEffect(() => {
    // Update localStorage when zipcode or email changes
    localStorage.setItem('zipcode', zipcode);
    localStorage.setItem('email', email);
  }, [zipcode, email, userId]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post('http://localhost:5000/api/createaccount', {
        zipcode,
        email
      });

      const data = response.data;

      if (response.status === 200 && data.success) {

        const newUserId = response.data.UserID;
        setUserId(newUserId);
        localStorage.setItem('userId', newUserId);
        console.log(`User created successfully: userId=${response.data.UserID}, email=${email}, zipcode=${zipcode}`);
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
      {/* Zipcode */}
      <Form.Group className="mb-3">
        <Form.Control
          type="text"
          className="form-control"
          placeholder="Enter Zipcode"
          value={zipcode}
          onChange={(e) => setZipcode(e.target.value)}
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
        Create Account
      </Button>
      {error && <p className="text-danger">{error}</p>}
      <p className="forgot-password text-right">
        {/* Link href to API to create account */}
        Already have an account? <Link to="/login">Login</Link>
      </p>
    </Form>
  );
};

export default CreateAccForm;