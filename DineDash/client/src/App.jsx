import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import axios from 'axios';

import './App.css';
import LoginPage from "./pages/loginPage";
import NavigationBar from "./components/navBar.jsx";
import Orders from "./pages/orders.jsx";
import Home from "./pages/home.jsx";
import Restaurants from "./pages/restaurants.jsx";
import CreateAccPage from './pages/createAccPage';
import MenuItems from "./pages/menuItems.jsx"
import QueryResults from "./pages/queryResult.jsx"
function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(() => {
    // Initialize the login state based on localStorage
    return JSON.parse(localStorage.getItem('isLoggedIn')) || false;
  });
  const [ActivePage, setActivePage] = useState(true);
  const [RestaurantFiltersApplied, setRestaurantFiltersApplied] = useState(false);

  const fetchUserData = async (userId) => {
    try {
      const response = await axios.get(`http://localhost:5000/api/users/${userId}`);
      const userData = response.data;
      
      console.log(userData);
      localStorage.setItem('user', JSON.stringify(userData));

      // Obtain user zipcode as an individual item in local storage
      const userZip = userData.data[0].zipcode;
      const cleanedZip = userZip.trim();
      localStorage.setItem('userZip', cleanedZip);
      // console.log(`User Zipcode set to: ${cleanedZip}`);

      console.log('User data fetched and stored in localStorage:', userData);
    } catch (error) {
      console.error('Error fetching user data:', error);
      throw error; // Rethrow to handle in the calling function
    }
  };
  
  // const fetchUserData = async (userId) => {
  //   try {
  //     const response = await axios.get(`http://localhost:5000/api/users/${userId}`);
  //     const userData = response.data;
  
  //     console.log(userData);
  //     localStorage.setItem('user', JSON.stringify(userData));
  
  //     // Obtain user zipcode as an individual item in local storage
  //     const userZip = userData.data.UserInformation.zipcode;
  //     console.log(userZip);

  //     localStorage.setItem('userZip', userZip);
  //     console.log(`User Zipcode set to: ${userZip}`);
  //     console.log('User data fetched and stored in localStorage:', userData);
  //   } catch (error) {
  //     console.error('Error fetching user data:', error);
  //     throw error; // Rethrow to handle in the calling function
  //   }
  // };

  const fetchDefaultRestaurants = async (zipcode) => {
    try {
      const response = await axios.get(`http://localhost:5000/api/restaurants/${zipcode}`);
      const defaultRestaurants = response.data;

      // console.log(defaultRestaurants.success);

      if(defaultRestaurants.success){
        //add a message to the user
        const Results = {
          message: "Showing Restaurants Near You!",
          restaurants: defaultRestaurants.restaurants
        };
        localStorage.setItem('default_restaurants', JSON.stringify(Results));
        console.log('Restaurant Data fetched and stored in localStorage:', defaultRestaurants);

      }else if(!defaultRestaurants.sucess){
        console.log('No restaurants found for the ZIP code. Fetching top 50 restaurants.');
        const defaultResponses = await axios.get('http://localhost:5000/api/restaurants/default');
        const defaultResult = defaultResponses.data;

        const Results = {
          message: "No Restaurants Near You :( Showing Top 50 Results",
          restaurants: defaultResult.restaurants
        };
        localStorage.setItem('default_restaurants', JSON.stringify(Results));
        console.log('Restaurant Data fetched and stored in localStorage:', defaultResult);

      }
    } catch (error) {
      console.error('Error fetching restaurant data:', error);
      throw error; // Rethrow to handle in the calling function
    }
  };

  // Function to handle login success
  const handleLoginSuccess = async () => {
    try {
      // Retrieve the userId from localStorage
      const userId = localStorage.getItem('userId');
      const email = localStorage.getItem('email');
      console.log(`User logged in as: userId=${userId}, email=${email}`);

      if (userId) {
        // Fetch and store user data
        await fetchUserData(userId);

        // Retrieve zipcode from localStorage and fetch restaurant data
        const zipcode = localStorage.getItem('userZip');
        console.log(`Retrieved zipcode: ${zipcode}`);

        if (zipcode) {
          await fetchDefaultRestaurants(zipcode);
        }else{
          console.log("Zipcode Invalid could not retrieve Restaurants in the Area");
          //fetch first 30 restaurants instead?
        }
        // Set the login state after data has been fetched
        setIsLoggedIn(true);
        localStorage.setItem('isLoggedIn', 'true');
      } else {
        console.error('User ID not found in localStorage');
      }
    } catch (error) {
      console.error('An error occurred during login:', error);
    }
  };

  useEffect(() => {
    localStorage.setItem('isLoggedIn', isLoggedIn);
  }, [isLoggedIn]);

  const handleLogout = () => {
    const userId = localStorage.getItem('userId');
    const email = localStorage.getItem('email');
    console.log(`User logged out: userId=${userId}, email=${email}`);
    // Retrieve all keys from localStorage
    const allKeys = Object.keys(localStorage);

    // Filter out keys that are related to orderPlaced
    const orderPlacedKeys = allKeys.filter(key => key.startsWith('orderPlaced_'));
    
    // Clear all localStorage items except those related to orderPlaced
    localStorage.clear();
    orderPlacedKeys.forEach(key => {
      localStorage.setItem(key, true); // Re-add orderPlaced items
  });

  // Update the isLoggedIn state
  localStorage.setItem('isLoggedIn', 'false');
  setIsLoggedIn(false); // Ensure state is updated
  };

  const handleDelete = async (e) => {
    console.log('Deleting account & Data');
    e.preventDefault(); // Prevent the default form submission or button click behavior

    try {
      const userId = localStorage.getItem('userId');
      if (!userId) {
        console.error('User ID not found');
        return;
      }

      // Make a request to the server to delete the account
      const response = await axios.delete(`http://localhost:5000/api/delete/${userId}`);

      if (response.status === 200) {
        console.log('Account deleted successfully');
        localStorage.clear();
        setIsLoggedIn(false);
      } else {
        console.error('Failed to delete account');
      }
    } catch (error) {
      console.error('An error occurred while deleting the account:', error);
    }
  };

  return (
    <Router>
      {isLoggedIn && <NavigationBar RestaurantFiltersApplied={RestaurantFiltersApplied} setRestaurantFiltersApplied={setRestaurantFiltersApplied} ActivePage={ActivePage} setActivePage={setActivePage} onLogout={handleLogout} onDelete={handleDelete} />}
      <Routes className="App">
        <Route path="" element={isLoggedIn ? <Home RestaurantFiltersApplied={RestaurantFiltersApplied} setRestaurantFiltersApplied={setRestaurantFiltersApplied} setActivePage={setActivePage} /> : <Navigate to="/login" />} />
        <Route path="/login" element={!isLoggedIn ? <LoginPage onLoginSuccess={handleLoginSuccess} /> : <Navigate to="/" />} />
        <Route path="/createaccount" element={!isLoggedIn ? <CreateAccPage onLoginSuccess={handleLoginSuccess} /> : <Navigate to="/" />} />
        <Route path="/orders" element={isLoggedIn ? <Orders /> : <Navigate to="/login" />} />
        {/* <Route path="/menuItems" element={isLoggedIn ? <MenuItems setActivePage={setActivePage} /> : <Navigate to="/login" />} /> */}
        <Route path="/menuItems/:restaurantId" element={isLoggedIn ? <MenuItems setActivePage={setActivePage} /> : <Navigate to="/login" />} />
        <Route path="/queryResults/:queryId" element={isLoggedIn ? <QueryResults /> : <Navigate to="/login" />} />
        <Route path="/restaurants" element={isLoggedIn ? <Restaurants RestaurantFiltersApplied={RestaurantFiltersApplied} setRestaurantFiltersApplied={setRestaurantFiltersApplied} setActivePage={setActivePage} /> : <Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;